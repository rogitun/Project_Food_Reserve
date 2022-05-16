package heading.ground.service;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final MenuRepository menuRepository;
    private final BookedMenuRepository bookedMenuRepository;


    public Long createBookMenus(HashMap<Long,Integer> menuSets,Long sid){
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
    public Long createBook(List<BookedMenu> bookMenus, Long studentId, Seller seller) {
        Student student = studentRepository.findById(studentId).get();
        Book book = Book.book(seller, student, bookMenus);
        Book save = bookRepository.save(book);
        return save.getId();
    }

//
//    //TODO 쿼리 최적화
//    public List<BookedMenu> createBookMenus(List<MenuSet> form) {
//        List<BookedMenu> bookedMenus = new ArrayList<>();
//        for (MenuSet menuSet : form) {
//            Menu byName = menuRepository.findByName(menuSet.getName());
//            if(byName.isOutOfStock()) return null;
//            bookedMenus.add(new BookedMenu(byName, menuSet.getQuantity()));
//        }
//        return bookedMenus;
//    }



    @Transactional
    public void process(Long id, boolean flag) {
        Book book = bookRepository.findById(id).get();
        book.processBook(flag);
    }

    @Transactional
    public void rejectBook(Long id, String reason) {
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
}
