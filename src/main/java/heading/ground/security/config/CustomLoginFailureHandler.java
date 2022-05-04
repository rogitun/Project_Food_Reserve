package heading.ground.security.config;

import heading.ground.entity.user.BaseUser;
import heading.ground.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoginFailureHandler
        implements AuthenticationFailureHandler {

//    private HashMap<String,Integer> userAccess = new HashMap<>();
//
//    @Autowired
//    private MyUserDetailsService userDetailsService;

    private static final int MAX_ATTEMPT = 5;
    private static final int LOCK_TIME = (1000 * 60) * 180; //3시간동안 잠금

   // @Autowired
    private final UserRepository userRepository;

//
    @Transactional
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String username = request.getParameter("loginId");
        String password= request.getParameter("password");
        String error = exception.getMessage();
        log.info("values = {} ,{} ,{}, {} ",username,password,error, exception.getClass());

        if(exception.getClass() == BadCredentialsException.class){ //비밀번호 불일치
            //failedAttempt(username,response);
            BaseUser baseUser = userRepository.findByLoginId(username).get();

            if(baseUser.isNon_locked()) {
                if (baseUser.getFailed_attempt() > MAX_ATTEMPT) { //최대 시도 이상으로 틀렸을경우
                    baseUser.setNon_locked(false); //계정 잠금
                    baseUser.setLock_time(new Date(System.currentTimeMillis() + LOCK_TIME));
                } else //아직 안잠겨있음 => 로그인 시도만 ++
                    baseUser.addFailed_attempt(true);
                response.sendError(400,"Not a Valid Credential");
            }
            else{ //잠겨있음. => 잠김 시간 끝났고 로그인 성공했으면 풀어주는건 successHandler 에서함
                //여기선 잠긴 상태에서 또 로그인 시도 했을경우
                response.sendError(401,"아이디 잠금");
            }
        }
    }
//
//    @Transactional
//    public void failedAttempt(String username,HttpServletResponse response) throws IOException {
//
//    }
}
