package org.chs.domain.image;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomImageEntityRepositoryImpl implements CustomImageEntityRepository{

    private final JPAQueryFactory queryFactory;


}
