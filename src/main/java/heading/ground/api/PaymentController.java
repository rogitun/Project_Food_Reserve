package heading.ground.api;

import heading.ground.dto.util.CartMenuDto;
import heading.ground.entity.book.BookedMenu;
import heading.ground.entity.user.Student;
import heading.ground.repository.user.UserRepository;
import heading.ground.security.user.MyUserDetails;
import heading.ground.service.BookService;
import heading.ground.service.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final UtilService utilService;
    private final UserRepository userRepository;
    private final BookService bookService;

//    @GetMapping("/test-pay")
//    public String pay(){
//
//        return "/util/pay";
//    }
//    @ResponseBody
//    @PostMapping("/test-pay")
//    public String paySuccess(@RequestBody testData data){
//        log.info("data = {} ",data);
//
//        return "ok";
//    }
//
//    @ResponseBody
//    @PostMapping("/list-test")
//    public String testone(@RequestBody Map<Integer,Integer> datasList){
//        for (Integer integer : datasList.keySet()) {
//            Integer value = datasList.get(integer);
//            log.info("data = {},{}",integer,value);
//        }
//        return "ok";
//    }

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
        Long bookId = bookService.createBookMenus(menuSet, principal.getId());
        //bookedMenu 만들고 => Book  생성
//        for (BookedMenu bookMenu : bookMenus) {
//            log.info("BookedMenus = {},{},{}",bookMenu.getQuantity(),bookMenu.getPrice(),bookMenu.getMenu().getId());
//        }

        return ResponseEntity.ok(bookId);
    }

    @ResponseBody
    @PostMapping("/add-cart/{id}/check")
    public String cartCheck(@PathVariable("id") Long menuId, @AuthenticationPrincipal MyUserDetails principal,
                            HttpServletResponse rep) throws IOException {
        Optional<Student> opt = userRepository.findByIdForCart(principal.getId());
        if (opt.isEmpty()) {
            rep.sendError(401, "UserError");
            return "Fail";
        }
        //이미 장바구니에 추가된 경우
        Student student = opt.get();
        boolean duplicate = utilService.cartDuplicate(student, menuId);
        if (duplicate)
            return "Duplicate";

        boolean check = utilService.cartCheck(student, menuId);
        if (!check) {
            //동일한 가게 X
            return "NotSame";
        }
        return "Same";
    }

    //장바구니에 아이템 추가.
    @ResponseBody
    @PostMapping("/add-cart/{id}")
    public String addCart(@PathVariable("id") Long id,
                          @AuthenticationPrincipal MyUserDetails principal,
                          HttpServletResponse rep) throws IOException {
        if (!principal.getRole().equals("STUDENT"))
            rep.sendError(403, "AuthorityError");

        utilService.addCart(principal.getId(), id); //유저, 메뉴id
        //TODO 가게 바뀐 경우 추가

        return "Ok";
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/cart-list")
    public String showCart(@AuthenticationPrincipal MyUserDetails principal,
                           Model model) {
        List<CartMenuDto> cart = utilService.getCart(principal.getId());
        if(cart==null || cart.size()<=0){
            model.addAttribute("empty","empty");
        }else{
            CartMenuDto cartMenuDto = cart.get(0);
            model.addAttribute("seller",cartMenuDto.getSellerName());
            model.addAttribute("menus", cart);
        }

        return "/util/shopCart";
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/cart-list/delete/{id}")
    @ResponseBody
    public String deleteCart(@PathVariable("id") Long id,
                             @AuthenticationPrincipal MyUserDetails principal){

        //해당 장바구니 메뉴가 요청을 한 유저의 장바구니인지 확인 필요함
        Long userId = principal.getId();

        long cnt = utilService.deleteCart(id,userId);
        if(cnt<=0){
            return "None";
        }
        return "Ok";
    }
}
