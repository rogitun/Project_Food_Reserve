package heading.ground;

import heading.ground.entity.post.Menu;
import heading.ground.entity.user.MyRole;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.entity.util.Category;
import heading.ground.entity.util.ShopCart;
import heading.ground.forms.post.MenuForm;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.UserRepository;
import heading.ground.repository.util.CategoryRepository;
import heading.ground.repository.util.ShopCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class TestData {

    private final UserRepository us;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ShopCartRepository shopCartRepository;

    @PostConstruct
    @Transactional
    public void init(){

        String[] stores = new String[]{"하늘지기","청와삼대","중국관","바지락칼국수","스와레","쇼쿠오쿠",};

        for(int i=0;i<6;i++){
            Seller seller = Seller.builder()
                    .loginId("abc"+i)
                    .name(stores[i])
                    .password(passwordEncoder.encode("1234"))
                    .phoneNumber("010-1231-12"+i)
                    .email("abc@maf.com"+i)
                    .role(MyRole.SELLER)
                    .doro("불암로 112")
                    .doroSpce("@@건물 1층")
                    .zipCode("11643")
                    .companyId("1234455")
                    .build();
            us.save(seller);
        }

        Student student = Student.builder()
                .id("abcd")
                .pwd(passwordEncoder.encode("1234"))
                .phone("010302131")
                .name("abcd")
                .email("abc@abcd.com")
                .build();

        Student save = us.save(student);
        shopCartRepository.save(new ShopCart(save));
    }
}
