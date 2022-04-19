package heading.ground.repository.book;

import heading.ground.entity.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,Long> {

    @Query("select b from Book b where :id in (b.seller.id, b.student.id)")
    List<Book> findAllBooks(@Param("id") Long id);

    //TODO Book - bookedmenu - menu fetccJoin 처리

    @Query("select distinct b from Book b " +
            "join fetch b.student s " +
            "join fetch b.seller se " +
            "join fetch b.bookedMenus bm " +
            "where b.id = :bid ")
    Book findByIdWithCollections(@Param("bid") Long id);


    @Query("select distinct b from Book b " +
            "join fetch b.student s " +
            "where b.seller.id = :pid")
    List<Book> findAllBooksForSeller(@Param("pid") Long id);

    @Query("select distinct b from Book b " +
            "join fetch b.seller s " +
            "where b.student.id = :pid")
    List<Book> findAllBooksForStudent(@Param("pid") Long id);


    @Query("select b from Book b " +
            "join fetch b.seller s " +
            "where s.id = :pid")
    Optional<Book> findBookWithSeller(@Param("pid") Long id);
    
}
