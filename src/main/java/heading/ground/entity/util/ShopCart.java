package heading.ground.entity.util;

import heading.ground.entity.Base;
import heading.ground.entity.post.Menu;
import lombok.Getter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
public class ShopCart extends Base {

    @Id
    @GeneratedValue
    @Column(name = "cart_id")
    private Long id;

    private Long sellerId;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "cart",orphanRemoval = true)
    List<CartMenu> menuList = new LinkedList<>();

    @ElementCollection
    @MapKeyColumn(name = "menu_name")
    @Column(name = "exist")
    @CollectionTable(name = "CartMenuMap",joinColumns = @JoinColumn(name = "cartMenu_Id"))
    private Map<String,Integer> duplicate= new HashMap<>();

    public void addMenu(Menu menu) {
        Long sellerIdFromMenu = menu.getSeller().getId();
        if(sellerId != sellerIdFromMenu){
            System.out.println("다른메뉴 in entity");
            menuList.clear();
            duplicate.clear();
        }
        this.sellerId = sellerIdFromMenu;
        menuList.add(new CartMenu(this,menu));
        duplicate.put(menu.getName(),1);
    }

    public boolean duplicateCheck(String menuName){
        Integer dup = this.duplicate.getOrDefault(menuName, 0);
        return (dup==1)?true:false;
    }
}
