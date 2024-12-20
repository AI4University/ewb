package gr.cite.intelcomp.evaluationworkbench.data;

import gr.cite.intelcomp.evaluationworkbench.common.enums.CommandType;
import gr.cite.intelcomp.evaluationworkbench.common.enums.Status;

import gr.cite.intelcomp.evaluationworkbench.data.conventers.enums.CommandTypeConverter;
import gr.cite.intelcomp.evaluationworkbench.data.conventers.enums.StatusConverter;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "execution")
public class ExecutionEntity {
    @Id
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    public final static String _id = "id";

    @Column(name = "user_id", columnDefinition = "uuid", nullable = false)
    private UUID userId;
    public static final String _userId = "userId";

    @Column(name = "type", nullable = false)
    @Convert(converter = CommandTypeConverter.class)
    private CommandType type;
    public final static String _type = "type";

    @Column(name = "command", nullable = false, length = _commandLength)
    private String command;
    public static final String _command = "command";
    public final static int _commandLength = 200;

    @Column(name = "result", length = _resultLength)
    private String result;
    public static final String _result = "result";
    public final static int _resultLength = 200;

    @Column(name = "status", nullable = false)
    @Convert(converter = StatusConverter.class)
    private Status status;
    public final static String _status = "status";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    public static final String _createdAt = "createdAt";

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    public static final String _updatedAt = "updatedAt";

    @Column(name = "executed_at")
    private Instant executedAt;
    public static final String _executedAt = "executedAt";

    @Column(name = "finished_at")
    private Instant finishedAt;
    public static final String _finishedAt = "finishedAt";

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public CommandType getType() {
        return type;
    }
    public void setType(CommandType type) {
        this.type = type;
    }

    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }
    public void setExecutedAt(Instant executedAt) {
        this.executedAt = executedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }
    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }
}
