package heading.ground.repository.book;

import heading.ground.entity.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,String> {

    //TODO Book - bookedmenu - menu fetccJoin 처리

    @Query("select distinct b from Book b " +
            "join fetch b.student s " +
            "join fetch b.seller se " +
            "join fetch b.bookedMenus bm " +
            "where b.id = :bid ")
    Book findByIdWithCollections(@Param("bid") String id);

    @Query("select distinct b from Book b " +
            "join b.student s " +
            "join b.seller se " +
            "join fetch b.bookedMenus bm " +
            "where b.id = :bid ")
    Book findByIdWithMenus(@Param("bid") String id);

    @Query("select distinct b from Book b " +
            "join fetch b.student s " +
            "join fetch b.seller se " +
            "where b.id = :bid ")
    Optional<Book> findByIdWithUser(@Param("bid") String id);

    @Query("select b from Book b " +
            "join fetch b.seller s " +
            "where s.id = :pid and " +
            "b.id = :bid")
    Optional<Book> findBookWithSeller(@Param("pid") Long id,@Param("bid") String bid);

    @Query("select b from Book b " +
            "where b.id = :bid and " +
            "b.totalPrice = :p")
    Optional<Book> findByIdPrice(@Param("bid") String bid, @Param("p") int amount);

    @Query("select b from Book b " +
            "join b.seller s " +
            "join fetch b.student st " +
            "where s.id = :sid and " +
            "b.isPaid = true")
    List<Book> findBySellerId(@Param("sid") Long id);

    @Query("select count(b) > 0 from Book b " +
            "where b.bookDate = :t")
    boolean findByBookDate(@Param("t") LocalDateTime time);

    @Transactional
    @Modifying
    @Query("delete from Book b " +
            "where b.student.id = :sid and b.id = :bid")
    void deleteByStudentId(@Param("bid") String id, @Param("sid") Long studentId);
}
