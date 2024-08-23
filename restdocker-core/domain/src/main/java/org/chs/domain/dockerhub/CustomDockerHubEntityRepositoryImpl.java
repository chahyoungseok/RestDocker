package org.chs.domain.dockerhub;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.chs.domain.dockerhub.entity.DockerHubEntity;
import org.springframework.stereotype.Repository;

import static org.chs.domain.dockerhub.entity.QDockerHubEntity.dockerHubEntity;

@Repository
@RequiredArgsConstructor
public class CustomDockerHubEntityRepositoryImpl implements CustomDockerHubEntityRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public DockerHubEntity selectDockerImage(String imageName) {
        String[] imageNameAndTag = validColonImageName(imageName);

        return queryFactory.selectFrom(dockerHubEntity)
                .where(eqImageName(imageNameAndTag[0])
                        .and(eqImageTag(imageNameAndTag[1]))
                )
                .fetchOne();
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

    private BooleanExpression eqImageName(String imageName) {
        if (null == imageName) {
            return null;
        }

        return dockerHubEntity.name.eq(imageName);
    }

    private BooleanExpression eqImageTag(String imageTag) {
        if (null == imageTag) {
            return null;
        }

        return dockerHubEntity.tag.eq(imageTag);
    }
}
