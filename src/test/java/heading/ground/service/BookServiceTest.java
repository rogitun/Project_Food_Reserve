package heading.ground.service;

import heading.ground.entity.post.Menu;
import heading.ground.entity.user.Seller;
import heading.ground.forms.post.MenuForm;
import heading.ground.repository.book.BookRepository;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private BookService bookService;

    //품절 테스트
    @Test
    @Transactional
    void isOutOfStock() {
        //메뉴 생성, Seller 필요
        String password = passwordEncoder.encode("1234");
        Seller seller = new Seller(new BaseSignUp("aaa", password, password, "test", "test@test.com", "010-1010-0101", "123123123"));
        userRepository.save(seller);

        Menu menu = new Menu(new MenuForm("test1", 2555, "menu", "asdasd"));
        menu.addSeller(seller);
        menuRepository.save(menu);
        //메뉴 품절
        menu.setStock(); //품절상태로 만들기
        //메뉴 검증
        List<Menu> menus = new LinkedList<>();
        menus.add(menu);
        boolean outOfStock = bookService.isOutOfStock(menus);

        System.out.println(outOfStock);
        Assertions.assertEquals(outOfStock,true);
    }

    @Test
    void createBookMenus() {
    }
}