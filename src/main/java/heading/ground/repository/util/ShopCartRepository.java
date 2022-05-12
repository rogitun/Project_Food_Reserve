package heading.ground.repository.util;

import heading.ground.entity.util.ShopCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopCartRepository extends JpaRepository<ShopCart,Long> {

}
