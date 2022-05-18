package heading.ground.service;

import heading.ground.api.vo.BookVo;
import heading.ground.api.vo.PaymentDetails;
import heading.ground.api.vo.PaymentSuccessDetails;
import heading.ground.dto.book.BookedMenuDto;
import heading.ground.dto.book.MenuListDto;
import heading.ground.entity.book.Book;
import heading.ground.entity.book.BookedMenu;
import heading.ground.entity.post.Menu;
import heading.ground.entity.user.Seller;
import heading.ground.entity.user.Student;
import heading.ground.forms.book.BookForm;
import heading.ground.forms.book.MenuSet;
import heading.ground.repository.book.BookRepository;
import heading.ground.repository.book.BookedMenuRepository;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.SellerRepository;
import heading.ground.repository.user.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final MenuRepository menuRepository;
    private final BookedMenuRepository bookedMenuRepository;


    public String createBookMenus(HashMap<Long,Integer> menuSets,Long sid){
        List<Menu> menus = menuRepository.findByIds(menuSets.keySet());
        if(menus.isEmpty()){
            log.info("No Menus From Cart List");
            throw new IllegalStateException();
        }
        Seller seller = menus.get(0).getSeller();
        List<BookedMenu> bookedMenus = menus.stream().map(m -> new BookedMenu(m, menuSets.get(m.getId()))).collect(Collectors.toList());

        return createBook(bookedMenus,sid,seller);
    }

    @Transactional
    public String createBook(List<BookedMenu> bookMenus, Long studentId, Seller seller) {
        Student student = studentRepository.findById(studentId).get();
        Book book = Book.book(seller, student, bookMenus);
        Book save = bookRepository.save(book);
        return save.getId();
    }

    @Transactional
    public void process(String id, boolean flag) {
        Book book = bookRepository.findById(id).get();
        book.processBook(flag);
    }

    @Transactional
    public void rejectBook(String id, String reason) {
        Book book = bookRepository.findByIdWithCollections(id);
        List<BookedMenu> bookedMenus = book.getBookedMenus();
        book.bookReject(reason);
        bookedMenuRepository.deleteAllInBatch(bookedMenus);
    }

    public long checkStock(List<MenuListDto> menus) {
        return menus.stream().filter(m -> !(m.isOut())).count();
    }

    public long findStock(Long id) {
        return menuRepository.countStock(id);
    }

    @Transactional
    public void setDetails(String id, BookVo bookVo) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if(bookOpt.isPresent()){
            Book book = bookOpt.get();
            book.setDetail(bookVo);
        }
        else{
            throw new IllegalStateException();
        }
    }

    public PaymentDetails paymentDetails(String id) {
        Optional<Book> bookOpt = bookRepository.findByIdWithUser(id);
        if(bookOpt.isEmpty()){
            return null;
        }
        Book book = bookOpt.get();
        PaymentDetails paymentDetails = new PaymentDetails(book);

        return paymentDetails;
    }

    public boolean checkPayment(PaymentSuccessDetails payment) {
        String bid = payment.getMerchant_uid();
        int amount = payment.getAmount();
        Optional<Book> bookOpt = bookRepository.findByIdPrice(bid, amount);
        if(bookOpt.isEmpty())return false;
        else return true;
    }

    @Transactional
    public void bookPaid(String bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Book book = bookOpt.get();
        book.bookPaid();
    }
}
