package gr.cite.intelcomp.evaluationworkbench.eventscheduler.processing;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.intelcomp.evaluationworkbench.data.ScheduledEventEntity;

import jakarta.persistence.EntityManager;

public interface ScheduledEventHandler {
	EventProcessingStatus handle(ScheduledEventEntity scheduledEvent, EntityManager entityManager) throws JsonProcessingException;
}
