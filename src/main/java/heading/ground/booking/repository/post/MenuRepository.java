package heading.ground.booking.repository.post;

import heading.ground.booking.entity.post.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MenuRepository extends JpaRepository<Menu,Long>, MenuRepositoryCustom {

//    @Query(value = "select m from Menu m " +
//            "join fetch m.seller s ",
//    countQuery = "select count(m) from Menu m")
//    Page<Menu> findMenusWithSeller(Pageable pageable);

//    @Query(value = "select m from Menu m " +
//            "join fetch m.seller s " +
//            "where s.id = :sid",
//    countQuery = "select count(m) from Menu m")
//    Page<Menu> findBySellerPage(@Param("sid") Long id, Pageable pageable);

//    @Query(value = "select m from Menu m " +
//            "join fetch m.seller s " +
//            "where m.name like %:key%",
//    countQuery = "select count(m) from Menu m where m.name like %:key%")
//    Page<Menu> findAllByKey(Pageable pageRequest,@Param("key") String key);

    @Query("select m from Menu m " +
            "join fetch m.seller s " +
            "where s.id = :sid")
    List<Menu> findMenusBySellerId(@Param("sid") Long id);

    @Query("select m from Menu m " +
            "join fetch m.seller s " +
            "where s.id = :sid and " +
            "m.id = :mid")
    Optional<Menu> findMenuByIdWithSeller(@Param("mid") Long id, @Param("sid") Long sid);

    @Query("select count(m) > 0 from Menu m " +
            "left join m.seller s " +
            "where s.id = :sid and " +
            "m.id = :mid")
    boolean countBySellerId(@Param("sid") Long sid, @Param("mid") Long mid);


    @Query("select m from Menu m " +
            "left join fetch m.comments c " +
            "left join fetch c.writer w " +
            "join fetch m.seller s " +
            "where m.id = :mid")
    Optional<Menu> findMenuByIdWithCoSe(@Param("mid") Long mid);


    @Query("select count(m) from Menu m " +
            "where m.outOfStock = false " +
            "and m.seller.id =:sid")
    long countStock(@Param("sid") Long id);

    //TODO New version
    @Query("select m from Menu m " +
            "join fetch m.seller s " +
            "where m.id in (:ids)")
    List<Menu> findByIds(@Param("ids") Set<Long> menuIds);

    @Query("select m from Menu m " +
            "join m.seller s " +
            "where s.id =:sid and " +
            "m.isBest = true")
    List<Menu> findBestMenusBySellerId(@Param("sid") Long id,Pageable pageable);



}
