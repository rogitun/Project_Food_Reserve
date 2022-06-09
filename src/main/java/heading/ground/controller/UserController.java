package heading.ground.controller;

import heading.ground.dto.book.BookDto;
import heading.ground.dto.post.MenuDto;
import heading.ground.dto.user.SellerDto;
import heading.ground.dto.user.StudentDto;
import heading.ground.entity.ImageFile;
import heading.ground.entity.book.Book;
import heading.ground.entity.post.Menu;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.entity.util.Category;
import heading.ground.forms.user.*;
import heading.ground.repository.book.BookRepository;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.UserRepository;

import heading.ground.repository.util.CategoryRepository;
import heading.ground.security.user.MyUserDetails;
import heading.ground.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final MenuRepository menuRepository;

    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/login";
    }

    @GetMapping("/signup")
    public String signUpForm() {
        return "user/signup";
    }

    @GetMapping("/signup-seller")
    public String sellerSignUpForm() {
        return "/user/seller-signup";
    }

    @GetMapping("/profile") //프로필
    public String profile(Model model,
                          @AuthenticationPrincipal MyUserDetails principal) {
        Pageable pageRequest = PageRequest.of(0, 10);
        if (principal.getRole().equals("SELLER")) {
            //TODO MENU는 Best 메뉴만
            //TODO SellerWithMenu & Book 나눠서 가져오기
            Seller seller = (Seller) userRepository.findById(principal.getId()).get();
            List<Menu> bestMenus = menuRepository.findBestMenusBySellerId(seller.getId(), PageRequest.of(0, 4));
            List<Book> books = bookRepository.findBySellerId(principal.getId(), pageRequest);
            SellerDto sellerDto = SellerDto.builder()
                    .id(seller.getId())
                    .name(seller.getName())
                    .phoneNumber(seller.getPhoneNumber())
                    .desc(seller.getDesc())
                    .doro(seller.getAddress().getDoro())
                    .doro_spec(seller.getAddress().getDoro_spec())
                    .photo((seller.getImageFile() != null) ? seller.getImageFile().getStoreName() : null)
                    .build();

            if (books != null) {
                List<BookDto> bookDtos = books.stream()
                        .map(b -> BookDto.builder()
                                .id(b.getId())
                                .seller(SellerDto
                                        .builder()
                                        .id(b.getSeller().getId())
                                        .name(b.getSeller().getName())
                                        .build())
                                .student(b.getStudent().getName())
                                .bookTime(b.getBookDate())
                                .status(b.getStatus())
                                .type(b.getType())
                                .isPaid(b.isPaid())
                                .reason(b.getReason())
                                .number(b.getNumber())
                                .build())
                        .collect(Collectors.toList());
                model.addAttribute("books", bookDtos);
            }
            if (bestMenus != null) {
                List<MenuDto> menuDtos = bestMenus.stream().map(m -> new MenuDto(m)).collect(Collectors.toList());
                model.addAttribute("menus", menuDtos);
            }
            model.addAttribute("account", sellerDto);

            return "/user/account";
        } else if (principal.getRole().equals("STUDENT")) {
            //Student student = userRepository.findStudentByIdForAccount(principal.getId());
            Optional<BaseUser> byId = userRepository.findById(principal.getId());
            if (byId.isEmpty()) {
                log.info("Not Found User in Profile");
                throw new IllegalStateException();
            }
            BaseUser student = byId.get();
            List<Book> book = bookRepository.findByStudentId(principal.getId(), pageRequest);
            StudentDto studentDto = new StudentDto(student.getId(), student.getName(), student.getEmail());

            //TODO Books service에서 처리하기
            // List<Book> booksForStudent = student.getBooks();
            List<BookDto> bookDtos = book.stream()
                    .map(b -> BookDto.builder()
                            .seller(SellerDto
                                    .builder()
                                    .id(b.getSeller().getId())
                                    .name(b.getSeller().getName())
                                    .photo((b.getSeller().getImageFile() == null) ?
                                            null : b.getSeller().getImageFile().getStoreName())
                                    .build())
                            .bookTime(b.getBookDate())
                            .status(b.getStatus())
                            .type(b.getType())
                            .isPaid(b.isPaid())
                            .totalPrice(b.getTotalPrice())
                            .reason(b.getReason())
                            .id(b.getId())
                            .build())
                    .collect(Collectors.toList());

            model.addAttribute("student", studentDto);
            model.addAttribute("books", bookDtos);

            return "user/student";
        }
        return "redirect:/";
    }

    //TODO 유저의 정보 수정 또한 동일하다
    @GetMapping("/edit")  //정보 수정 메서드
    public String editAccount(@AuthenticationPrincipal MyUserDetails principal,
                              Model model) {
        //TODO 세션으로 접근하는 사람과 수정 대상 데이터의 주인이 일치하는지 확인
        //TODO 세션 유저가 업체인지 학생인지 구분
        if (principal.getRole().equals("SELLER")) {
            Seller seller = (Seller) userRepository.findById(principal.getId()).get();
            SellerEditForm sellerEditForm = new SellerEditForm(seller);
            List<Category> catall = categoryRepository.findAll();

            model.addAttribute("categories", catall);
            model.addAttribute("seller", sellerEditForm);
            return "user/edit-seller";
        } else if (principal.getRole().equals("STUDENT")) {
            Student student = (Student) userRepository.findById(principal.getId()).get();
            UserEditForm user = new UserEditForm(student);
            model.addAttribute("user", user);
            return "user/edit-student";
        }
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @GetMapping("/edit-photo")  //정보 수정 메서드
    public String editPhoto(@AuthenticationPrincipal MyUserDetails principal,
                            Model model) {
        //TODO 세션으로 접근하는 사람과 수정 대상 데이터의 주인이 일치하는지 확인
        //TODO 세션 유저가 업체인지 학생인지 구분
        Seller seller = (Seller) userRepository.findById(principal.getId()).get();
        if(seller.getImageFile()!=null) model.addAttribute("image",seller.getImageFile().getOriginName());

        return "user/edit-photo";
    }

    @PreAuthorize("hasAuthority('SELLER')")
    @PostMapping("/edit-photo")  //정보 수정 메서드
    public String editPhotoPost(@AuthenticationPrincipal MyUserDetails principal,
                                @RequestBody MultipartFile image) throws IOException {
        userService.updatePhoto(principal.getId(),image);

        return "redirect:/profile";
    }

    @PostMapping("/edit") //수정 POST
    @ResponseBody
    public String editSeller(@RequestBody SellerEditForm form,
                             @AuthenticationPrincipal MyUserDetails principal){
        log.info("form ={}",form);
        userService.updateSeller(principal.getId(), form);

        return "ok";
    }

    @ResponseBody
    @PostMapping("/edit-student") //수정 POST
    public String editStudent(@RequestBody UserEditForm form,
                              @AuthenticationPrincipal MyUserDetails principal){
        log.info("form 1 ={}", form);
        if (form.getId() == principal.getId())
            userService.updateStudent(principal.getId(), form);

        return "ok";
    }

}
