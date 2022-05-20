package heading.ground.dto.book;

import heading.ground.dto.user.StudentDto;
import heading.ground.entity.book.Book;
import heading.ground.entity.book.BookStatus;
import heading.ground.entity.book.BookType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BookDto {

    private String id;
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
        bookTime = book.getBookDate();
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

    public static BookDto bookDto(Book book){
        BookDto bookDto = new BookDto();
        bookDto.setNumber(book.getNumber());
        bookDto.setBookTime(book.getBookDate());
        bookDto.setStatus(book.getStatus());
        bookDto.setStudent(new StudentDto(book.getStudent().getName()));
        bookDto.setSeller(new BookSellerDto(book.getSeller()));

        return bookDto;
    }

    public String type(){
        String typeIs = (type==null)?"미정":type.toString().toLowerCase();
        return typeIs;
    }

    public String status(){
        if(isPaid == false){
            return "미결제";
        }

        String statusIs;
        if(status==BookStatus.PENDING)
            statusIs = "승인 대기중";
        else if(status==BookStatus.ACCEPT)
            statusIs ="예약 승인";
        else
            statusIs = "예약 취소";

        return statusIs;
    }

}
