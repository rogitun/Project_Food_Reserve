package heading.ground.entity.user;

import heading.ground.entity.ImageFile;
import heading.ground.entity.book.Book;
import heading.ground.entity.post.Menu;
import heading.ground.forms.user.BaseSignUp;
import heading.ground.forms.user.SellerEditForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@PrimaryKeyJoinColumn(name="user_id")
public class Seller extends BaseUser{
    @Embedded
    private Address address; //주소

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private ImageFile imageFile;

    //TODO 이미지 파일 추가
    
    //가게 정보
    private String companyId; //사업자 번호

    @Lob
    @Column(name="seller_desc",length = 512)
    private String desc;

    //가게가 가진 메뉴를 연관
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "seller",cascade = CascadeType.REMOVE)
    private Set<Menu> menus = new HashSet<>();

    @OneToMany(mappedBy = "seller")
    private List<Book> books = new ArrayList<>();

    public Seller(BaseSignUp sf) {
        this.loginId = sf.getLoginId();
        this.password = sf.getPassword();
        this.name = sf.getName();
        this.phoneNumber = sf.getPhoneNumber();
        this.companyId = sf.getCompanyId();
        this.role = MyRole.SELLER;
    }

    public void updateSeller(SellerEditForm form){
        this.name = form.getName();
        this.phoneNumber = form.getPhoneNumber();
        this.desc = form.getDesc();
        this.companyId = form.getSellerId();
    }

    public void updateImage(ImageFile image){
        this.imageFile = image;
    }


}
