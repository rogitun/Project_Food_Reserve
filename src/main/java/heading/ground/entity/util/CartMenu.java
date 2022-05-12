package heading.ground.entity.util;

import heading.ground.entity.post.Menu;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartMenu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartMenu_Id")
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private ShopCart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    public CartMenu(ShopCart cart, Menu menu) {
        this.cart = cart;
        this.menu = menu;
    }
}
