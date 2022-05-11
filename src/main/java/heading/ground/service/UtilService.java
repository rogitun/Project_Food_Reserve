package heading.ground.service;

import heading.ground.dto.util.PwdReset;
import heading.ground.entity.post.Menu;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.user.Student;
import heading.ground.entity.util.Message;
import heading.ground.forms.util.MsgForm;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.UserRepository;
import heading.ground.repository.util.MessageRepository;
import heading.ground.repository.util.ShopCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtilService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MenuRepository menuRepository;
    private final ShopCartRepository cartRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Transactional
    public void makeMsg(MsgForm form) {
        BaseUser receiver = userRepository.findById(form.getReceiverId()).get();
        BaseUser sender = userRepository.findById(form.getSenderId()).get();
        Message message = new Message(sender, receiver, form);
        messageRepository.save(message);
    }


    public List<Message> getMessages(Long id) {
        return messageRepository.findAllMessagesById(id);
    }

    @Transactional
    public void msgRead(Message msg) {
        msg.read();
    }

    public MsgForm makeReply(Message msg) {
        MsgForm msgForm = new MsgForm();
        msgForm.setReply(msg);
        return msgForm;
    }


    //아이디 검증
    @Transactional
    public String pwdCheck(String id, String email) {
        Optional<BaseUser> optional = userRepository.findUserByIdEmail(id, email);
        if (optional.isPresent()) {
            BaseUser baseUser = optional.get();
            if (baseUser.getUuid() != null)
                return baseUser.getUuid();

            String uuid = UUID.randomUUID().toString();
            baseUser.setUUID(uuid);
            return uuid;
        }
        return null;
    }

    @Async
    public void sendMail(String email, String uuid) throws MessagingException {
        String message = createMessage(uuid);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(email);
        helper.setSubject("비밀번호 변경");
        helper.setText(message, true);
        javaMailSender.send(mimeMessage);
    }

    private String createMessage(String uuid) {
        StringBuffer content = new StringBuffer();
        content.append("<!DOCTYPE html>");
        content.append("<html>");
        content.append("<head>");
        content.append("</head>");
        content.append("<body>");
        content.append(
                "<div>" +
                        "<p>아래 URL을 통해 비밀번호를 변경해주세요.</p><br>" +
                        "<a href = http://localhost:8080/reset-password/" + uuid + ">비밀번호 변경</a>"
                        + "</div>");
        content.append("</body>");
        content.append("</html>");
        return content.toString();
    }

    @Transactional
    public String resetPwd(String uuid, PwdReset pwds) {
        //유저 비밀번호 변경하기
        //유저 lock, attempt, uuid 초기화
        Optional<BaseUser> optional = userRepository.findByUuid(uuid);
        if (!pwds.getOne().equals(pwds.getTwo()) ||
                optional.isEmpty())
            return null;

        BaseUser baseUser = optional.get();
        baseUser.changePassword(passwordEncoder.encode(pwds.getOne()));
        return "ok";
    }

    @Transactional
    public void addCart(Long userId, Long menuId) {
        //유저의 장바구니에 해당 메뉴 추가
        //유저의 장바구니에 이미 다른 가게의 아이템이 있는 경우 초기화시키고 다시 추가.
        Optional<Student> optional = userRepository.findByIdForCart(userId);
        Optional<Menu> optionalMenu = menuRepository.findById(menuId);
        if(optional.isEmpty() || optionalMenu.isEmpty()) {
            log.info("User or Menu Not Found on Searching User with ShopCart");
            return;
        }

        Student student = optional.get();
        Menu menu = optionalMenu.get();
        student.addMenuToCart(menu);
    }

    public boolean cartCheck(Student student,Long menuId) {
        Long sellerId = student.getCart().getSellerId();
        Optional<Menu> opt = menuRepository.findMenuByIdWithSeller(menuId, sellerId);
        if(opt.isPresent() || student.getCart().getSellerId() == null)
            return true;
        else return false;
        //True => 동일한 가게의 메뉴를 담음
        //False => 다른 가게의 메뉴를 담음
    }

    public boolean cartDuplicate(Student student, Long menuId) {
        Long cartId = student.getCart().getId();
        cartRepository.findByMenuId(cartId,menuId);


        return true;
    }
}
