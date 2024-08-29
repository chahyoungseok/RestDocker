package org.chs.domain.network;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.chs.domain.network.entity.NetworkContainerMappingEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.chs.domain.network.entity.QNetworkContainerMappingEntity.networkContainerMappingEntity;
import static org.chs.domain.network.entity.QNetworkEntity.networkEntity;

@Repository
@RequiredArgsConstructor
public class CustomNetworkContainerMappingEntityRepositoryImpl implements CustomNetworkContainerMappingEntityRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public long deleteByNetworkPk(String networkPk) {
        List<NetworkContainerMappingEntity> containerPkList = queryFactory.selectFrom(networkContainerMappingEntity)
                .where(eqNetworkPk(networkPk))
                .fetch();

        if (containerPkList.isEmpty()) {
            return 1;
        }

        long networkContainerMappingDeleteResult = queryFactory.delete(networkContainerMappingEntity)
                .where(eqNetworkPk(networkPk))
                .execute();

        if (0 != networkContainerMappingDeleteResult) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean existNetworkBindingContainer(String accountPk, String networkName) {
        Integer fetchOne = queryFactory.selectOne()
                .from(networkContainerMappingEntity)
                .innerJoin(networkContainerMappingEntity.network, networkEntity)
                .on(networkContainerMappingEntity.network.pk.eq(networkEntity.pk))
                .where(
                        eqAccountPk(accountPk),
                        eqNetworkName(networkName)
                )
                .fetchOne();

        return null != fetchOne;
    }

    private BooleanExpression eqAccountPk(String accountPk) {
        if (null == accountPk) {
            return null;
        }

        return networkEntity.account.pk.eq(accountPk);
    }

    private BooleanExpression eqNetworkName(String networkName) {
        if (null == networkName) {
            return null;
        }

        return networkEntity.name.eq(networkName);
    }

    private BooleanExpression eqNetworkPk(String networkPk) {
        if (null == networkPk) {
            return null;
        }

        return networkContainerMappingEntity.network.pk.eq(networkPk);
    }
}
