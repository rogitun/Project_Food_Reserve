package heading.ground.controller;

import heading.ground.api.ShopMenuDto;
import heading.ground.api.vo.BookVo;
import heading.ground.api.vo.PaymentDetails;
import heading.ground.api.vo.PaymentResponse;
import heading.ground.dto.book.BookDto;
import heading.ground.dto.book.BookedMenuDto;
import heading.ground.dto.book.MenuListDto;
import heading.ground.entity.book.Book;
import heading.ground.entity.book.BookedMenu;
import heading.ground.entity.post.Menu;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.forms.book.BookForm;
import heading.ground.repository.book.BookRepository;
import heading.ground.repository.post.MenuRepository;
import heading.ground.security.user.MyUserDetails;
import heading.ground.service.BookService;
import heading.ground.service.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/book")
public class BookController {
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final UtilService utilService;

    @GetMapping("/test-book")
    public String testbook(){
        return "/book/bookDetailDemo";
    }

    @PostMapping("/list-confirm")
    @ResponseBody
    public ResponseEntity listConfirm(@RequestBody List<ShopMenuDto> data,
                                      @AuthenticationPrincipal MyUserDetails principal){
        //데이터 가공해서 Book 생성
        HashMap<Long,Integer> menuSet = new HashMap<>();
        for (ShopMenuDto s : data) {
            log.info("data = {} ",s);
            menuSet.put(s.getId(),s.getQuantity());
            //menuRepository에서 id 일치하는 메뉴 모두 가져온 다음 BookedMenu로 전환
        }
        Long studentId = principal.getId();
        String bookId = bookService.createBookMenus(menuSet, studentId);
        utilService.resetCart(studentId);

        return ResponseEntity.ok(bookId);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/{id}/un-paid")
    public String bookForPaying(@PathVariable("id") String id,Model model){
        Book books = bookRepository.findByIdWithCollections(id);
        List<BookedMenuDto> bookedMenuDtos = books.getBookedMenus().stream().map(bm -> new BookedMenuDto(bm)).collect(Collectors.toList());
        model.addAttribute("menus",bookedMenuDtos);
        model.addAttribute("total",books.getTotalPrice());

        return "book/bookForm";
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/{id}/un-paid")
    @ResponseBody
    public ResponseEntity<PaymentResponse> bookVo(@PathVariable("id") String id,
                                                  @RequestBody BookVo bookVo,
                                                  @AuthenticationPrincipal MyUserDetails principal){
        log.info("data ={}",bookVo);
        bookService.setDetails(id,bookVo);
        PaymentDetails paymentDetails = bookService.paymentDetails(id);

        PaymentResponse paymentResponse;
        if(paymentDetails==null){
            paymentResponse = PaymentResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("데이터 에러")
                    .paymentDetails(null)
                    .count(0).build();
        }
        else{
            paymentResponse = PaymentResponse.builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message("예약 조회 성공")
                    .paymentDetails(paymentDetails)
                    .count(1).build();
        }

        return new ResponseEntity<>(paymentResponse,paymentResponse.getHttpStatus());
    }

    @GetMapping("/{id}") //예약 세부 정보
    public String bookDetail(@PathVariable("id")String id,Model model){
        Book books = bookRepository.findByIdWithCollections(id);
        BookDto book = new BookDto(books);
        model.addAttribute("book",book);

        return "book/bookDetail";
    }

    //TODO 아래는 예약 처리하기
    @PostMapping("/{id}/accept") //예약 수락(seller)
    public String bookAccept(@PathVariable("id") String id,
                             @AuthenticationPrincipal MyUserDetails principal){
        if(principal.getRole().equals("SELLER")){
            Optional<Book> bookWithSeller = bookRepository.findBookWithSeller(principal.getId(),id);
            if(bookWithSeller.isEmpty()){
                //ㄴㄴ
                throw new IllegalStateException();
            }
        }
        log.info("id = {} ",id);
        bookService.process(id,true);

        return "redirect:/profile";
    }


    @GetMapping("/{id}/reject") //예약 거절(seller)
    public String bookReject(@PathVariable("id") String id, Model model){
        //TODO 예약 내용 + 거절 사유 input
        Book book = bookRepository.findByIdWithCollections(id);
        BookDto bookDto = new BookDto(book);
        model.addAttribute("book",bookDto);

        return "book/bookReject";
    }


    @PostMapping("/{id}/reject") //예약 거절(seller)
    public String bookRejected(@PathVariable("id") String id,
                               @RequestParam("reason") String reason){
        //TODO book에 거절 사유 update + 연관된 bookedMenus 삭제 + book.status = Canceled
        bookService.rejectBook(id,reason);

        return "redirect:/profile";
    }

    @PostMapping("/{id}/cancel") //예약 거절(Student)
    public String bookCancel(@PathVariable("id") String id){
        bookService.rejectBook(id,"사용자가 예약 취소");
        return "redirect:/profile";
    }

    //TODO 아래는 예약하기
    @GetMapping("/{id}/bookRequest") //예약 폼(Student)  ID는 Seller
    public void bookRequest(@PathVariable("id") Long id,
                            HttpServletResponse response) throws IOException {
        long l = bookService.findStock(id);
        log.info("size =  {}",l);
        if (l <= 0) {
            response.sendError(500,"데이터 없음");
        }
    }


//    //@PostMapping("/{id}/book") //예약 추가(Student), ID는 Seller
//    @PostMapping("/{id}/addBook")
//    public void addBook(@PathVariable("id") Long id, @RequestBody BookForm form,
//                          HttpServletResponse response,
//                          @AuthenticationPrincipal MyUserDetails principal) throws IOException {
//
//        //TODO 메뉴 품절 체크
//        List<BookedMenu> bookMenus = bookService.createBookMenus(form.returnArr());
//        if(bookMenus == null)
//            response.sendError(500,"메뉴 오류");
//
//        bookService.createBook(bookMenus, principal.getId(), id, form);
//        return;
//    }
}
