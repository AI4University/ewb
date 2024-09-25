package gr.cite.intelcomp.evaluationworkbench.eventscheduler.processing;

public interface ConsistencyHandler<T extends ConsistencyPredicates> {
	Boolean isConsistent(T consistencyPredicates);

}
