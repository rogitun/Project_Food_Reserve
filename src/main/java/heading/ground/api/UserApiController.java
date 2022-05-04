package heading.ground.api;


import heading.ground.security.user.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;



@Slf4j
@RequiredArgsConstructor
public class UserApiController {

    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;
//
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
//        return ResponseEntity.ok(jwt);
//    }
}
