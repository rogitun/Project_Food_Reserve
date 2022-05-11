package heading.ground.controller;

import heading.ground.dto.Paging;
import heading.ground.dto.book.BookDto;
import heading.ground.dto.book.MenuListDto;
import heading.ground.dto.post.MenuDto;
import heading.ground.dto.user.SellerDto;
import heading.ground.entity.book.Book;
import heading.ground.entity.book.BookedMenu;
import heading.ground.entity.post.Menu;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.forms.book.BookForm;
import heading.ground.forms.user.LoginForm;
import heading.ground.forms.user.SellerEditForm;
import heading.ground.forms.user.SellerSignUpForm;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.SellerRepository;
import heading.ground.repository.user.UserRepository;
import heading.ground.service.BookService;
import heading.ground.service.PostService;
import heading.ground.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/seller")
public class SellerController {

    private final PostService postService;
    @GetMapping("/{id}/menus") //해당 가게의 전체 메뉴
    public String menus(@PathVariable("id") Long id,
                        Pageable pageable,
                        Model model){
        int page = (pageable.getPageNumber()==0)?0:(pageable.getPageNumber()-1);
        Page<MenuDto> all = postService.pageBySeller(page, 9,id);
        Paging paging = postService.pageTemp(all);
        Optional<MenuDto> first = all.get().findFirst();
        if(first.isPresent())
            model.addAttribute("seller_name",first.get().getSeller());

        model.addAttribute("sid",id);
        model.addAttribute("menus",all);
        model.addAttribute("paging",paging);

        return "post/menusBySeller";
    }


}
