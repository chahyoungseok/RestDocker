package org.chs.domain.container;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.chs.domain.container.dto.ContainerDetailElements;
import org.chs.domain.container.dto.ContainerElements;
import org.chs.domain.container.dto.ContainerValidElementsDto;
import org.chs.domain.container.entity.ContainerEntity;
import org.chs.domain.network.NetworkContainerMappingEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.chs.domain.account.entity.QAccountEntity.accountEntity;
import static org.chs.domain.container.entity.QContainerEntity.containerEntity;
import static org.chs.domain.image.entity.QImageEntity.imageEntity;

@Repository
@RequiredArgsConstructor
public class CustomContainerEntityRepositoryImpl implements CustomContainerEntityRepository{

    private final JPAQueryFactory queryFactory;
    private final NetworkContainerMappingEntityRepository networkContainerMappingRepository;

    @Override
    public List<ContainerElements> lsContainer(String oauthServiceId) {
        return queryFactory.select(
                Projections.fields(ContainerElements.class,
                        containerEntity.createDate.as("createDate"),
                        containerEntity.updateDate.as("updateDate"),
                        containerEntity.name.as("name"),
                        containerEntity.image.name.as("imageName"),
                        containerEntity.image.tag.as("imageTag"),
                        containerEntity.privateIp.as("privateIp"),
                        containerEntity.outerPort.as("outerPort"),
                        containerEntity.innerPort.as("innerPort"),
                        containerEntity.status.as("status")))
                .from(containerEntity)
                .innerJoin(containerEntity.image, imageEntity)
                .innerJoin(imageEntity.account, accountEntity)
                .where(eqOauthServiceId(oauthServiceId))
                .fetch();
    }

    @Override
    public ContainerDetailElements inspectContainer(String oauthServiceId, String containerName) {
        if (null == containerName) {
            throw new IllegalArgumentException("Inspect 명령은 ContainerName이 필요합니다");
        }

        return queryFactory.select(
                        Projections.fields(ContainerDetailElements.class,
                                containerEntity.createDate.as("createDate"),
                                containerEntity.updateDate.as("updateDate"),
                                containerEntity.name.as("name"),
                                containerEntity.image.name.as("imageName"),
                                containerEntity.image.tag.as("imageTag"),
                                containerEntity.outerPort.as("outerPort"),
                                containerEntity.innerPort.as("innerPort"),
                                containerEntity.privateIp.as("privateIp"),
                                containerEntity.stopRm.as("stopRm"),
                                containerEntity.status.as("status")))
                .from(containerEntity)
                .innerJoin(containerEntity.image, imageEntity)
                .innerJoin(imageEntity.account, accountEntity)
                .where(
                        eqOauthServiceId(oauthServiceId),
                        eqContainerName(containerName)
                )
                .fetchOne();
    }

    @Override
    public boolean renameContainer(String containerPk, String postContainerName) {
        long renameResult = queryFactory.update(containerEntity)
                .set(containerEntity.name, postContainerName)
                .where(
                        eqContainerPk(containerPk)
                )
                .execute();

        return renameResult != 0;
    }

    @Override
    public String findContainerPk(String oauthServiceId, String containerName) {
        ContainerEntity container = queryFactory.selectFrom(containerEntity)
                .innerJoin(containerEntity.image, imageEntity)
                .innerJoin(imageEntity.account, accountEntity)
                .where(
                        eqOauthServiceId(oauthServiceId),
                        eqContainerName(containerName)
                )
                .fetchOne();

        return container.getPk();
    }

    @Override
    public List<ContainerValidElementsDto> findValidElementsListByOAuthServiceId(String oauthServiceId) {
        return queryFactory.select(Projections.fields(ContainerValidElementsDto.class,
                        containerEntity.name.as("containerName"),
                        containerEntity.outerPort.as("outerPort")
                ))
                .from(containerEntity)
                .innerJoin(containerEntity.image, imageEntity)
                .innerJoin(imageEntity.account, accountEntity)
                .where(eqOauthServiceId(oauthServiceId))
                .fetch();
    }

    @Override
    public String findContainerPkByOAuthServiceAndContainerName(String oauthServiceId, String preContainerName) {
        return queryFactory.selectFrom(containerEntity)
                .innerJoin(containerEntity.image, imageEntity)
                .innerJoin(imageEntity.account, accountEntity)
                .where(
                        eqOauthServiceId(oauthServiceId),
                        eqContainerName(preContainerName)
                )
                .fetchOne().getPk();
    }

    @Override
    public boolean rmContainer(String containerPk) {
        // 매퍼 삭제
        long networkDeleteResult = networkContainerMappingRepository.deleteByContainerPk(containerPk);

        long result = queryFactory.delete(containerEntity)
                .where(eqContainerPk(containerPk))
                .execute();

        return 0 != result;
    }

    @Override
    public boolean existContainerForImage(String oauthServiceId, String imageName) {
        String[] imageNameAndTag = validColonImageName(imageName);

        Integer fetchOne = queryFactory.selectOne()
                .from(containerEntity)
                .innerJoin(containerEntity.image, imageEntity)
                .innerJoin(imageEntity.account, accountEntity)
                .where(
                        eqOauthServiceId(oauthServiceId),
                        eqImageName(imageNameAndTag[0]),
                        eqImageTag(imageNameAndTag[1])
                )
                .fetchOne();

        return null != fetchOne;
    }

    private BooleanExpression eqContainerName(String containerName) {
        if (null == containerName) {
            return null;
        }

        return containerEntity.name.eq(containerName);
    }

    private BooleanExpression eqOauthServiceId(String oauthServiceId) {
        if (null == oauthServiceId) {
            throw new IllegalArgumentException("");
        }

        return accountEntity.oauthServiceId.eq(oauthServiceId);
    }

    private BooleanExpression eqImageName(String imageName) {
        if (null == imageName) {
            return null;
        }

        return imageEntity.name.eq(imageName);
    }

    private BooleanExpression eqImageTag(String imageTag) {
        if (null == imageTag) {
            return null;
        }

        return imageEntity.tag.eq(imageTag);
    }

    private BooleanExpression eqContainerPk(String containerPk) {
        if (null == containerPk) {
            return Expressions.FALSE;
        }

        return containerEntity.pk.eq(containerPk);
    }

    private String[] validColonImageName(String imageName) {
        String imageFullName = notExistImageTagAddLatest(imageName);

        String[] imageNameAndTag = imageFullName.split(":");

        if (2 != imageNameAndTag.length) {
            throw new IllegalArgumentException("Image 이름에 콜론(:) 이 포함 되어 있습니다.");
        }

        return imageNameAndTag;
    }

    private String notExistImageTagAddLatest(String imageName) {
        if (false == imageName.contains(":")) {
            return imageName + ":latest";
        }

        return imageName;
    }

    // Container Create 시, OauthServiceId, ContainerName 이 복합 Unique 속성인걸 인지
}
