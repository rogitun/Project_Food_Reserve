package heading.ground.controller;

import heading.ground.dto.util.CartMenuDto;
import heading.ground.dto.util.PwdCheck;
import heading.ground.dto.util.PwdReset;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Student;
import heading.ground.entity.util.Message;
import heading.ground.entity.util.ShopCart;
import heading.ground.forms.util.MsgForm;
import heading.ground.repository.user.UserRepository;
import heading.ground.repository.util.CartMenuRepository;
import heading.ground.repository.util.MessageRepository;
import heading.ground.repository.util.ShopCartRepository;
import heading.ground.security.user.MyUserDetails;
import heading.ground.service.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//TODO 메세지, 이메일, 쿠폰, 장바구니 등을 관리

@Slf4j
@Controller
@RequiredArgsConstructor
public class UtilController {

    private final UtilService utilService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ShopCartRepository shopCartRepository;

    //TODO 메세지 관련
    @GetMapping("/messages")
    public String messageBox(Model model, @AuthenticationPrincipal MyUserDetails principal) {
        List<Message> messages = utilService.getMessages(principal.getId());

        List<Message> collect = messages.stream()
                .filter(m -> m.getWriter().getId().equals(principal.getId())).collect(Collectors.toList());
        List<Message> receive = messages.stream()
                .filter(m -> m.getWriter().getId() != principal.getId()).collect(Collectors.toList());


        model.addAttribute("snt", collect);
        model.addAttribute("rcv", receive);

        return "message/messageBox";
    }

    @GetMapping("/messages/{id}")
    public String messageDetail(@PathVariable("id") Long id,
                                Model model,
                                @AuthenticationPrincipal MyUserDetails principal) {
        Message message = messageRepository.findGraphById(id).get();
        if (!message.isRead()) {
            utilService.msgRead(message);

        }
        if (principal.getId() != message.getWriter().getId() &&
                principal.getId() != message.getReceiver().getId()) {
            return "redirect:/";
        }

        model.addAttribute("msg", message);

        return "message/message";
    }


    @GetMapping("/message/{id}/send")
    public String messageForm(@PathVariable("id") Long id,
                              Model model,
                              @AuthenticationPrincipal MyUserDetails principal) {
        //id는 수신자, session은 송신자
        if (principal.getRole().equals("STUDENT")) { //학생이 -> 사장에게
            String name = userRepository.findSellerById(id);
            MsgForm msgForm = new MsgForm();
            msgForm.setIds(name, principal.getId(), id);
            model.addAttribute("mid", id);
            model.addAttribute("MsgForm", msgForm);
        } else {
            return "redirect:/";
        }

        return "message/messageForm";
    }

    @GetMapping("/message/{id}/reply")
    public String replyForm(@PathVariable("id") Long id,
                            @AuthenticationPrincipal MyUserDetails principal,
                            Model model) {
        //id는 메세지
        log.info("msg Test ==================");
        Message msg = messageRepository.findGraphById(id).get();
        if (msg.getReceiver().getId() != principal.getId())
            return "redirect:/";

        MsgForm msgForm = utilService.makeReply(msg);
        model.addAttribute("mid", msg.getReceiver().getId());
        model.addAttribute("MsgForm", msgForm);

        return "message/messageForm";
    }

    @PostMapping("/message/{id}/send")
    public String messageSend(@Validated @ModelAttribute("MsgForm") MsgForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("error! {}", bindingResult.toString());
            return "message/messageForm";
        }

        utilService.makeMsg(form);
        return "redirect:/messages";
    }

    @GetMapping("/forget-password")
    public String forgetPwd() {
        return "util/password";
    }

    @ResponseBody
    @PostMapping("/forget-password")
    public String pwdCheck(@RequestBody PwdCheck data,
                           HttpServletResponse res) throws MessagingException, IOException {
        log.info("check data = {}", data);
        String check = utilService.pwdCheck(data.getId(), data.getEmail());
        if (check != null) {
            log.info("유저 존재");
            utilService.sendMail(data.getEmail(), check);
            return "Ok";
        } else {
            res.sendError(401, "notFound");
            return "Error";
        }
    }

    @GetMapping("/reset-password/{id}")
    public String newPwd(@PathVariable("id") String uuid,
                         Model model) {
        model.addAttribute("id", uuid);
        return "util/pwdChange";
    }

    @ResponseBody
    @PostMapping("/reset-password/{id}")
    public String resetPwd(@PathVariable("id") String uuid,
                           @RequestBody PwdReset reset,
                           HttpServletResponse res) throws IOException {

        String s = utilService.resetPwd(uuid, reset);
        if (s == null)
            res.sendError(401, "FailReset");

        return s;
    }

    @ResponseBody
    @PostMapping("/add-cart/{id}/check")
    public String cartCheck(@PathVariable("id") Long menuId, @AuthenticationPrincipal MyUserDetails principal,
                            HttpServletResponse rep) throws IOException {
        Optional<ShopCart> optCart = shopCartRepository.findByUserId(principal.getId());

        if (optCart.isEmpty()) {
            rep.sendError(401, "CartError");
            return "Fail";
        }
        //이미 장바구니에 추가된 경우
//        Student student = opt.get();
        ShopCart cart = optCart.get();
        boolean duplicate = utilService.cartDuplicate(cart, menuId);
        if (duplicate)
            return "Duplicate";

        boolean check = utilService.cartCheck(cart, menuId);
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
