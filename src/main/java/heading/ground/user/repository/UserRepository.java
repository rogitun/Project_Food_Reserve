package heading.ground.user.repository;

import heading.ground.user.entity.BaseUser;
import heading.ground.user.entity.Seller;
import heading.ground.user.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<BaseUser,Long>, UserRepositoryCustom {

    // 공통
    Optional<BaseUser> findByLoginId(String username);

    long countByLoginId(String loginId);

    long countByEmail(String email);

    //TODO Api쪽으로 분리하기
    @Query("select count(u) from BaseUser u " +
            "where u.loginId = :loginId or u.email = :email")
    long countEmailId(@Param("loginId") String id, @Param("email") String email);

//    @Query("select u from BaseUser u " +
//            "where u.loginId = :id and " +
//            "u.email = :email")
//    Optional<BaseUser> findUserByIdEmail(@Param("id") String id, @Param("email") String email);

    Optional<BaseUser> findByUuid(String uuid);

    //TODO 아래는 seller
//    @Query("select u.name from BaseUser u " +
//            "where u.id = :uid")
//    String findUserNameById(@Param("uid") Long id);

//    @Query(value = "select distinct s from Seller s " +
//            "left join fetch s.category c " +
//            "left join fetch s.imageFile i",
//    countQuery = "select count(s) from Seller s")
//    Page<Seller> findAllPage(Pageable pageRequest);

//    @Query(value = "select distinct s from Seller s " +
//            "left join fetch s.category c " +
//            "left join fetch s.imageFile i " +
//            "where s.name like %:key%",
//    countQuery = "select count(s) from Seller s where s.name like %:key%")
//    Page<Seller> findAllByKeyword(@Param("key") String key, Pageable pageable);

    @Query(value = "select distinct s from Seller s " +
            "left join fetch s.category c " +
            "left join fetch s.imageFile i " +
            "where c.name like %:cat%",
            countQuery = "select count(s) from Seller s " +
                    "where s.category.name like %:cat%")
    Page<Seller> findAllByCategory(@Param("cat") String cat, Pageable pageable);

//    @Query("select s from Seller s " +
//            "left join fetch s.menus m " +
//            "where s.id = :uid")
//    Seller findByIdWithMenu(@Param("uid") Long id);


    //TODO 아래는 Student

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update Seller se " +
            "set se.category_id = null " +
            "where se.category_id = :cid",nativeQuery = true)
    void deleteCategory(@Param("cid") long cid);
}
