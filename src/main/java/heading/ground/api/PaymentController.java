package heading.ground.api;

import heading.ground.api.vo.PaymentSuccessDetails;
import heading.ground.api.vo.PaymentSuccessResponse;
import heading.ground.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final BookService bookService;

    @PostMapping("/payment-processing/{id}")
    public ResponseEntity<PaymentSuccessResponse> paymentProcessing(
            @PathVariable("id") String bookId,
            @RequestBody PaymentSuccessDetails payment){

        log.info("결제 요청");
        log.info("결제 내역 = {} {}",bookId,payment);
        boolean paymentResult = bookService.checkPayment(payment);
        PaymentSuccessResponse paymentSuccessResponse;
        if(paymentResult && bookId.equals(payment.getMerchant_uid())){
            paymentSuccessResponse = PaymentSuccessResponse.builder()
                    .count(1)
                    .httpStatus(HttpStatus.OK)
                    .code(HttpStatus.OK.value())
                    .paymentSuccessDetails(payment)
                    .build();
            bookService.bookPaid(bookId);
        }
        else{
            paymentSuccessResponse = PaymentSuccessResponse.builder()
                    .count(0)
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .code(HttpStatus.NO_CONTENT.value())
                    .paymentSuccessDetails(payment)
                    .build();
        }
        return new ResponseEntity<>(paymentSuccessResponse,paymentSuccessResponse.getHttpStatus());
    }


//    @ResponseBody
//    @PostMapping("/add-cart/{id}/check")
//    public String cartCheck(@PathVariable("id") Long menuId, @AuthenticationPrincipal MyUserDetails principal,
//                            HttpServletResponse rep) throws IOException {
//        Optional<Student> opt = userRepository.findByIdForCart(principal.getId());
//        if (opt.isEmpty()) {
//            rep.sendError(401, "UserError");
//            return "Fail";
//        }
//        //이미 장바구니에 추가된 경우
//        Student student = opt.get();
//        boolean duplicate = utilService.cartDuplicate(student, menuId);
//        if (duplicate)
//            return "Duplicate";
//
//        boolean check = utilService.cartCheck(student, menuId);
//        if (!check) {
//            //동일한 가게 X
//            return "NotSame";
//        }
//        return "Same";
//    }
//
//    //장바구니에 아이템 추가.
//    @ResponseBody
//    @PostMapping("/add-cart/{id}")
//    public String addCart(@PathVariable("id") Long id,
//                          @AuthenticationPrincipal MyUserDetails principal,
//                          HttpServletResponse rep) throws IOException {
//        if (!principal.getRole().equals("STUDENT"))
//            rep.sendError(403, "AuthorityError");
//
//        utilService.addCart(principal.getId(), id); //유저, 메뉴id
//        //TODO 가게 바뀐 경우 추가
//
//        return "Ok";
//    }
//
//    @PreAuthorize("hasAuthority('STUDENT')")
//    @GetMapping("/cart-list")
//    public String showCart(@AuthenticationPrincipal MyUserDetails principal,
//                           Model model) {
//        List<CartMenuDto> cart = utilService.getCart(principal.getId());
//        if(cart==null || cart.size()<=0){
//            model.addAttribute("empty","empty");
//        }else{
//            CartMenuDto cartMenuDto = cart.get(0);
//            model.addAttribute("seller",cartMenuDto.getSellerName());
//            model.addAttribute("menus", cart);
//        }
//
//        return "/util/shopCart";
//    }
//
//    @PreAuthorize("hasAuthority('STUDENT')")
//    @PostMapping("/cart-list/delete/{id}")
//    @ResponseBody
//    public String deleteCart(@PathVariable("id") Long id,
//                             @AuthenticationPrincipal MyUserDetails principal){
//
//        //해당 장바구니 메뉴가 요청을 한 유저의 장바구니인지 확인 필요함
//        Long userId = principal.getId();
//
//        long cnt = utilService.deleteCart(id,userId);
//        if(cnt<=0){
//            return "None";
//        }
//        return "Ok";
//    }
}
