package heading.ground.controller;

import heading.ground.dto.Paging;
import heading.ground.dto.post.CommentDto;
import heading.ground.dto.post.MenuDto;
import heading.ground.dto.user.SellerDto;
import heading.ground.entity.post.Comment;
import heading.ground.entity.post.Menu;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.file.FileStore;
import heading.ground.forms.post.CommentForm;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.UserRepository;
import heading.ground.security.user.MyUserDetails;
import heading.ground.service.PostService;
import heading.ground.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final UserService userService;
    private final FileStore fileStore;
    private final UserRepository userRepository;
    private final PostService postService;
    private final MenuRepository menuRepository;

    //TODO Query 문제있음
    @GetMapping("/")
    public String home(Model model, Pageable pageable,
                       @AuthenticationPrincipal MyUserDetails user){
        int page = (pageable.getPageNumber()==0)? 0: (pageable.getPageNumber()-1);
        Page<SellerDto> pages = userService.page(page, 6);
        Paging paging = userService.pageTemp(pages);
        if(user!=null)
            log.info("Current User is = {}, name = {}, Role = {}",user,user.getUsername(),user.getAuthorities());
        model.addAttribute("seller",pages);
        model.addAttribute("paging",paging);

        return "main/home";
    }


    @ResponseBody
    @GetMapping("/image/{image}")
    public Resource showImage(@PathVariable String image) throws MalformedURLException {
        return new UrlResource("file:"+fileStore.getFullPath(image));
    }

    @RequestMapping(value = "/sellerInfo/{id}",method = RequestMethod.GET)
    public String sellerInfo(@PathVariable("id") Long id, Model model) {
        //Seller seller = sellerRepository.findById(id).get();
        //TODO Seller + Menu + Comment
        Seller seller = userRepository.findByIdWithMenuComment(id);
        SellerDto sellerDto = new SellerDto();
        sellerDto.setSellerWithMenus(seller);
        //SellerDto sellerDto = new SellerDto(seller);
        List<MenuDto> menus = sellerDto.getMenus();
        List<MenuDto> best = menus.stream().filter(m -> m.isBest()).collect(Collectors.toList());

        model.addAttribute("seller", sellerDto);
        model.addAttribute("menus", menus);
        model.addAttribute("best", best);
        //TODO 메뉴도 같이 보여줘야 합니다.

        return "user/seller";
    }

    @GetMapping("/menus")
    public String menuList(Model model, Pageable pageable) {
        int page = (pageable.getPageNumber()==0)? 0: (pageable.getPageNumber()-1);
        Page<MenuDto> all = postService.page(page, 9);//현재 인덱스, 보여줄 객체 갯수
        Paging paging = postService.pageTemp(all);

        model.addAttribute("menus", all);
        model.addAttribute("paging",paging);
        // model.addAttribute("paging",paging);
        return "post/menus";
    }

    @GetMapping("/menus/{id}")
    public String singleMenu(@PathVariable("id") Long id, Model model,
                             @AuthenticationPrincipal MyUserDetails principal) {

        CommentForm commentForm = new CommentForm();
        if(principal!=null){
            boolean flag = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("STUDENT"));
            commentForm.setStudent(flag);
        }

        Optional<Menu> optional = menuRepository.findMenuByIdWithCoSe(id);
        if(optional.isEmpty())
            return "redirect:/";
        Menu entity = optional.get();
        MenuDto menu = new MenuDto(entity);

        //댓글 목록 가져오기
        List<Comment> comments = entity.getComments();
        if (comments !=null) {
            List<CommentDto> commentDtos = comments.stream()
                    .map(c -> new CommentDto(c))
                    .collect(Collectors.toList());
            model.addAttribute("comments",commentDtos);
        }

        model.addAttribute("menu", menu);
        model.addAttribute("sellerId", entity.getSeller().getId());
        model.addAttribute("comment", commentForm);

        log.info("menu-comment-size-on-page = {}",entity.getComments().size());
        return "post/menu";
    }
}
