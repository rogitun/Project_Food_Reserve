package heading.ground.dto.book;

import heading.ground.dto.user.StudentDto;
import heading.ground.entity.book.Book;
import heading.ground.entity.book.BookStatus;
import heading.ground.entity.book.BookType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class BookDto {

    private Long id;
    private BookStatus status;

    private BookType type; //방문 타입

    private int totalPrice;

    private LocalDateTime bookTime;

    private int number; //사람 몇명 오는지

    private StudentDto student; //예약자

    private BookSellerDto seller;

    private String reason;

    private boolean isPaid;

    private List<BookedMenuDto> bookedMenus = new ArrayList<>(); //예약된 메뉴들

    public BookDto(Book book) {
        id = book.getId();
        status = book.getStatus();
        type = book.getType();
        totalPrice = book.getTotalPrice();
        bookTime = book.getBookTime();
        number = book.getNumber();
        student = new StudentDto(book.getStudent().getName());
        seller = new BookSellerDto(book.getSeller());
        reason = book.getReason();
        isPaid = book.isPaid();
        book.getBookedMenus()
                .stream()
                .map(bm -> new BookedMenuDto(bm))
                .forEach(s -> bookedMenus.add(s));
    }

    public String type(){
        return type.toString().toLowerCase();
    }

    public String status(){
        return status.toString().toLowerCase();
    }

}
