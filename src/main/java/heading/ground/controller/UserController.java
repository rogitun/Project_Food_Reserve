package heading.ground.controller;

import heading.ground.dto.book.BookDto;
import heading.ground.dto.post.MenuDto;
import heading.ground.dto.user.SellerDto;
import heading.ground.dto.user.StudentDto;
import heading.ground.entity.book.Book;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.forms.user.BaseSignUp;
import heading.ground.forms.user.LoginForm;
import heading.ground.forms.user.SellerEditForm;
import heading.ground.forms.user.UserEditForm;
import heading.ground.repository.user.UserRepository;
import heading.ground.security.jwt.JwtUtil;
import heading.ground.security.user.MyUserDetails;
import heading.ground.security.user.MyUserDetailsService;
import heading.ground.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
//TODO 로그인, 회원가입 URL 시큐리티 처리 후 나머지는 RequestMapping으로 Profile

    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;

    @GetMapping("/loginForm")
    public String loginForm(@ModelAttribute("user") LoginForm form) {
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


    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm form,
                        HttpServletResponse response,
                        HttpServletRequest req) throws Exception {
        log.info("form = {} ", form);
        //글로벌 에러처리는 아이디 혹은 비밀번호 존재하지 않음
        log.info("login call");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(form.getLoginId(), form.getPassword()));
            log.info("auth Manager");

        } catch (BadCredentialsException e) {
            log.info("BadCredential");
            throw new Exception("BadRequest", e);
        }
        final MyUserDetails userDetails = (MyUserDetails) myUserDetailsService.loadUserByUsername(form.getLoginId());
        final String jwt = jwtUtil.generateToken(userDetails);
        response.setHeader("Authorization", jwt);

        return "redirect:/";
    }
//
//    @PostMapping("/login")
//    public String login(@Validated @ModelAttribute("user") LoginForm form,
//                        BindingResult bindingResult,
//                        HttpServletRequest request,
//                        HttpServletResponse response) throws Exception {
//        if (bindingResult.hasErrors()) {
//            log.info("field Error");
//            return "user/login";
//        }
//        //글로벌 에러처리는 아이디 혹은 비밀번호 존재하지 않음
//        BaseUser user = userService.logIn(form.getLoginId(), form.getPassword());
//        if (user == null) {
//            bindingResult.reject("NotFound", "아이디 혹은 비밀번호 불일치");
//            return "user/loginForm";
//        }
//        log.info("pass");
//        try{
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(form.getLoginId(),form.getPassword()));
//        }catch (BadCredentialsException e){
//            throw new Exception("BadRequest",e);
//        }
//        final MyUserDetails userDetails = (MyUserDetails) myUserDetailsService.loadUserByUsername(form.getLoginId());
//        final String jwt = jwtUtil.generateToken(userDetails);
//        response.setHeader("Authorization",jwt);
//
//        return "redirect:/"; //홈화면 이동
//    }

    @PostMapping("/logout")
    public String logOut(HttpServletRequest request) {
        HttpSession session = request.getSession(false); //세션이 있으면 세션 반환
        if (session != null)
            session.invalidate();
        return "redirect:/";
    }


    @GetMapping("/profile")     //가게 정보로 가는 메서드
    public String profile(Model model,
                          @SessionAttribute(name = "user") BaseUser user) {

        if (user instanceof Seller) {
            log.info("seller");
            //TODO Seller & Menu 한꺼번에 fetch Join으로 가져와서 DTO 전환
            Seller seller = userRepository.findSellerByIdForAccount(user.getId());
            log.info("query track 1 {}", seller);
            SellerDto sellerDto = new SellerDto(seller);
            log.info("query track2");
            List<MenuDto> menuDto = sellerDto.getMenus();
            log.info("query track3");
            List<BookDto> bookDto = sellerDto.getBooks();

            model.addAttribute("books", bookDto);
            model.addAttribute("menus", menuDto);
            model.addAttribute("account", sellerDto);


            return "/user/account";
        }
        if (user instanceof Student) {
            Student student = userRepository.findStudentByIdForAccount(user.getId());
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
                              @SessionAttribute(name = "user", required = false) BaseUser ses,
                              Model model) {
        //TODO 세션으로 접근하는 사람과 수정 대상 데이터의 주인이 일치하는지 확인
        //TODO 세션 유저가 업체인지 학생인지 구분
        if (ses instanceof Seller && ses.getId() == id) {
            Seller seller = (Seller) userRepository.findById(id).get();
            SellerEditForm sellerEditForm = new SellerEditForm(seller);
            model.addAttribute("seller", sellerEditForm);
            return "user/edit-seller";
        } else if (ses instanceof Student && ses.getId() == id) {
            UserEditForm user = new UserEditForm((Student) ses);
            model.addAttribute("user", user);
            return "user/edit-student";
        }
        return "redirect:/";
    }

    @PostMapping("/edit/{id}") //수정 POST
    public String editSeller(@Validated @ModelAttribute("seller") SellerEditForm form, BindingResult bindingResult,
                             @SessionAttribute(name = "user") BaseUser ses,
                             @PathVariable("id") Long id,
                             HttpServletRequest request) throws IOException {
        log.info("tracking 1");
        if (bindingResult.hasErrors() || id != ses.getId()) {
            log.info("bionding = {}", bindingResult);
            return "user/edit-seller";
        }
        log.info("edit tracking 1 ");
        //가게 데이터 변경 처리 + 파일처리
        Seller updatedSeller = userService.updateSeller(ses.getId(), form);
        log.info("edit tracking 2 ");
        //세션 업데이트
        HttpSession session = request.getSession(false);
        log.info("edit tracking 3 ");
        session.setAttribute("user", updatedSeller);
        log.info("edit tracking 4 ");
        return "redirect:/profile";
    }

    @PostMapping("/edit-student/{id}") //수정 POST
    public String editStudent(@Validated @ModelAttribute("user") UserEditForm form, BindingResult bindingResult,
                              @SessionAttribute(name = "user") BaseUser ses,
                              @PathVariable("id") Long id,
                              HttpServletRequest request) throws IOException {
        if (bindingResult.hasErrors() || id != ses.getId()) {
            return "user/edit-student";
        }
        Student student = userService.updateStudent(id, form);
        HttpSession session = request.getSession();
        session.setAttribute("user", student);

        return "redirect:/profile";
    }

}
