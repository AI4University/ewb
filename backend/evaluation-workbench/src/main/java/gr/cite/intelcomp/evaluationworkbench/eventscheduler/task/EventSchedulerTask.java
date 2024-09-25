package gr.cite.intelcomp.evaluationworkbench.eventscheduler.task;

import gr.cite.intelcomp.evaluationworkbench.common.enums.IsActive;
import gr.cite.intelcomp.evaluationworkbench.common.enums.ScheduledEventStatus;
import gr.cite.intelcomp.evaluationworkbench.common.scope.fake.FakeRequestScope;
import gr.cite.intelcomp.evaluationworkbench.data.ScheduledEventEntity;
import gr.cite.intelcomp.evaluationworkbench.eventscheduler.EventSchedulerProperties;
import gr.cite.intelcomp.evaluationworkbench.eventscheduler.processing.EventProcessingStatus;
import gr.cite.intelcomp.evaluationworkbench.query.ScheduledEventQuery;
import gr.cite.tools.data.query.Ordering;
import gr.cite.tools.logging.LoggerService;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class EventSchedulerTask {
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(EventSchedulerTask.class));

    private final ApplicationContext applicationContext;
    private final EventSchedulerProperties properties;

    public EventSchedulerTask(ApplicationContext applicationContext, EventSchedulerProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
        long intervalSeconds = properties.getTask().getProcessor().getIntervalSeconds();
        if (properties.getTask().getProcessor().getEnable() && intervalSeconds > 0) {
            logger.info("Task '{}' will be scheduled to run every {} seconds", properties.getTask().getName(), intervalSeconds);

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            //GK: Fixed rate is heavily unpredictable, and it will not scale well on a very heavy workload
            scheduler.scheduleWithFixedDelay(this::process, 10, intervalSeconds, TimeUnit.SECONDS);
        }
    }

    public void process() {
        logger.trace("Scheduled task running");
        EntityManagerFactory entityManagerFactory = applicationContext.getBean(EntityManagerFactory.class);
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try (FakeRequestScope ignored = new FakeRequestScope()) {
            Instant lastCandidateCreationTimestamp = null;
            while (true) {
                CandidateInfo candidateInfo = this.candidateEventToRun(lastCandidateCreationTimestamp, entityManager);
                if (candidateInfo == null) break;

                lastCandidateCreationTimestamp = candidateInfo.getCreatedAt();
                Boolean shouldOmit = this.shouldOmit(candidateInfo, entityManager);
                if (shouldOmit) {
                    continue;
                }
                Boolean shouldAwait = this.shouldWait(candidateInfo, entityManager);
                if (shouldAwait) {
                    continue;
                }
                this.handle(candidateInfo.getId(), entityManager);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            EntityTransaction transaction = null;
            if (entityManager != null) {
                transaction = entityManager.getTransaction();
            }
            if (transaction != null && transaction.isActive()) transaction.rollback();
        } finally {
            if (entityManager != null) {
                entityManager.close();
                logger.trace("Scheduled task finished");
            }
        }
    }

    private CandidateInfo candidateEventToRun(Instant lastCandidateNotificationCreationTimestamp, EntityManager entityManager) {
        CandidateInfo candidateInfo = null;
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        ScheduledEventQuery scheduledEventQuery = applicationContext.getBean(ScheduledEventQuery.class);
        ScheduledEventEntity candidate;

        scheduledEventQuery = scheduledEventQuery
                .isActives(IsActive.ACTIVE)
                .status(ScheduledEventStatus.PENDING, ScheduledEventStatus.ERROR, ScheduledEventStatus.PARKED)
                .retryThreshold(Math.toIntExact(this.properties.getTask().getProcessor().getOptions().getRetryThreshold()))
                .shouldRunBefore(Instant.now())
                .createdAfter(lastCandidateNotificationCreationTimestamp)
                .ordering(new Ordering().addAscending(ScheduledEventEntity._createdAt));
        candidate = scheduledEventQuery.first();
        if (candidate != null) {
            ScheduledEventStatus previousState = candidate.getStatus();
            candidate = entityManager.find(ScheduledEventEntity.class, candidate.getId());
            candidate.setStatus(ScheduledEventStatus.PROCESSING);
            candidate = entityManager.merge(candidate);
            entityManager.persist(candidate);
            entityManager.flush();

            candidateInfo = new CandidateInfo(candidate.getId(), previousState, candidate.getCreatedAt());
        }

        transaction.commit();
        return candidateInfo;
    }

    private Boolean shouldWait(CandidateInfo candidateInfo, EntityManager entityManager) {
        boolean shouldWait = false;
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        ScheduledEventEntity scheduledEventEntity = entityManager.find(ScheduledEventEntity.class, candidateInfo.getId());
        if (scheduledEventEntity.getRetryCount() != null && scheduledEventEntity.getRetryCount() >= 1) {
            int accumulatedRetry = 0;
            int pastAccumulateRetry = 0;
            EventSchedulerProperties.Task.Processor.Options options = properties.getTask().getProcessor().getOptions();
            for (int i = 1; i <= scheduledEventEntity.getRetryCount() + 1; i += 1)
                accumulatedRetry += (i * options.getRetryThreshold());
            for (int i = 1; i <= scheduledEventEntity.getRetryCount(); i += 1)
                pastAccumulateRetry += (i * options.getRetryThreshold());
            int randAccumulatedRetry = ThreadLocalRandom.current().nextInt((int) (accumulatedRetry / 2), accumulatedRetry + 1);
            long additionalTime = randAccumulatedRetry > options.getMaxRetryDelaySeconds() ? options.getMaxRetryDelaySeconds() : randAccumulatedRetry;
            long retry = pastAccumulateRetry + additionalTime;

            Instant retryOn = scheduledEventEntity.getCreatedAt().plusSeconds(retry);
            boolean itIsTime = retryOn.isBefore(Instant.now());

            if (!itIsTime) {
                scheduledEventEntity.setStatus(candidateInfo.getPreviousState());
                //notification.setUpdatedAt(Instant.now());
                scheduledEventEntity = entityManager.merge(scheduledEventEntity);
                entityManager.persist(scheduledEventEntity);

            }
            shouldWait = !itIsTime;
        }

        transaction.commit();
        return shouldWait;
    }

    private Boolean shouldOmit(CandidateInfo candidateInfo, EntityManager entityManager) {
        boolean shouldOmit = false;
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        ScheduledEventEntity scheduledEventEntity = entityManager.find(ScheduledEventEntity.class, candidateInfo.getId());
        long age = Instant.now().getEpochSecond() - scheduledEventEntity.getCreatedAt().getEpochSecond();
        long omitSeconds = properties.getTask().getProcessor().getOptions().getTooOldToHandleSeconds();
        if (age >= omitSeconds) {
            scheduledEventEntity.setStatus(ScheduledEventStatus.OMITTED);
            scheduledEventEntity = entityManager.merge(scheduledEventEntity);
            entityManager.persist(scheduledEventEntity);
            shouldOmit = true;
        }

        transaction.commit();
        return shouldOmit;
    }

    private void handle(UUID eventId, EntityManager entityManager) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        ScheduledEventQuery scheduledEventQuery = applicationContext.getBean(ScheduledEventQuery.class);
        ScheduledEventEntity scheduledEvent = scheduledEventQuery.ids(eventId).first();
        if (scheduledEvent == null) throw new IllegalArgumentException("scheduledEvent is null");

        EventProcessingStatus status = this.process(scheduledEvent, entityManager);
        scheduledEvent = entityManager.find(ScheduledEventEntity.class, scheduledEvent.getId());
        switch (status) {
            case Success: {
                scheduledEvent.setStatus(ScheduledEventStatus.SUCCESSFUL);
                break;
            }
            case Postponed: {
                scheduledEvent.setStatus(ScheduledEventStatus.PENDING);
                break;
            }
            case Error: {
                scheduledEvent.setStatus(ScheduledEventStatus.ERROR);
                scheduledEvent.setRetryCount(scheduledEvent.getRetryCount() + 1);
                break;
            }
            case Discard:
            default: {
                scheduledEvent.setStatus(ScheduledEventStatus.DISCARD);
                break;
            }
        }

        entityManager.merge(scheduledEvent);
        entityManager.flush();

        transaction.commit();
    }

    protected EventProcessingStatus process(ScheduledEventEntity scheduledEventMessage, EntityManager entityManager) {
        return EventProcessingStatus.Discard;
    }
}
