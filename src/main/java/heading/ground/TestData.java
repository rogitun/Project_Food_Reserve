package heading.ground;

import heading.ground.entity.post.Menu;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.entity.util.Category;
import heading.ground.forms.post.MenuForm;
import heading.ground.forms.user.BaseSignUp;
import heading.ground.forms.user.SellerSignUpForm;
import heading.ground.forms.user.StudentForm;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.SellerRepository;
import heading.ground.repository.user.StudentRepository;
import heading.ground.repository.user.UserRepository;
import heading.ground.repository.util.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

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

        BaseSignUp c = new BaseSignUp("abc2","","","abc2","abc2@abc.com","012-0000-0000","1232343");
        BaseSignUp b = new BaseSignUp("abc","","","abc","abc@abc.com","010-0000-0000","1234123");
        BaseSignUp a = new BaseSignUp("abcd","","","abcd","kunyjf@naver.com","011-0000-0000","1234123");
        BaseSignUp test = new BaseSignUp();
        BaseSignUp test2  = new BaseSignUp();
        BaseSignUp test3  = new BaseSignUp();

        Menu menu = new MenuForm("삼계탕", 5500, "막걸리와 함께", "김치랑 밀가루").toEntity();
        a.setPassword(passwordEncoder.encode("1234"));
        b.setPassword(passwordEncoder.encode("1234"));
        c.setPassword(passwordEncoder.encode("1234"));

        Category kor = new Category("한식");
        Category jap = new Category("일식");
        Category chi = new Category("중식");
        cr.save(kor);
        cr.save(jap);
        cr.save(chi);

        Student student = test2.toStudent(a);
        Seller seller = test.toSeller(b);
        Seller seller2  = test3.toSeller(c);

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
