package heading.ground.entity.util;

import heading.ground.entity.Base;
import heading.ground.entity.post.Menu;
import lombok.Getter;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
public class ShopCart extends Base {

    @Id
    @GeneratedValue
    @Column(name = "cart_id")
    private Long id;

    private Long sellerId;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "cart")
    List<CartMenu> menuList = new LinkedList<>();

    public void addMenu(Menu menu) {
//        Long sellerIdFromMenu = menu.getSeller().getId();
//        if(sellerId != sellerIdFromMenu){
//            menuList.clear();
//        }
//
//
//        this.sellerId = sellerIdFromMenu;
//        menuList.add(menu);
    }
}
