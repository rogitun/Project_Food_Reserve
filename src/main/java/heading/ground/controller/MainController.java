package heading.ground.controller;

import heading.ground.dto.Paging;
import heading.ground.dto.post.CommentDto;
import heading.ground.dto.post.MenuDto;
import heading.ground.dto.user.SellerDto;
import heading.ground.entity.post.Comment;
import heading.ground.entity.post.Menu;
import heading.ground.entity.user.Seller;
import heading.ground.entity.util.Category;
import heading.ground.entity.util.Notice;
import heading.ground.file.FileStore;
import heading.ground.forms.post.CommentForm;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.UserRepository;
import heading.ground.repository.util.CategoryRepository;
import heading.ground.repository.util.NoticeRepository;
import heading.ground.security.user.MyUserDetails;
import heading.ground.service.PostService;
import heading.ground.service.UserService;
import heading.ground.service.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private final PostService postService;
    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final NoticeRepository noticeRepository;
    private final UtilService utilService;

    //TODO Query 문제있음
    @GetMapping("/")
    public String home(Model model, Pageable pageable,
                       @AuthenticationPrincipal MyUserDetails user,
                       @RequestParam(required = false, name = "keyWord") String key,
                       @RequestParam(required = false, name = "category") String cat) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        log.info("pageable = {}", pageable);
        Page<SellerDto> pages;


        if (cat != null) {
            pages = userService.searchCategory(page, 6, cat);
        } else if (key != null) {
            pages = userService.searchPage(page, 6, key);
        } else {
            pages = userService.page(page, 6);
        }
        Paging paging = userService.pageTemp(pages, key, cat);
        if (user != null)
            log.info("Current User is = {}, name = {}, Role = {}", user, user.getUsername(), user.getRole());
        List<Category> all = categoryRepository.findAll();

        log.info("pages = {}", pages);

        model.addAttribute("category", all);
        model.addAttribute("seller", pages);
        model.addAttribute("paging", paging);

        return "main/home";
    }

    @ResponseBody
    @GetMapping("/image/{image}")
    public Resource showImage(@PathVariable String image) throws MalformedURLException {
        return new UrlResource("file:imageUpload/" + image);
        //return new UrlResource("file:imageUpload/" + fileStore.getFullPath(image));
    }

    @GetMapping("/notices")
    public String notices(Model model, Pageable pageable) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        Page<Notice> notices = utilService.pageNotice(page, 10);
        Paging paging = utilService.pageTemp(notices);
        log.info("size = {}",notices.getTotalElements());
        model.addAttribute("notices", notices);
        model.addAttribute("paging", paging);
        return "main/notices";
    }

    @GetMapping("/notices/{id}")
    public String notice(@PathVariable("id") Long id,
                         Model model, HttpServletResponse rsp) throws IOException {
        Optional<Notice> byId = noticeRepository.findById(id);
        if(byId.isEmpty()){
            rsp.sendError(404,"NotFound");
        }
        Notice notice = byId.get();
        model.addAttribute("notice",notice);
        return "main/notice";
    }

    @GetMapping("/menus")
    public String menuList(Model model, Pageable pageable,
                           @RequestParam(required = false, name = "keyWord") String key) {
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        Page<MenuDto> all;
        if (key == null) all = postService.page(page, 9);
        else all = postService.searchPage(page, 9, key);//현재 인덱스, 보여줄 객체 갯수
        Paging paging = postService.pageTemp(all, key);

        model.addAttribute("menus", all);
        model.addAttribute("paging", paging);
        return "post/menus";
    }

    @GetMapping("/menus/{id}")
    public String singleMenu(@PathVariable("id") Long id, Model model,
                             @AuthenticationPrincipal MyUserDetails principal) {

        CommentForm commentForm = new CommentForm();
        if (principal != null) {
            boolean flag = principal.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("STUDENT"));
            commentForm.setStudent(flag);
        }

        Optional<Menu> optional = menuRepository.findMenuByIdWithCoSe(id);
        log.info("AFter Cose");
        if (optional.isEmpty())
            return "redirect:/";
        Menu entity = optional.get();
        log.info("After get");
        MenuDto menu = new MenuDto(entity);

        //댓글 목록 가져오기
        List<Comment> comments = entity.getComments();
        if (comments != null) {
            List<CommentDto> commentDtos = comments.stream()
                    .map(c -> CommentDto.builder()
                            .id(c.getId())
                            .writer(c.getWriter().getName())
                            .desc(c.getDesc())
                            .star(c.getStar())
                            .writerId(c.getWriter().getLoginId())
                            .build())
                    .collect(Collectors.toList());
            model.addAttribute("comments", commentDtos);
        }
        model.addAttribute("menu", menu);
        model.addAttribute("sellerId", entity.getSeller().getId());
        model.addAttribute("comment", commentForm);

        return "post/menu";
    }
}
