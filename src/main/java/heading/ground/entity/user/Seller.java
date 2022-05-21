package heading.ground.entity.user;

import heading.ground.entity.ImageFile;
import heading.ground.entity.book.Book;
import heading.ground.entity.post.Menu;
import heading.ground.entity.util.Category;
import heading.ground.forms.user.BaseSignUp;
import heading.ground.forms.user.SellerEditForm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.Size;
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
    @Size(max = 30)
    private String companyId; //사업자 번호

    @Lob
    @Column(name="intro",length = 512)
    private String desc;

    //가게가 가진 메뉴를 연관
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "seller",cascade = CascadeType.REMOVE)
    private Set<Menu> menus = new HashSet<>();

    @OneToMany(mappedBy = "seller")
    private List<Book> books = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Seller(BaseSignUp sf) {
        this.loginId = sf.getLoginId();
        this.password = sf.getPassword();
        this.name = sf.getName();
        this.phoneNumber = sf.getPhoneNumber();
        this.companyId = sf.getCompanyId();
        this.role = MyRole.SELLER;
        //TODO 추후 이메일 인증 후에 풀어주도록 로직 설정
        this.email = sf.getEmail();
        this.non_locked = true;
    }

    public void updateSeller(SellerEditForm form,Category category){
        this.name = form.getName();
        this.phoneNumber = form.getPhoneNumber();
        this.desc = form.getDesc();
        this.companyId = form.getSellerId();
        this.category = category;
    }

    public void updateImage(ImageFile image){
        this.imageFile = image;
    }

    public void updateCategory(Category category){
        this.category = category;
        category.getSeller().add(this);
    }
}
