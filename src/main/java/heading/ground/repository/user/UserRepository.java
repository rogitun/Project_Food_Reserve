package heading.ground.repository.user;

import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<BaseUser,Long> {

    // 공통
    Optional<BaseUser> findByLoginId(String username);

    long countByLoginId(@Param("loginId") String loginId);


    //TODO 아래는 seller
    @Query("select s.name from Seller s " +
            "where s.id = :uid")
    String findSellerById(@Param("uid") Long id);

    @Query("select distinct s from Seller s " +
            "left join fetch s.menus m " +
            "left join fetch s.books b " +
            "where s.id = :uid")
    Seller findSellerByIdForAccount(@Param("uid") Long id);

    @Query(value = "select distinct s from Seller s " +
            "left join fetch s.menus m",
    countQuery = "select count(s) from Seller s")
    Page<Seller> findAll(PageRequest pageRequest);


    @Query("select s from Seller s " +
            "left join fetch s.imageFile i " +
            "where s.id = :uid")
    Seller findSellerWithImage(@Param("uid") Long id);

    @Query("select s from Seller s " +
            "left join fetch s.menus m " +
            "left join fetch m.comments c " +
            "where s.id = :uid")
    Seller findByIdWithMenuComment(@Param("uid") Long id);


    //TODO 아래는 Student

    @Query("select distinct s from Student s " +
            "left join fetch s.books b " +
            "where s.id = :uid")
    Student findStudentByIdForAccount(@Param("uid") Long id);

    @Query("select s from Student s " +
            "where s.id = :uid")
    Student findStudentById(@Param("uid") Long id);

}
