package heading.ground.booking.repository.post;

import heading.ground.booking.entity.post.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuRepositoryCustom {
    Page<Menu> findMenusWithSeller(Pageable pageable);

    Page<Menu> findBySellerPage(Long id, Pageable pageable);

    Page<Menu> findAllByKey(Pageable pageRequest,String key);

}
