package heading.ground.user.repository;

import heading.ground.GroundApplication;
import heading.ground.booking.entity.post.Menu;
import heading.ground.booking.forms.post.MenuForm;
import heading.ground.booking.repository.post.MenuRepository;
import heading.ground.user.entity.BaseUser;
import heading.ground.user.entity.MyRole;
import heading.ground.user.entity.Seller;
import heading.ground.user.entity.Student;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@SpringBootTest
@Transactional
class UserRepositoryImplTest {

    @Autowired
    UserRepository repository;

    @Autowired
    MenuRepository menuRepository;


    @BeforeEach
    void init(){
        for(int i=1;i<=6;i++){
            Student student = Student.builder()
                    .id("abc" + i)
                    .email("abc@abc.com"+i)
                    .name("hi")
                    .phone("010234534")
                    .pwd("1234")
                    .build();
            Student save = repository.save(student);
        }

        String[] stores = new String[]{"하늘지기","청와삼대","중국관","바지락칼국수","스와레","쇼쿠오쿠",};

        MenuForm form = new MenuForm("menu1", 2500, "asda", "asdasd");
        Menu menu = new Menu(form);

        Seller seller1 = Seller.builder()
                .loginId("abc0")
                .name(stores[0])
                .password("1234")
                .phoneNumber("010-1231-120")
                .email("abc@maf.com0")
                .role(MyRole.SELLER)
                .doro("불암로 112")
                .doroSpce("@@건물 1층")
                .zipCode("11643")
                .companyId("1234455")
                .build();
        repository.save(seller1);
        menu.addSeller(seller1);
        menuRepository.save(menu);
    }

    @Test
    @Rollback(value = false)
    public void qdslTest(){
//        Student student = Student.builder()
//                .id("1")
//                .email("abc@abc.com")
//                .name("hi")
//                .phone("010234534")
//                .pwd("1234")
//                .build();
//        Student save = repository.save(student);
//
//        Student std = repository.findStudentById(save.getId());

      //  Assertions.assertThat(std.getId()).isEqualTo(save.getId());
    }

    @Test
    @Rollback(value = false)
    public void findByEmail(){
        Optional<BaseUser> abc1 = repository.findUserByIdEmail("abc1", "abc@abc.com1");
        Assertions.assertThat(abc1.isEmpty()).isFalse();
    }

    @Test
    @Rollback(value = false)
    public void findSellerByIdwithMenu(){
        Seller seller = repository.findByIdWithMenu(7L);

        Set<Menu> menus = seller.getMenus();
        for (Menu menu : menus) {
            System.out.println(menu.getName() + " " + menu.getPrice());
        }
    }
}