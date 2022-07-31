package heading.ground;

import heading.ground.booking.forms.post.MenuForm;
import heading.ground.booking.entity.post.Menu;
import heading.ground.booking.repository.post.MenuRepository;
import heading.ground.user.entity.MyRole;
import heading.ground.user.entity.Seller;
import heading.ground.user.entity.Student;
import heading.ground.utils.entity.Category;
import heading.ground.utils.entity.ShopCart;
import heading.ground.user.repository.UserRepository;
import heading.ground.utils.repository.CategoryRepository;
import heading.ground.utils.repository.ShopCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("local")
@Component
@RequiredArgsConstructor
public class TestData {

    private final UserRepository us;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ShopCartRepository shopCartRepository;
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init(){
        String[] stores = new String[]{"하늘지기","청와삼대","중국관","바지락칼국수","스와레","쇼쿠오쿠","아리랑김밥"};

        MenuForm form = new MenuForm("menu1", 2500, "asda", "asdasd");
        Menu menu = new Menu(form);

        Seller seller1 = Seller.builder()
                .loginId("abc0")
                .name(stores[0])
                .password(passwordEncoder.encode("1234"))
                .phoneNumber("010-1231-120")
                .email("abc@maf.com0")
                .role(MyRole.SELLER)
                .doro("불암로 112")
                .doroSpce("@@건물 1층")
                .zipCode("11643")
                .companyId("1234455")
                .build();
        us.save(seller1);
        menu.addSeller(seller1);
        menuRepository.save(menu);

        for(int i=1;i<7;i++){
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

        Category kor = new Category("한식");
        categoryRepository.save(kor);
    }
}
