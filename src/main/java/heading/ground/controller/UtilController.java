package heading.ground.controller;

import heading.ground.dto.util.CartMenuDto;
import heading.ground.dto.util.PwdCheck;
import heading.ground.dto.util.PwdReset;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Student;
import heading.ground.entity.util.Message;
import heading.ground.forms.util.MsgForm;
import heading.ground.repository.user.UserRepository;
import heading.ground.repository.util.CartMenuRepository;
import heading.ground.repository.util.MessageRepository;
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
}
