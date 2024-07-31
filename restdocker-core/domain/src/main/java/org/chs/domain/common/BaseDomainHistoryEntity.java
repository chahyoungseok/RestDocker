package org.chs.domain.common;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseDomainHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pk", columnDefinition = "CHAR(36)", length = 36)
    private String pk;

    @CreatedDate
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @CreatedBy
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "ip_address")
    private String ipAddress;

    public BaseDomainHistoryEntity(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public BaseDomainHistoryEntity(String createdBy, String ipAddress) {
        this.createdBy = createdBy;
        this.ipAddress = ipAddress;
    }
}
