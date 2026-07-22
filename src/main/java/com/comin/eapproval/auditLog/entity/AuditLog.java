package com.comin.eapproval.auditLog.entity;

import com.comin.eapproval.auditLog.entity.enums.AuditLogAction;
import com.comin.eapproval.auditLog.entity.enums.AuditLogResult;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditLogAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditLogResult result;

    @Column(length = 500)
    private String reason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private AuditLog(
            Long documentId,
            Long actorId,
            AuditLogAction action,
            AuditLogResult result,
            String reason
    ) {
        this.documentId = documentId;
        this.actorId = actorId;
        this.action = action;
        this.result = result;
        this.reason = reason;
    }

    /**
     * 결재 작업의 감사 로그를 생성한다.
     */
    public static AuditLog create(
            Long documentId,
            Long actorId,
            AuditLogAction action,
            AuditLogResult result,
            String reason
    ) {
        return AuditLog.builder()
                .documentId(documentId)
                .actorId(actorId)
                .action(action)
                .result(result)
                .reason(reason)
                .build();
    }
}
