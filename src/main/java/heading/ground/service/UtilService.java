package heading.ground.service;

import heading.ground.dto.util.CartMenuDto;
import heading.ground.dto.util.PwdReset;
import heading.ground.entity.post.Menu;
import heading.ground.entity.user.BaseUser;
import heading.ground.entity.util.CartMenu;
import heading.ground.entity.util.Message;
import heading.ground.entity.util.ShopCart;
import heading.ground.forms.util.MsgForm;
import heading.ground.repository.post.MenuRepository;
import heading.ground.repository.user.UserRepository;
import heading.ground.repository.util.CartMenuRepository;
import heading.ground.repository.util.MessageRepository;
import heading.ground.repository.util.ShopCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtilService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MenuRepository menuRepository;
    private final ShopCartRepository cartRepository;
    private final CartMenuRepository cartMenuRepository;


    @Autowired
    private JavaMailSender javaMailSender;

    @Transactional
    public void makeMsg(MsgForm form) {
        BaseUser receiver = userRepository.findById(form.getReceiverId()).get();
        BaseUser sender = userRepository.findById(form.getSenderId()).get();
        Message message = new Message(sender, receiver, form);
        messageRepository.save(message);
    }


    public List<Message> getMessagesRcv(Long id) {
        return messageRepository.findReceivedMsg(id);
    }

    public List<Message> getMessagesSnd(Long id) {
        return messageRepository.findSentMsg(id);
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
        Optional<ShopCart> optCart = cartRepository.findByUserIdWithMenuList(userId);
        Optional<Menu> optionalMenu = menuRepository.findById(menuId);
        if (optionalMenu.isEmpty()) {
            log.info("User or Menu Not Found on Searching User with ShopCart");
            return;
        }

        ShopCart shopCart = optCart.get();
        Menu menu = optionalMenu.get();
        shopCart.addMenu(menu);
    }

    public boolean cartCheck(Long sellerId, Long menuId) {
        if(sellerId == null) return true;

        boolean l = menuRepository.countBySellerId(sellerId, menuId);
        return l;
    }

    public boolean cartDuplicate(ShopCart cart, Long menuId) {
        return cart.duplicateCheck(menuId);
    }

    public List<CartMenuDto> getCart(Long id) {
        Optional<ShopCart> optional = cartRepository.findByUserIdWithAll(id);
        log.info("optional ={}", optional.isEmpty());
        if (optional.isEmpty()) {
            log.info("Optional Null -Service");
            return null;
        }
        ShopCart shopCart = optional.get();
        String name = userRepository.findUserNameById(shopCart.getSellerId());

        List<CartMenu> menuList = shopCart.getMenuList();
        List<CartMenuDto> cartMenuDtos = menuList.stream().map(
                        m -> CartMenuDto.builder()
                                .id(m.getId())
                                .name(m.getMenu().getName())
                                .isOut(m.getMenu().isOutOfStock())
                                .price(m.getMenu().getPrice())
                                .sellerName(name)
                                .build())
                .collect(Collectors.toList());
        return cartMenuDtos;
    }

    @Transactional
    public int deleteCart(Long id, Long userId) {
        Optional<ShopCart> optUser = cartRepository.findByUserId(userId);
        if (optUser.isEmpty()) {
            log.warn("User Not Found During Deleting Cart From UtilService");
            return 0;
        }
        ShopCart shopCart = optUser.get();

        int cnt = cartMenuRepository.deleteByMenuId(id, shopCart.getId());
        return cnt;
    }

    @Transactional
    public void resetCart(Long studentId) {
        Optional<ShopCart> optUser = cartRepository.findByUserIdWithMenuList(studentId);
        if (optUser.isEmpty()) {
            log.warn("User Not Found During Deleting Cart From UtilService");
            throw new IllegalStateException();
        }
        ShopCart shopCart = optUser.get();
        shopCart.resetCart();
    }
}
