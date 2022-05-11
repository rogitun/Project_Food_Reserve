package heading.ground.entity.util;

import heading.ground.entity.post.Menu;

import javax.persistence.*;

@Entity
public class CartMenu {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private ShopCart cart;

    @ManyToOne
    private Menu menu;


}
