package heading.ground.entity.util;

import heading.ground.entity.Base;
import heading.ground.entity.post.Menu;
import heading.ground.entity.user.Student;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShopCart extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @Column(name = "currentSeller")
    private Long sellerId;

    @OneToOne()
    @JoinColumn(name="user_id")
    private Student student;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "cart",orphanRemoval = true)
    List<CartMenu> menuList = new LinkedList<>();

    @ElementCollection
    @MapKeyColumn(name = "menu_name")
    @Column(name = "exist")
    @CollectionTable(name = "CartMenuMap",joinColumns = @JoinColumn(name = "cartMenu_Id"))
    private Map<String,Integer> duplicate= new HashMap<>();

    public ShopCart(Student student) {
        this.student = student;
    }

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

    public void resetCart(){
        sellerId = null;
        duplicate.clear();
        menuList.clear();
    }
}
