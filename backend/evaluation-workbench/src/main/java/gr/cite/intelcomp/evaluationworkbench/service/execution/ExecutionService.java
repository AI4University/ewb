package gr.cite.intelcomp.evaluationworkbench.service.execution;

import gr.cite.intelcomp.evaluationworkbench.common.enums.Status;
import gr.cite.intelcomp.evaluationworkbench.data.ExecutionEntity;

import java.util.UUID;


public interface ExecutionService {

    void persist(ExecutionEntity entity);

    void updateStatus(UUID id, Status status);

}
