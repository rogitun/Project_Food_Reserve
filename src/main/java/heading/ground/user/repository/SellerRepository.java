package heading.ground.user.repository;

import heading.ground.user.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//TODO -> UserRepository로 통합
public interface SellerRepository extends JpaRepository<Seller,Long> {

    long countByLoginId(@Param("loginId") String loginId);

    Optional<Seller> findByLoginId(String longId);

//    //TODO Seller & Menu 최적화
    @Query(value = "select distinct s from Seller s " +
            "join fetch s.menus m",
            countQuery = "select count(s) from Seller s")
    Page<Seller> findAll(PageRequest pageRequest);

    @Query("select s from Seller s " +
            "join fetch s.menus m " +
            "where s.id =:id")
    Seller findByIdWithMenu(@Param("id") Long id);




}
