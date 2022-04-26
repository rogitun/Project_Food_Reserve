package heading.ground.controller;

import heading.ground.dto.book.BookDto;
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
import heading.ground.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private final MenuRepository menuRepository;

    @GetMapping("/{id}") //예약 세부 정보
    public String bookDetail(@PathVariable("id")Long id,Model model){
        Book books = bookRepository.findByIdWithCollections(id);
        BookDto book = new BookDto(books);
        model.addAttribute("book",book);

        return "book/bookDetail";
    }

    //TODO 아래는 예약 처리하기
    @PostMapping("/{id}/accept") //예약 수락(seller)
    public String bookAccept(@PathVariable("id") String id,
                             @SessionAttribute("user") BaseUser user){
        Long l = Long.parseLong(id);
        if(user instanceof Seller){
            Seller seller = (Seller)user;
            Optional<Book> bookWithSeller = bookRepository.findBookWithSeller(seller.getId(),l);
            if(bookWithSeller.isEmpty()){
                //ㄴㄴ
                throw new IllegalStateException();
            }
        }
        log.info("id = {} ",id);
        bookService.process(l,true);

        return "redirect:/profile";
    }


    @GetMapping("/{id}/reject") //예약 거절(seller)
    public String bookReject(@PathVariable("id") Long id, Model model){
        //TODO 예약 내용 + 거절 사유 input
        Book book = bookRepository.findByIdWithCollections(id);
        BookDto bookDto = new BookDto(book);
        model.addAttribute("book",bookDto);

        return "book/bookReject";
    }


    @PostMapping("/{id}/reject") //예약 거절(seller)
    public String bookRejected(@PathVariable("id") Long id,
                               @RequestParam("reason") String reason){
        //TODO book에 거절 사유 update + 연관된 bookedMenus 삭제 + book.status = Canceled
        bookService.rejectBook(id,reason);

        return "redirect:/profile";
    }

    @PostMapping("/{id}/cancel") //예약 거절(Student)
    public String bookCancel(@PathVariable("id") Long id){
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

    //@GetMapping("/{id}/book") //예약 폼(Student)  ID는 Seller
    @GetMapping("/{id}/bookForm")
    public String bookForm(@PathVariable("id") Long id, Model model,
                           @SessionAttribute("user") BaseUser std,
                           HttpServletResponse response) throws IOException {
        if (!(std instanceof Student)) {
            return "redirect:/seller/" + id;
        }

        List<Menu> menuList = menuRepository.selectMenuBySeller(id);
        List<MenuListDto> menus = menuList.stream().map(m -> new MenuListDto(m)).collect(Collectors.toList());
        model.addAttribute("form", menus);
        model.addAttribute("sellerId", id);
        return "/book/bookForm";
    }

    //@PostMapping("/{id}/book") //예약 추가(Student), ID는 Seller
    @PostMapping("/{id}/addBook")
    public void addBook(@PathVariable("id") Long id, @RequestBody BookForm form,
                          HttpServletResponse response,
                          @SessionAttribute("user") BaseUser std) throws IOException {

        //TODO 메뉴 품절 체크
        List<BookedMenu> bookMenus = bookService.createBookMenus(form.returnArr());
        if(bookMenus == null)
            response.sendError(500,"메뉴 오류");

        Student student = (Student) std;
        bookService.createBook(bookMenus, student.getId(), id, form);
        return;
    }
}
