package heading.ground.repository.user;

import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<BaseUser,Long> {


    long countByLoginId(@Param("loginId") String loginId);

    @Query("select u.name from BaseUser u " +
            "where u.id = :uid")
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

    //TODO 아래는 Student

    @Query("select distinct s from Student s " +
            "left join fetch s.books b " +
            "where s.id = :uid")
    Student findStudentByIdForAccount(@Param("uid") Long id);


}
