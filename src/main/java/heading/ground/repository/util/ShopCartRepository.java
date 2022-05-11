package heading.ground.repository.util;

import heading.ground.entity.util.ShopCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopCartRepository extends JpaRepository<ShopCart,Long> {

    @Query("select c from Cart c " +
            "left join fetch c.menulist m " +
            "where c.id = :cid and " +
            ":mid in (m.id)")
    void findByMenuId(@Param("cid") Long cartId,@Param("mid") Long menuId);
}
