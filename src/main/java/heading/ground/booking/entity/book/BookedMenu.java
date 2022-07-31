package heading.ground.booking.entity.book;

import heading.ground.booking.entity.post.Menu;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookedMenu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookedMenu_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @NotNull
    private int price;

    @NotNull
    @Column(columnDefinition = "TINYINT",length = 4)
    private int quantity;

    public BookedMenu(Menu menu, int quantity) {
        this.menu = menu;
        this.price = menu.getPrice() * quantity;
        this.quantity = quantity;
    }

    public void addBook(Book book){
        this.book = book;
    }

}
