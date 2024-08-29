package org.chs.domain.network;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.chs.domain.network.entity.NetworkContainerMappingEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.chs.domain.account.entity.QAccountEntity.accountEntity;
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
    public boolean existNetworkBindingContainer(String oauthServiceId, String networkName) {
        Integer fetchOne = queryFactory.selectOne()
                .from(networkContainerMappingEntity)
                .innerJoin(networkContainerMappingEntity.network, networkEntity)
                .innerJoin(networkEntity.account, accountEntity)
                .where(
                        eqOAuthServiceId(oauthServiceId),
                        eqNetworkName(networkName)
                )
                .fetchOne();

        return null != fetchOne;
    }

    private BooleanExpression eqOAuthServiceId(String oauthServiceId) {
        if (null == oauthServiceId) {
            return null;
        }

        return networkEntity.account.oauthServiceId.eq(oauthServiceId);
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
