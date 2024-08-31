package org.chs.domain.network;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.chs.domain.network.dto.NetworkDetailElements;
import org.chs.domain.network.dto.NetworkElements;
import org.chs.domain.network.entity.NetworkEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.chs.domain.account.entity.QAccountEntity.accountEntity;
import static org.chs.domain.network.entity.QNetworkEntity.networkEntity;

@Repository
@RequiredArgsConstructor
public class CustomNetworkEntityRepositoryImpl implements CustomNetworkEntityRepository{

    private final JPAQueryFactory queryFactory;
    private final NetworkContainerMappingEntityRepository networkContainerMappingEntityRepository;

    @Override
    public NetworkEntity findByOAuthServiceIdAndNetworkName(String oauthServiceId, String networkName) {
        return queryFactory.selectFrom(networkEntity)
                .innerJoin(networkEntity.account, accountEntity)
                .where(
                        eqOauthServiceId(oauthServiceId),
                        eqNetworkName(networkName)
                )
                .fetchOne();
    }

    @Override
    public List<NetworkElements> findByOAuthServiceId(String oauthServiceId) {

        return queryFactory.select(
                Projections.fields(NetworkElements.class,
                        networkEntity.createDate.as("createDate"),
                        networkEntity.updateDate.as("updateDate"),
                        networkEntity.name.as("name"),
                        networkEntity.subnet.as("subnet"),
                        networkEntity.ipRange.as("ipRange"),
                        networkEntity.gateway.as("gateway"))
                )
                .from(networkEntity)
                .innerJoin(networkEntity.account, accountEntity)
                .where(
                        eqOauthServiceId(oauthServiceId)
                )
                .fetch();
    }

    @Override
    public NetworkDetailElements inspectNetwork(String oauthServiceId, String networkName) {
        nullCheckNetworkName(networkName);

        return queryFactory.select(
                        Projections.fields(NetworkDetailElements.class,
                                networkEntity.createDate.as("createDate"),
                                networkEntity.updateDate.as("updateDate"),
                                networkEntity.name.as("name"),
                                networkEntity.subnet.as("subnet"),
                                networkEntity.ipRange.as("ipRange"),
                                networkEntity.gateway.as("gateway"),
                                networkEntity.enableIcc.as("enableIcc"),
                                networkEntity.mtu.as("mtu"))
                )
                .from(networkEntity)
                .innerJoin(networkEntity.account, accountEntity)
                .where(
                        eqOauthServiceId(oauthServiceId),
                        eqNetworkName(networkName)
                )
                .fetchOne();
    }

    @Override
    public boolean rmNetwork(String oauthServiceId, String networkName) {
        nullCheckNetworkName(networkName);

        NetworkEntity network = queryFactory.selectFrom(networkEntity)
                .innerJoin(networkEntity.account, accountEntity)
                .where(
                        eqOauthServiceId(oauthServiceId),
                        eqNetworkName(networkName)
                )
                .fetchOne();

        long networkContainerMappingDeleteResult =
                networkContainerMappingEntityRepository.deleteByNetworkPk(network.getPk());

        long networkDeleteResult = queryFactory.delete(networkEntity)
                .where(eqNetworkPk(network.getPk()))
                .execute();

        if (0 != networkContainerMappingDeleteResult && 0 != networkDeleteResult) {
            return true;
        }
        return false;
    }

    private BooleanExpression eqOauthServiceId(String oauthServiceId) {
        if (null == oauthServiceId) {
            throw new IllegalArgumentException("OAuthServiceId를 가진 계정이 존재하지 않습니다.");
        }

        return accountEntity.oauthServiceId.eq(oauthServiceId);
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

        return networkEntity.pk.eq(networkPk);
    }

    private void nullCheckNetworkName(String networkName) {
        if (null == networkName) {
            throw new IllegalArgumentException("NetworkName은 Null 이면 안됩니다.");
        }
    }
}
