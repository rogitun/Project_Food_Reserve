package heading.ground.controller;

import heading.ground.api.ShopMenuDto;
import heading.ground.api.dto.BookApiDto;
import heading.ground.api.dto.Payment.PaymentDetails;
import heading.ground.api.dto.Payment.PaymentResponse;
import heading.ground.controller.controllerDto.CartListSuccessResponse;
import heading.ground.dto.book.BookDto;
import heading.ground.dto.book.BookedMenuDto;
import heading.ground.dto.user.SellerDto;
import heading.ground.entity.book.Book;
import heading.ground.entity.book.BookedMenu;
import heading.ground.entity.post.Menu;
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
    private final MenuRepository menuRepository;

    @GetMapping("/test-book")
    public String testbook() {
        return "book/bookDetailDemo";
    }

    @PostMapping("/list-confirm")
    @ResponseBody
    public ResponseEntity<CartListSuccessResponse> listConfirm(@RequestBody List<ShopMenuDto> data,
                                                               @AuthenticationPrincipal MyUserDetails principal) {
        //????????? ???????????? Book ??????
        log.info("data = {} ",data);
        HashMap<Long, Integer> menuSet = new HashMap<>();
        for (ShopMenuDto s : data) {
            menuSet.put(s.getId(), s.getQuantity());
        }
        Long studentId = principal.getId();
        List<Menu> menus = menuRepository.findByIds(menuSet.keySet());
        log.info("menusId = {} ",menus);

        CartListSuccessResponse response;
        if (menus == null) { //null
            log.info("BookController-listConfirm, Menu is Empty = {}", data);
            response = CartListSuccessResponse.builder()
                    .code(400)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("?????? ??? ?????? ??????")
                    .build();
        } else if (bookService.isOutOfStock(menus)) {
            //????????? ????????? ???????????????
            response = CartListSuccessResponse.builder()
                    .code(400)
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("????????? ?????? ??????")
                    .build();
        } else {
            String bookId = bookService.createBookMenus(menus, studentId, menuSet);
            utilService.resetCart(studentId);
            response = CartListSuccessResponse.builder()
                    .code(200)
                    .httpStatus(HttpStatus.OK)
                    .message(bookId)
                    .build();
        }
        //?????? ?????? ????????? ??????
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/{id}/un-paid") //?????? ????????????
    public String bookForPaying(@PathVariable("id") String id, Model model) {
        Book books = bookRepository.findByIdWithMenus(id);
        List<BookedMenuDto> bookedMenuDtos =
                books.getBookedMenus().stream()
                        .map(bm -> new BookedMenuDto(bm.getMenu().getName(),bm.getPrice(),bm.getQuantity()))
                        .collect(Collectors.toList());

        model.addAttribute("menus", bookedMenuDtos);
        model.addAttribute("total", books.getTotalPrice());

        return "book/bookForm";
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/{id}/un-paid")
    @ResponseBody
    public ResponseEntity<PaymentResponse> bookVo(@PathVariable("id") String id,
                                                  @RequestBody BookApiDto bookApiDto,
                                                  @AuthenticationPrincipal MyUserDetails principal) {
        log.info("data ={}", bookApiDto);
        if(bookApiDto.getTypeVal().equals("here")) {
            boolean isDuplicate = bookService.duplicateBookTime(bookApiDto);
            if (isDuplicate) {
                PaymentResponse paymentResponse = PaymentResponse.builder()
                        .code(HttpStatus.UNAUTHORIZED.value())
                        .httpStatus(HttpStatus.UNAUTHORIZED)
                        .message("?????? ????????? ???????????????. ?????? ???????????? ??????????????????.")
                        .paymentDetails(null)
                        .count(0)
                        .build();
                return new ResponseEntity<>(paymentResponse, paymentResponse.getHttpStatus());
            }
        }

        bookService.setDetails(id, bookApiDto);
        PaymentDetails paymentDetails = bookService.paymentDetails(id);

        PaymentResponse paymentResponse;
        if (paymentDetails == null) {
            paymentResponse = PaymentResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("????????? ??????")
                    .paymentDetails(null)
                    .count(0).build();
        } else {
            paymentResponse = PaymentResponse.builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message("?????? ?????? ??????")
                    .paymentDetails(paymentDetails)
                    .count(1).build();
        }

        return new ResponseEntity<>(paymentResponse, paymentResponse.getHttpStatus());
    }

    @GetMapping("/{id}") //?????? ?????? ??????
    public String bookDetail(@PathVariable("id") String id, Model model) {
        Book books = bookRepository.findByIdWithCollections(id);
        List<BookedMenu> bookedMenus = books.getBookedMenus();

        //BookDto book = new BookDto(books);
        BookDto book = BookDto.builder()
                .seller(SellerDto.builder().name(books.getSeller().getName()).build())
                .student(books.getStudent().getName())
                .studentNumber(books.getStudent().getPhoneNumber())
                .bookTime(books.getBookDate())
                .totalPrice(books.getTotalPrice())
                .number(books.getNumber())
                .type(books.getType())
                .status(books.getStatus())
                .isPaid(books.isPaid())
                .build();

        List<BookedMenuDto> bookedMenuDtos =
                bookedMenus.stream()
                        .map(bm -> new BookedMenuDto(
                                bm.getMenu().getName(),
                                bm.getPrice(),
                                bm.getQuantity()))
                        .collect(Collectors.toList());

        model.addAttribute("book", book);
        model.addAttribute("bookedMenus", bookedMenuDtos);

        return "book/bookDetail";
    }

    //TODO ????????? ?????? ????????????
    @ResponseBody
    @PostMapping("/{id}/accept") //?????? ??????(seller)
    public ResponseEntity<String> bookAccept(@PathVariable("id") String id,
                                             @AuthenticationPrincipal MyUserDetails principal) {
        if (principal.getRole().equals("SELLER")) {
            Optional<Book> bookWithSeller = bookRepository.findBookWithSeller(principal.getId(), id);
            if (bookWithSeller.isEmpty()) {
                //??????
                throw new IllegalStateException();
            }
            log.info("id = {} ", id);
            bookService.process(id, true);
            return new ResponseEntity<String>("????????? ?????????????????????.", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("?????? ?????? ??????", HttpStatus.FORBIDDEN);
        }
    }


    @GetMapping("/{id}/reject") //?????? ??????(seller)
    public String bookReject(@PathVariable("id") String id, Model model) {
        //TODO ?????? ?????? + ?????? ?????? input
        Book book = bookRepository.findByIdWithCollections(id);
        List<BookedMenu> bms = book.getBookedMenus();

        BookDto bookDto = BookDto.builder()
                .student(book.getStudent().getName())
                .bookTime(book.getBookDate())
                .type(book.getType())
                .status(book.getStatus())
                .reason("")
                .build();
        List<BookedMenuDto> bookedMenuDtos = bms.stream()
                .map(bm -> new BookedMenuDto(
                        bm.getMenu().getName(),
                        bm.getPrice(),
                        bm.getQuantity()))
                .collect(Collectors.toList());


        model.addAttribute("book", bookDto);
        model.addAttribute("bookedMenus",bookedMenuDtos);

        return "book/bookReject";
    }


    @PostMapping("/{id}/reject") //?????? ??????(seller)
    public String bookRejected(@PathVariable("id") String id,
                               @RequestParam("reason") String reason) {
        //TODO book??? ?????? ?????? update + ????????? bookedMenus ?????? + book.status = Canceled
        bookService.rejectBook(id, reason);

        return "redirect:/profile";
    }

    @PostMapping("/{id}/cancel") //?????? ??????(Student)
    public String bookCancel(@PathVariable("id") String id,@AuthenticationPrincipal MyUserDetails principal) {
        bookService.deleteBook(id,principal.getId());
        return "redirect:/profile";
    }

    @PostMapping("/{id}/cancelRequest")
    public ResponseEntity bookCancelRequest(@PathVariable("id") String id,@AuthenticationPrincipal MyUserDetails principal){
        boolean flag = bookService.requestCancel(id);
        if(flag) return new ResponseEntity(HttpStatus.OK);
        else return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    //TODO ????????? ????????????
    @GetMapping("/{id}/bookRequest") //?????? ???(Student)  ID??? Seller
    public void bookRequest(@PathVariable("id") Long id,
                            HttpServletResponse response) throws IOException {
        long l = bookService.findStock(id);
        log.info("size =  {}", l);
        if (l <= 0) {
            response.sendError(500, "????????? ??????");
        }
    }
}
