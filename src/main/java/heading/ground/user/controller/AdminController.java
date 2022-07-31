package heading.ground.user.controller;

import heading.ground.utils.entity.Category;
import heading.ground.utils.entity.Notice;
import heading.ground.user.repository.UserRepository;
import heading.ground.utils.repository.CategoryRepository;
import heading.ground.main.repository.NoticeRepository;
import heading.ground.utils.service.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CategoryRepository categoryRepository;
    private final NoticeRepository noticeRepository;
    private final UtilService utilService;
    private final UserRepository userRepository;


    @GetMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminPage(){
        return "admin/main";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/add-category")
    public String addCategory(Model model){
        List<Category> all = categoryRepository.findAll();
        model.addAttribute("category",all);
        return "admin/addCategory";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-category")
    public String postCategory(@RequestParam("name") String name){
        Category category = new Category(name);
        categoryRepository.save(category);
        return "redirect:/admin";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/del-category")
    public String delCategory(@PathVariable("id") String id){
        long cid = Long.parseLong(id);
        userRepository.deleteCategory(cid);
        categoryRepository.deleteById(cid);
        return "redirect:/admin";
    }

    //TODO NOTICE;
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/add-notice")
    public String addNotice(){
        return "admin/addNotice";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-notice")
    public String postNotice(@RequestParam("title") String title,
                             @RequestParam("content") String content){
        Notice notice = new Notice(title, content);
        noticeRepository.save(notice);

        return "redirect:/admin/notices";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}/notice-edit")
    public String editNotice(@PathVariable("id") Long id, Model model){
        Optional<Notice> byId = noticeRepository.findById(id);
        if(byId.isEmpty()) throw new IllegalStateException();
        Notice notice = byId.get();

        model.addAttribute("notice",notice);

        return "admin/noticeEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/notice-edit")
    public String editNoticePost(@PathVariable("id") Long id,
                                 @RequestParam("title") String title,
                                 @RequestParam("content") String content){
        utilService.editNotice(id,title,content);
        return "redirect:/admin/notices";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{id}/notice-del")
    public String delNotice(@PathVariable("id") String id){
        noticeRepository.deleteById(Long.parseLong(id));
        return "redirect:/admin/notices";
    }



    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/notices")
    public String notices(Model model){
        List<Notice> all = noticeRepository.findAll();
        model.addAttribute("notice",all);
        return "admin/notices";
    }

}
