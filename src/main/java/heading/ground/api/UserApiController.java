package heading.ground.api;

import heading.ground.entity.user.BaseUser;
import heading.ground.forms.user.LoginForm;
import heading.ground.security.jwt.JwtUtil;
import heading.ground.security.user.MyUserDetails;
import heading.ground.security.user.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URI;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserApiController {

    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;

//    @PostMapping("/login")
//    public ResponseEntity login(@RequestBody LoginForm form,
//                                HttpServletResponse response) throws Exception {
//        log.info("form = {} ", form);
//        //글로벌 에러처리는 아이디 혹은 비밀번호 존재하지 않음
//        log.info("login call");
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(form.getLoginId(), form.getPassword()));
//            log.info("auth Manager");
//
//        } catch (BadCredentialsException e) {
//            log.info("BadCredential");
//            throw new Exception("BadRequest", e);
//        }
//        final MyUserDetails userDetails = (MyUserDetails) myUserDetailsService.loadUserByUsername(form.getLoginId());
//        final String jwt = jwtUtil.generateToken(userDetails);
//        response.setHeader("Authorization",jwt);
//        return ResponseEntity.ok(jwt);
//    }
}
