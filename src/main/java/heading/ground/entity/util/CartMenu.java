package heading.ground.entity.util;

import heading.ground.entity.post.Menu;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartMenu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartMenu_Id")
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "cart_id",nullable = false)
    private ShopCart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id",nullable = false)
    private Menu menu;

    @Column(columnDefinition = "TINYINT",length = 4)
    @NotNull
    private int quantity;

    public CartMenu(ShopCart cart, Menu menu) {
        this.cart = cart;
        this.menu = menu;
    }
}
