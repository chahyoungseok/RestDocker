package org.chs.domain.network;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.chs.domain.container.dto.ContainerPrivateIpDto;
import org.chs.domain.network.entity.NetworkContainerMappingEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.chs.domain.account.entity.QAccountEntity.accountEntity;
import static org.chs.domain.container.entity.QContainerEntity.containerEntity;
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

        return queryFactory.delete(networkContainerMappingEntity)
                .where(eqNetworkPk(networkPk))
                .execute();
    }

    @Override
    public long deleteByContainerPk(String containerPk) {
        return queryFactory.delete(networkContainerMappingEntity)
                .where(eqContainerPk(containerPk))
                .execute();
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

    @Override
    public List<String> findPrivateIpByOAuthServiceIdAndNetworkName(String oauthServiceId, String networkName) {
        return queryFactory.select(Projections.fields(ContainerPrivateIpDto.class,
                        containerEntity.privateIp)
                )
                .from(networkContainerMappingEntity)
                .innerJoin(networkContainerMappingEntity.network, networkEntity)
                .innerJoin(networkEntity.account, accountEntity)
                .innerJoin(networkContainerMappingEntity.container, containerEntity)
                .where(
                        eqOAuthServiceId(oauthServiceId),
                        eqNetworkName(networkName)
                )
                .fetch()
                .stream().map(ContainerPrivateIpDto::getPrivateIp).toList();
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

    private BooleanExpression eqContainerPk(String containerPk) {
        if (null == containerPk) {
            return null;
        }

        return networkContainerMappingEntity.container.pk.eq(containerPk);
    }
}
