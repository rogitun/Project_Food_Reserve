package heading.ground;

import heading.ground.entity.post.Menu;
import heading.ground.entity.user.MyRole;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.entity.util.Category;
import heading.ground.forms.post.MenuForm;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.UserRepository;
import heading.ground.repository.util.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TestData {

    private final MenuRepository mu;
    private final UserRepository us;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CategoryRepository cr;

    @PostConstruct
    @Transactional
    public void init(){


        Menu menu = new MenuForm("삼계탕", 5500, "막걸리와 함께", "김치랑 밀가루").toEntity();

        Category kor = new Category("한식");
        Category jap = new Category("일식");
        Category chi = new Category("중식");
        cr.save(kor);
        cr.save(jap);
        cr.save(chi);

        Student student = Student.builder()
                .id("abcd")
                .pwd(passwordEncoder.encode("1234"))
                .phone("010302131")
                .name("abcd")
                .email("abc@abcd.com")
                .build();


        Seller seller = Seller.builder()
                .loginId("abc")
                .name("abc")
                .password(passwordEncoder.encode("1234"))
                .phoneNumber("010-1231-1232")
                .email("abc@maf.com")
                .role(MyRole.SELLER)
                .doro("불암로 112")
                .doroSpce("@@건물 1층")
                .zipCode("11643")
                .companyId("1234455")
                .build();

        Seller seller2 = Seller.builder()
                .loginId("abc2")
                .name("abc2")
                .password(passwordEncoder.encode("1234"))
                .phoneNumber("010-1231-1222")
                .email("abc2@maf.com")
                .role(MyRole.SELLER)
                .doro("불암로 233")
                .doroSpce("@@건물 2층")
                .zipCode("11655")
                .companyId("1255665")
                .build();


        seller.updateCategory(kor);
        menu.addSeller(seller2);
        us.save(seller);
        us.save(seller2);
        for(int i=0;i<20;i++){
            Menu temp = new MenuForm("김치전"+i, 4500, "막걸리와 함께", "김치랑 밀가루").toEntity();
            temp.addSeller(seller);
            mu.save(temp);
        }


        mu.save(menu);
        us.save(student);

    }
}
