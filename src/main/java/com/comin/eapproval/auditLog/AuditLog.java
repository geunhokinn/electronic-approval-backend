package com.comin.eapproval.auditLog;

import com.comin.eapproval.auditLog.entity.enums.AuditLogAction;
import com.comin.eapproval.auditLog.entity.enums.AuditLogResult;
import com.comin.eapproval.document.entity.Document;
import com.comin.eapproval.member.entity.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private Member actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AuditLogAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditLogResult result;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private AuditLog(
            Document document,
            Member actor,
            AuditLogAction action,
            AuditLogResult result
    ) {
        this.document = document;
        this.actor = actor;
        this.action = action;
        this.result = result;
    }

    /**
     * 문서 또는 결재 작업의 감사 로그를 생성한다.
     */
    public static AuditLog create(
            Document document,
            Member actor,
            AuditLogAction action,
            AuditLogResult result
    ) {
        return AuditLog.builder()
                .document(document)
                .actor(actor)
                .action(action)
                .result(result)
                .build();
    }
}
