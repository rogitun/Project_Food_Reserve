package heading.ground.repository.user;

import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<BaseUser,Long> {

    // 공통
    Optional<BaseUser> findByLoginId(String username);

    long countByLoginId(String loginId);

    long countByEmail(String email);

    @Query("select count(u) from BaseUser u " +
            "where u.loginId = :loginId or u.email = :email")
    long countEmailId(@Param("loginId") String id, @Param("email") String email);

    @Query("select u from BaseUser u " +
            "where u.loginId = :id and " +
            "u.email = :email")
    Optional<BaseUser> findUserByIdEmail(@Param("id") String id, @Param("email") String email);

    Optional<BaseUser> findByUuid(String uuid);

    @Query("select u from Student u " +
            "left join fetch u.cart c " +
            "where u.id = :uid")
    Optional<Student> findByIdForCart(@Param("uid") Long id);

    //TODO 아래는 seller
    @Query("select s.name from Seller s " +
            "where s.id = :uid")
    String findSellerById(@Param("uid") Long id);

    @Query(value = "select distinct s from Seller s " +
            "left join fetch s.category c ",
    countQuery = "select count(s) from Seller s")
    Page<Seller> findAllPage(Pageable pageRequest);


    @Query(value = "select distinct s from Seller s " +
            "left join fetch s.category c " +
            "where s.name like %:key%",
    countQuery = "select count(s) from Seller s where s.name like %:key%")
    Page<Seller> findAllByKeyword(@Param("key") String key, Pageable pageable);

    @Query(value = "select distinct s from Seller s " +
            "left join fetch s.category c " +
            "where c.name like %:cat%",
            countQuery = "select count(s) from Seller s " +
                    "where s.category.name like %:cat%")
    Page<Seller> findAllByCategory(@Param("cat") String cat, Pageable pageable);

    @Query("select s from Seller s " +
            "left join fetch s.menus m " +
            "where s.id = :uid")
    Seller findByIdWithMenu(@Param("uid") Long id);


    //TODO 아래는 Student

    @Query("select distinct s from Student s " +
            "left join fetch s.books b " +
            "where s.id = :uid")
    Student findStudentByIdForAccount(@Param("uid") Long id);

    @Query("select s from Student s " +
            "where s.id = :uid")
    Student findStudentById(@Param("uid") Long id);


}
