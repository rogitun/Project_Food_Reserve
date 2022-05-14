package heading.ground.repository.util;

import heading.ground.entity.util.ShopCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopCartRepository extends JpaRepository<ShopCart,Long> {

    @Query("select sc from ShopCart sc " +
            "left join fetch sc.menuList ml " +
            "left join fetch ml.menu m " +
            "join fetch sc.student st " +
            "where sc.student.id = :uid")
    Optional<ShopCart> findByUserIdWithAll(@Param("uid") Long id);

    @Query("select sc from ShopCart sc " +
            "join fetch sc.student st " +
            "where st.id = :uid")
    Optional<ShopCart> findByUserId(@Param("uid") Long id);
}
