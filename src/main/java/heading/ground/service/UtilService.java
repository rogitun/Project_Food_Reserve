package heading.ground.service;

import heading.ground.dto.util.PwdReset;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.util.Message;
import heading.ground.forms.util.MsgForm;
import heading.ground.repository.user.UserRepository;
import heading.ground.repository.util.MessageRepository;
import lombok.RequiredArgsConstructor;
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
public class UtilService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BCryptPasswordEncoder passwordEncoder;

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
}
