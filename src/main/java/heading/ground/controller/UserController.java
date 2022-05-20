package heading.ground.controller;

import heading.ground.dto.book.BookDto;
import heading.ground.dto.post.MenuDto;
import heading.ground.dto.user.SellerDto;
import heading.ground.dto.user.StudentDto;
import heading.ground.entity.book.Book;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.forms.user.BaseSignUp;
import heading.ground.forms.user.LoginForm;
import heading.ground.forms.user.SellerEditForm;
import heading.ground.forms.user.UserEditForm;
import heading.ground.repository.book.BookRepository;
import heading.ground.repository.user.UserRepository;

import heading.ground.security.user.MyUserDetails;
import heading.ground.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
//TODO 로그인, 회원가입 URL 시큐리티 처리 후 나머지는 RequestMapping으로 Profile

    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;

    @GetMapping("/loginForm")
    public String loginForm(@ModelAttribute("user") LoginForm form,
                            @RequestParam(value = "auth", required = false) String err,
                            Model model) {
        model.addAttribute("err", err);
        log.info("error = {}", err);
        return "user/login";
    }

    @GetMapping("/signup")
    public String signUpForm(Model model) {
        model.addAttribute("user", new BaseSignUp());
        return "user/signup";
    }

    @GetMapping("/signup-seller")
    public String sellerSignUpForm(Model model) {
        model.addAttribute("user", new BaseSignUp());
        return "/user/seller-signup";
    }

    //TODO 폼 구분
    @PostMapping("/signup")
    public String signUp(@Validated @ModelAttribute("user") BaseSignUp form,
                         BindingResult bindingResult,
                         @RequestParam(value = "companyId", required = false) String cId) {
        log.info("cid = {} ", cId);

        String x = userService.isValidated(form, bindingResult);
        if (x != null) return x;
        form.setPassword(passwordEncoder.encode(form.getPassword()));
        if (cId != null) {
            userRepository.save(form.toSeller(form));
        } else {
            userRepository.save(form.toStudent(form));
        }

        return "redirect:/loginForm";
    }

    @PostMapping("/fail-login")
    public String failLogin(RedirectAttributes ra) {
        log.info("fail checking");

        ra.addAttribute("auth", "인증-실패");
        return "redirect:/loginForm";
    }

    @GetMapping("/profile") //프로필
    public String profile(Model model,
                          @AuthenticationPrincipal MyUserDetails principal) {

        if (principal.getRole().equals("SELLER")) {
            //TODO MENU는 Best 메뉴만
            //TODO SellerWithMenu & Book 나눠서 가져오기
            Seller seller = (Seller) userRepository.findById(principal.getId()).get();
            List<Book> books = bookRepository.findBySellerId(principal.getId());
            SellerDto sellerDto = new SellerDto(seller);

            if(books!=null){
                List<BookDto> bookDtos = books.stream().map(b -> BookDto.bookDto(b)).collect(Collectors.toList());
                model.addAttribute("books",bookDtos);
            }
            //model.addAttribute("menus", menuDto);
            model.addAttribute("account", sellerDto);


            return "/user/account";
        } else if (principal.getRole().equals("STUDENT")) {
            Student student = userRepository.findStudentByIdForAccount(principal.getId());
            StudentDto studentDto = new StudentDto(student.getId(), student.getName(), student.getEmail());

            //TODO Books service에서 처리하기
            log.info("student_id = {}", student.getId());
            List<Book> booksForStudent = student.getBooks();
            List<BookDto> bookDtos = booksForStudent.stream()
                    .map(b -> new BookDto(b))
                    .collect(Collectors.toList());

            model.addAttribute("student", studentDto);
            model.addAttribute("books", bookDtos);

            return "user/student";
        }
        return "redirect:/";
    }

    //TODO 유저의 정보 수정 또한 동일하다
    @GetMapping("/edit/{id}")  //정보 수정 메서드
    public String editAccount(@PathVariable("id") Long id,
                              @AuthenticationPrincipal MyUserDetails principal,
                              Model model) {
        //TODO 세션으로 접근하는 사람과 수정 대상 데이터의 주인이 일치하는지 확인
        //TODO 세션 유저가 업체인지 학생인지 구분
        if (principal.getRole().equals("SELLER") && principal.getId() == id) {
            Seller seller = (Seller) userRepository.findById(principal.getId()).get();
            SellerEditForm sellerEditForm = new SellerEditForm(seller);
            model.addAttribute("seller", sellerEditForm);
            return "user/edit-seller";
        } else if (principal.getRole().equals("STUDENT") && principal.getId() == id) {
            Student student = (Student) userRepository.findById(principal.getId()).get();
            UserEditForm user = new UserEditForm(student);
            model.addAttribute("user", user);
            return "user/edit-student";
        }
        return "redirect:/";
    }

    @PostMapping("/edit/{id}") //수정 POST
    public String editSeller(@Validated @ModelAttribute("seller") SellerEditForm form, BindingResult bindingResult,
                             @AuthenticationPrincipal MyUserDetails principal,
                             @PathVariable("id") Long id) throws IOException {
        if (bindingResult.hasErrors() || id != principal.getId()) {
            return "user/edit-seller";
        }
        //가게 데이터 변경 처리 + 파일처리
        userService.updateSeller(principal.getId(), form);

        return "redirect:/profile";
    }

    @PostMapping("/edit-student/{id}") //수정 POST
    public String editStudent(@Validated @ModelAttribute("user") UserEditForm form, BindingResult bindingResult,
                              @AuthenticationPrincipal MyUserDetails principal,
                              @PathVariable("id") Long id) throws IOException {
        if (bindingResult.hasErrors() || id != principal.getId()) {
            return "user/edit-student";
        }
        userService.updateStudent(id, form);

        return "redirect:/profile";
    }


    //    @PostMapping("/login")
//    public String login(@Validated @ModelAttribute("user") LoginForm form,
//                        BindingResult bindingResult,
//                        HttpServletRequest req) throws Exception {
//        if (bindingResult.hasErrors()) {
//            log.info("field Error");
//            return "user/login";
//        }
//        log.info("form = {}",form);
//        //글로벌 에러처리는 아이디 혹은 비밀번호 존재하지 않음
//        boolean flag = userService.idValidation(form.getLoginId(), form.getPassword());
//        if (!flag) {
//            bindingResult.reject("NotFound", "아이디 혹은 비밀번호 불일치");
//            return "user/login";
//        }
//        log.info("valid pass");
//        userService.logIn(form.getLoginId(),req);
//
//        return "redirect:/"; //홈화면 이동
//    }
}
