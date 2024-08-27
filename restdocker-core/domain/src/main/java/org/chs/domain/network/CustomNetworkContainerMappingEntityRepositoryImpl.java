package org.chs.domain.network;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.chs.domain.container.ContainerEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.chs.domain.container.entity.QContainerEntity.containerEntity;
import static org.chs.domain.network.entity.QNetworkContainerMappingEntity.networkContainerMappingEntity;
import static org.chs.domain.network.entity.QNetworkEntity.networkEntity;

@Repository
@RequiredArgsConstructor
public class CustomNetworkContainerMappingEntityRepositoryImpl implements CustomNetworkContainerMappingEntityRepository{

    private final JPAQueryFactory queryFactory;
    private final ContainerEntityRepository containerEntityRepository;

    @Override
    public long deleteByNetworkName(String networkName) {
        List<String> containerPkList = queryFactory.select(
                Projections.fields(String.class,
                        networkContainerMappingEntity.container.pk)
                )
                .from(networkContainerMappingEntity)
                .innerJoin(networkContainerMappingEntity.container, containerEntity)
                    .on(networkContainerMappingEntity.container.pk.eq(containerEntity.pk))
                .innerJoin(networkContainerMappingEntity.network, networkEntity)
                    .on(networkContainerMappingEntity.network.pk.eq(networkEntity.pk))
                .where(eqNetworkName(networkName))
                .fetch();

        if (containerPkList.isEmpty()) {
            return 1;
        }

        long containerDeleteResult = containerEntityRepository.deleteByContainerPkList(containerPkList);

        long networkContainerMappingDeleteResult = queryFactory.delete(networkContainerMappingEntity)
                .where(eqNetworkName(networkName))
                .execute();

        if (0 != containerDeleteResult && 0 != networkContainerMappingDeleteResult) {
            return 1;
        }
        return 0;
    }

    private BooleanExpression eqNetworkName(String networkName) {
        if (null == networkName) {
            return null;
        }

        return networkContainerMappingEntity.network.name.eq(networkName);
    }
}
