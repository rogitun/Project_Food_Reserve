package heading.ground.booking.service;

import heading.ground.api.dto.BookApiDto;
import heading.ground.api.dto.Payment.PaymentDetails;
import heading.ground.api.dto.Payment.PaymentSuccessDetails;
import heading.ground.booking.entity.book.Book;
import heading.ground.booking.entity.book.BookedMenu;
import heading.ground.booking.entity.post.Menu;
import heading.ground.user.entity.Seller;
import heading.ground.user.entity.Student;
import heading.ground.booking.repository.book.BookRepository;
import heading.ground.booking.repository.book.BookedMenuRepository;
import heading.ground.booking.repository.post.MenuRepository;
import heading.ground.user.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    public boolean isOutOfStock(List<Menu> menus){
        long count = menus.stream().filter(m-> m.isOutOfStock()).count();
        return (count>0)?true:false;
    }


    public String createBookMenus(List<Menu> menus,Long sid,HashMap<Long,Integer> menuSets){
        //품절인 메뉴가 있는지 확인한다.
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

    public void deleteBook(String id,Long studentId) {
        bookedMenuRepository.deleteByBookId(id);
        bookRepository.deleteByStudentId(id,studentId);
    }
    @Transactional
    public void rejectBook(String id, String reason) {
        Book book = bookRepository.findByIdWithCollections(id);
        book.bookReject(reason);
    }

    @Transactional
    public boolean requestCancel(String bid) {
        Optional<Book> byId = bookRepository.findById(bid);
        if(byId.isPresent()){
            Book book = byId.get();
            book.cancelRequest();
            return true;
        }
        else return false;
    }

//    @Transactional
//    public void requestCancel(String id, String reason) {
//        Book book = bookRepository.findByIdWithCollections(id);
//        List<BookedMenu> bookedMenus = book.getBookedMenus();
//        book.bookReject(reason);
//        bookedMenuRepository.deleteAllInBatch(bookedMenus);
//    }

    public long findStock(Long id) {
        return menuRepository.countStock(id);
    }


    public boolean duplicateBookTime(BookApiDto bookApiDto){
        LocalDateTime time = LocalDateTime.parse(bookApiDto.getDateVal() + "T" + bookApiDto.getTimeVal());
        boolean byBookDate = bookRepository.findByBookDate(time);
        return byBookDate;
    }

    @Transactional
    public void setDetails(String id, BookApiDto bookApiDto) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if(bookOpt.isPresent()){
            Book book = bookOpt.get();
            book.setDetail(bookApiDto);
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
