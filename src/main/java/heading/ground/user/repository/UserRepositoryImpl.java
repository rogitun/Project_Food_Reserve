package heading.ground.user.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import heading.ground.booking.entity.post.QMenu;
import heading.ground.file.QImageFile;
import heading.ground.user.entity.*;
import heading.ground.utils.entity.QCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static heading.ground.booking.entity.post.QMenu.menu;
import static heading.ground.file.QImageFile.imageFile;
import static heading.ground.user.entity.QBaseUser.baseUser;
import static heading.ground.user.entity.QSeller.seller;
import static heading.ground.user.entity.QStudent.student;
import static heading.ground.utils.entity.QCategory.category;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Student findStudentById(Long id) {
        //@Query("select s from Student s " +
//            "where s.id = :uid")

        Student student = queryFactory.selectFrom(QStudent.student)
                .where(QStudent.student.id.eq(id))
                .fetchOne();

        return student;
    }

    @Override
    public Optional<BaseUser> findUserByIdEmail(String id, String email) {
        BaseUser baseUser = queryFactory.selectFrom(QBaseUser.baseUser)
                .where(QBaseUser.baseUser.loginId.eq(id),
                        QBaseUser.baseUser.email.eq(email)).fetchOne();

        return Optional.ofNullable(baseUser);
    }

    @Override
    public Seller findByIdWithMenu(Long id) {
//        @Query("select s from Seller s " +
//                "left join fetch s.menus m " +
//                "where s.id = :uid")
//
        Seller seller = queryFactory.select(QSeller.seller)
                .from(QSeller.seller)
                .leftJoin(QSeller.seller.menus, menu).fetchJoin()
                .where(QSeller.seller.id.eq(id))
                .fetchOne();

        return seller;
    }

    @Override
    public String findUserNameById(Long id) {
//        @Query("select u.name from BaseUser u " +
//                "where u.id = :uid")
//        String findUserNameById(@Param("uid") Long id);
        String userName = queryFactory.select(baseUser.name)
                .from(baseUser)
                .where(baseUser.id.eq(id)).fetchOne();
        return userName;
    }

    @Override
    public Page<Seller> findAllPage(Pageable pageRequest) {
        List<Seller> data = queryFactory.select(seller).distinct()
                .from(seller)
                .leftJoin(seller.category, category)
                .leftJoin(seller.imageFile, imageFile)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(seller.count())
                .from(seller)
                .leftJoin(seller.category, category)
                .leftJoin(seller.imageFile, imageFile);

        return PageableExecutionUtils.getPage(data, pageRequest, countQuery::fetchOne);
    }

    //    @Query(value = "select distinct s from Seller s " +
//            "left join fetch s.category c " +
//            "left join fetch s.imageFile i " +
//            "where s.name like %:key%",
//            countQuery = "select count(s) from Seller s where s.name like %:key%")
    @Override
    public Page<Seller> findAllByKeyword(String key, String cat, Pageable pageable) {
        List<Seller> data = queryFactory.select(seller).distinct()
                .from(seller)
                .leftJoin(seller.category, category)
                .leftJoin(seller.imageFile, imageFile)
                .where(isOneEqual(key, cat))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(seller.count()).distinct()
                .from(seller)
                .leftJoin(seller.category, category)
                .leftJoin(seller.imageFile, imageFile)
                .where(isOneEqual(key, cat));

        return PageableExecutionUtils.getPage(data, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder isOneEqual(String key, String cat) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(key))
            builder.or(seller.name.contains(key));

        if (StringUtils.hasText(cat))
            builder.or(seller.category.name.eq(cat));
        return builder;
    }
}
