package heading.ground.booking.repository.post;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import heading.ground.booking.entity.post.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static heading.ground.booking.entity.post.QMenu.menu;
import static heading.ground.user.entity.QSeller.seller;

@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Menu> findMenusWithSeller(Pageable pageable) {
        List<Menu> data = queryFactory.select(menu)
                .from(menu)
                .join(menu.seller, seller).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(menu.count())
                .from(menu);

        return PageableExecutionUtils.getPage(data, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Menu> findBySellerPage(Long id, Pageable pageable) {
        List<Menu> data = queryFactory.selectFrom(menu)
                .join(menu.seller, seller).fetchJoin()
                .where(menu.seller.id.eq(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(menu.count())
                .from(menu)
                .where(menu.seller.id.eq(id));

        return PageableExecutionUtils.getPage(data, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Menu> findAllByKey(Pageable pageRequest, String key) {
        List<Menu> data = queryFactory.select(menu)
                .from(menu)
                .join(menu.seller, seller)
                .where(menu.name.contains(key))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(menu.count())
                .from(menu)
                .join(menu.seller, seller)
                .where(menu.name.contains(key));

        return PageableExecutionUtils.getPage(data, pageRequest, countQuery::fetchOne);
    }
}
