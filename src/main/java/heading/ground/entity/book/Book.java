package heading.ground.entity.book;

import heading.ground.api.dto.BookApiDto;
import heading.ground.entity.Base;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.forms.book.BookForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Book extends Base {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Enumerated(EnumType.STRING)
    private BookType type; //방문 타입

    @NotNull
    private int totalPrice;

    private LocalDateTime bookDate;

    @Column(columnDefinition = "TINYINT", length = 2)
    private int number; //사람 몇명 오는지

    private String reason; //취소시 사유

    private boolean isPaid;//결제가 되었는지.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; //예약자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BookedMenu> bookedMenus = new ArrayList<>(); //예약된 메뉴들


    public static Book book(Seller seller, Student student, List<BookedMenu> bookedMenus) {
        Book book = new Book();
        book.setBook(seller, student, bookedMenus);
        return book;
    }

    public void setBook(Seller seller, Student student, List<BookedMenu> bookedMenus) {
        for (BookedMenu bookedMenu : bookedMenus) {
            bookedMenu.addBook(this);
            this.bookedMenus.add(bookedMenu);
            this.totalPrice += bookedMenu.getPrice();
        }
        this.seller = seller;
        this.student = student;
        this.status = BookStatus.PENDING;

        student.getBooks().add(this);
        seller.getBooks().add(this);

    }

    public void processBook(boolean flag) {
        if (flag)
            this.status = BookStatus.ACCEPT;
        else
            this.status = BookStatus.CANCELED;
    }

    public void cancelRequest(){
        this.status = BookStatus.WITHDRAW;
    }

    public void bookReject(String reason) {
        this.reason = reason;
        this.status = BookStatus.CANCELED;
    }

    public void setDetail(BookApiDto bookApiDto) {
        LocalDateTime bookDateTime = LocalDateTime.parse(bookApiDto.getDateVal() + "T" + bookApiDto.getTimeVal());
        this.bookDate = bookDateTime;
        this.number = bookApiDto.getVisitVal();
        if (bookApiDto.getTypeVal().equals("togo")) {
            this.type = BookType.TOGO;
        } else {
            this.type = BookType.HERE;
        }
    }

    public void bookPaid(){
        this.isPaid = true;
    }
}
