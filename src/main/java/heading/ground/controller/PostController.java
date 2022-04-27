package heading.ground.controller;

import heading.ground.dto.post.CommentDto;
import heading.ground.dto.post.MenuDto;
import heading.ground.dto.Paging;
import heading.ground.dto.post.MenuManageDto;
import heading.ground.entity.post.Comment;
import heading.ground.entity.post.Menu;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.forms.post.CommentForm;
import heading.ground.forms.post.MenuForm;
import heading.ground.repository.post.CommentRepository;
import heading.ground.repository.post.MenuRepository;
import heading.ground.security.user.MyUserDetails;
import heading.ground.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Console;
import java.io.IOException;
import java.util.List;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//메뉴, 댓글, 리뷰 등을 관리합니다.

/**
 * 메뉴는 /menus/id -> 사진 1개
 * 리뷰는 /menus/id/review -> 사진 다량
 * 댓글은 /menus/id/comment -> 사진 없음
 */
@Slf4j
@Controller
@RequestMapping("/menu")
@RequiredArgsConstructor
public class PostController {
    private final MenuRepository menuRepository;
    private final PostService postService;
    private final CommentRepository commentRepository;

    @GetMapping("/add-menu")
    public String menuForm(Model model,
                           @SessionAttribute("user") BaseUser user) { //메뉴 폼
        if(!(user instanceof Seller))
            return "redirect:/";
        model.addAttribute("menu", new MenuForm());
        return "post/menuForm";
    }

    @PostMapping("/add-menu")
    public String addMenu(@Validated @ModelAttribute("menu") MenuForm form, BindingResult bindingResult,
                          @SessionAttribute(name = "user") BaseUser seller) throws IOException { //메뉴 폼
        if (bindingResult.hasErrors()) {
            return "post/menuForm";
        }
        Seller se = (Seller) seller;
        postService.addMenu(form, se);

        return "redirect:/profile";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model,
                           @SessionAttribute("user") BaseUser user) {
        Optional<Menu> optionalMenu = menuRepository.findMenuByIdWithSeller(id, user.getId());
        //Menu menu = menuRepository.findById(id).get();
        if(optionalMenu.isEmpty())
            return "redirect:/";
        Menu menu = optionalMenu.get();
        MenuForm form = new MenuForm(menu);
        model.addAttribute("menu", form);

        return "post/menuForm";
    }

    @PostMapping("/{id}/edit")
    public String editMenu(@Validated @ModelAttribute("menu") MenuForm form, BindingResult bindingResult,
                           @PathVariable("id") Long id,
                           @SessionAttribute("user") BaseUser user) throws IOException {
        Optional<Menu> optionalMenu = menuRepository.findMenuByIdWithSeller(id, user.getId());
        if(optionalMenu.isEmpty())
            return "redirect:/";
        Menu menu = optionalMenu.get();
       // Menu menu = menuRepository.findById(id).get();
        postService.updateMenu(menu, form);
        return "redirect:/profile";
    }



    @GetMapping("/manage")
    public String managingMenus(Model model,
                                @SessionAttribute("user") BaseUser user){
        if(!(user instanceof Seller))
            return "redirect:/";
        List<Menu> menus = menuRepository.findMenusBySellerId(user.getId());
        List<MenuManageDto> menuDtos = menus.stream().map(m -> new MenuManageDto(m)).collect(Collectors.toList());
        model.addAttribute("menus",menuDtos);
        return "/post/manage-menu";
    }

    @PostMapping("/{id}/status")
    public String setStatus(@PathVariable("id") Long id,
                            @RequestBody Map<String,String> data){

        log.info("controller In");
        String flag = data.get("data");
        postService.setMenuStatus(id,flag);
        return "redirect:/menu/manage";
    }

    @PostMapping("/{id}/del")
    public String delMenu(@PathVariable("id") Long id,
                          @SessionAttribute("user") BaseUser user){
        Optional<Menu> optionalMenu = menuRepository.findMenuByIdWithSeller(id, user.getId());
        if(optionalMenu.isEmpty()){
            return null;
        }
        menuRepository.deleteById(id);
        return "redirect:/menu/manage";
    }


    //TODO 댓글 고려사항
    /**
     * 1. 댓글은 메뉴와 사용자의 연관관계를 가진다.
     * 2. 댓글과 메뉴는 M:1 / 사용자도 M:1
     */
    @PostMapping("/{id}/add-comment")
    public String addComment(@PathVariable("id") Long id,
                             @Validated @ModelAttribute("comment") CommentForm form,
                             BindingResult bindingResult, HttpServletResponse response,
                             @AuthenticationPrincipal MyUserDetails principal) throws IOException {
        log.info("form = {}", form);
        if (bindingResult.hasErrors()) {
            log.info("result = {} ", bindingResult.toString());
            return "redirect:/menus/" + id;
        }

        try{
            Long sId = principal.getId();
            Comment comment = new Comment(form);
            postService.addComment(sId, comment, id);
        }catch (Exception e){
            response.sendError(500,"ServerError");
        }finally {
            return "redirect:/menus/" + id;
        }
    }

    @PostMapping("/{id}/del-comment")
    public String delComment(@PathVariable("id") Long id,
                             @AuthenticationPrincipal MyUserDetails principal){
        //TODO comment - User 같이
        Optional<Comment> comment = commentRepository.findByIdWithUser(id, principal.getId());
        if(comment.isEmpty())
            return "/"; //TODO 잘못된 접근 처리하기
        Comment cmt = comment.get();
        Long menuId = cmt.getMenu().getId();
        commentRepository.delete(cmt);
        return "redirect:/menus/" + menuId;
    }

}
