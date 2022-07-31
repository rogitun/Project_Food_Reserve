package heading.ground.booking.entity.post;

import heading.ground.file.ImageFile;
import heading.ground.common.Base;
import heading.ground.booking.entity.book.BookedMenu;
import heading.ground.user.entity.Seller;
import heading.ground.booking.forms.post.MenuForm;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Menu extends Base {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @Size(max = 16)
    @NotNull
    private String name; //음식 이름

    @NotNull
    private int price; //음식 가격

    @Lob
    @Column(name="intro",length = 512)
    private String desc; //음식 설명

    @Column(columnDefinition = "TEXT")
    private String sources;//음식에 들어가는 재료, 굳이 엔티로 뽑을 필요 없음

    @Column(columnDefinition = "TINYINT")
    private int commentNumber;

    @Column(columnDefinition = "TINYINT",length = 1)
    private int star;

    @NotNull
    private boolean outOfStock;

    private boolean isBest;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private ImageFile image;

    //메뉴가 소속된 가게
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id",nullable = false)
    private Seller seller;

    @OneToMany(mappedBy = "menu",cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "menu",cascade = CascadeType.REMOVE)
    private List<BookedMenu> bookedMenus = new ArrayList<>();

    public Menu(MenuForm form) {
        updateField(form);
    }

    public void addSeller(Seller seller){
        this.seller = seller;
        seller.getMenus().add(this);
    }

    public void addImage(ImageFile image){
        this.image = image;
    }

    public void updateField(MenuForm form){
        this.name = form.getName();
        this.price = form.getPrice();
        this.desc = form.getDesc();
        this.sources = form.getSources();
    }

    public void addStar(int num) {
        star+=num; commentNumber++;
    }

    public void delStar(int num){
        star-=num; commentNumber--;
    }

    public void setStock() {
        this.outOfStock = this.outOfStock ? false :  true;
    }

    public void setBest() {
        this.isBest = this.isBest ? false :  true;
    }
}
