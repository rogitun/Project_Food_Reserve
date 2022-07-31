package heading.ground.booking.repository.book;

import heading.ground.booking.entity.book.BookedMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface BookedMenuRepository extends JpaRepository<BookedMenu,Long> {

    @Transactional
    @Modifying
    @Query("delete from BookedMenu bm " +
            "where bm.book.id = :bid")
    void deleteByBookId(@Param("bid") String id);
}
