package heading.ground.repository.util;

import heading.ground.entity.util.CartMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartMenuRepository extends JpaRepository<CartMenu,Long> {

    @Modifying(clearAutomatically = true)
    @Query("delete from CartMenu cm " +
            "where cm.menu.id = :mid and " +
            "cm.cart.id = :cid")
    int deleteByMenuId(@Param("mid") Long id,@Param("cid") Long cartId);

}
