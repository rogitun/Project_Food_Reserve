package heading.ground.booking.repository.post;

import heading.ground.booking.entity.post.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("select c from Comment c where c.menu.id = :id")
    List<Comment> findByMenuId(@Param("id") Long id);

    @Query("select c from Comment c " +
            "join fetch c.writer w " +
            "where c.id =:cid and w.id =:uid")
    Optional<Comment> findByIdWithUser(@Param("cid") Long id,@Param("uid") Long id1);

    @Query("select c from Comment c " +
            "left join fetch c.menu m " +
            "left join fetch c.writer w " +
            "where m.id =:mid and w.id =:uid")
    Optional<Comment> findCoByMenuUser(@Param("mid") Long id,@Param("uid") Long id1);
}
