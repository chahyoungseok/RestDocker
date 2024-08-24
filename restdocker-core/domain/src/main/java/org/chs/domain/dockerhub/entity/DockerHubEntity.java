package org.chs.domain.dockerhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chs.domain.common.BaseDomainEntity;

@Entity
@Getter
@Table(name = "docker_hub")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DockerHubEntity extends BaseDomainEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "tag", nullable = false)
    private String tag;

    @Column(name = "os", nullable = false)
    private String os;

    @Column(name = "architecture", nullable = false)
    private String architecture;

    @Column(name = "size", nullable = false)
    private String size;

    @Builder
    public DockerHubEntity(String name, String tag, String os, String architecture, String size) {
        this.name = name;
        this.tag = tag;
        this.os = os;
        this.architecture = architecture;
        this.size = size;
    }
}
