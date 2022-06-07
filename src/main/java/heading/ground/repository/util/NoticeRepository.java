package heading.ground.repository.util;

import heading.ground.entity.util.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoticeRepository extends JpaRepository<Notice,Long> {

    @Query(value = "select n from Notice n " +
            "order by n.created desc",
    countQuery = "select count(n) from Notice n")
    Page<Notice> findAllByOrder(Pageable pageable);

}
