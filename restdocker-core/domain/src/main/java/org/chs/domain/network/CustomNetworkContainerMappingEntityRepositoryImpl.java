package org.chs.domain.network;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.chs.domain.container.ContainerEntityRepository;
import org.chs.domain.network.entity.NetworkContainerMappingEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.chs.domain.network.entity.QNetworkContainerMappingEntity.networkContainerMappingEntity;

@Repository
@RequiredArgsConstructor
public class CustomNetworkContainerMappingEntityRepositoryImpl implements CustomNetworkContainerMappingEntityRepository{

    private final JPAQueryFactory queryFactory;
    private final ContainerEntityRepository containerEntityRepository;

    @Override
    public long deleteByNetworkPk(String networkPk) {
        List<NetworkContainerMappingEntity> containerPkList = queryFactory.selectFrom(networkContainerMappingEntity)
                .where(eqNetworkPk(networkPk))
                .fetch();

        if (containerPkList.isEmpty()) {
            return 1;
        }

        long containerDeleteResult = containerEntityRepository
                .deleteByContainerPkList(containerPkList.stream().map(NetworkContainerMappingEntity::getPk).toList());

        long networkContainerMappingDeleteResult = queryFactory.delete(networkContainerMappingEntity)
                .where(eqNetworkPk(networkPk))
                .execute();

        if (0 != containerDeleteResult && 0 != networkContainerMappingDeleteResult) {
            return 1;
        }
        return 0;
    }

    private BooleanExpression eqNetworkPk(String networkPk) {
        if (null == networkPk) {
            return null;
        }

        return networkContainerMappingEntity.network.pk.eq(networkPk);
    }
}
