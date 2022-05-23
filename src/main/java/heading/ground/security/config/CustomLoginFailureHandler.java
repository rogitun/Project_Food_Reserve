package heading.ground.security.config;

import heading.ground.entity.user.BaseUser;
import heading.ground.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoginFailureHandler
        implements AuthenticationFailureHandler {

    private static final int MAX_ATTEMPT = 5;
    private static final int LOCK_TIME = (1000 * 60) * 180; //3시간동안 잠금

    // @Autowired
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String username = request.getParameter("loginId");
        String password = request.getParameter("password");
        String error = exception.getMessage();

        Optional<BaseUser> reqUser = userRepository.findByLoginId(username);
        if(reqUser.isEmpty()){
            response.sendError(403,"NotFound");
            return;
        }
        BaseUser baseUser = reqUser.get();
        if (exception.getClass() == BadCredentialsException.class) { //비밀번호 불일치
            int errorCode = badCredential(baseUser);
            response.sendError(errorCode,"BadCredential");
        }
        else if (exception.getClass() == LockedException.class) {
            //잠긴 상태에서 또 로그인 시도 했을경우
            int statusCode = lockControl(response, baseUser);
            response.sendError(statusCode,"lockControl");
        }
    }

    private int lockControl(HttpServletResponse response, BaseUser baseUser){
        boolean unlock = unlock(baseUser);
        if(unlock){
            return 302;
        }
        int l = (int) ((baseUser.getLock_time().getTime() - System.currentTimeMillis()) / 1000);
        int hour = (l/3600);
        int min = (l/ 60)-(hour*60);
        int sec = (l-(hour*3600)-(min*60));

        String lockTime = hour + ":" + min + ":" + sec;
        Cookie cookie = new Cookie("lockTime",lockTime);
        cookie.setPath("/loginForm");
        response.addCookie(cookie);
        return 402;
    }

    private int badCredential(BaseUser baseUser){
        if (baseUser.isNon_locked()) {
            baseUser.addFailed_attempt(true);
            if (baseUser.getFailed_attempt() >= MAX_ATTEMPT) { //최대 시도 이상으로 틀렸을경우
                baseUser.setNon_locked(false); //계정 잠금
                baseUser.setLock_time(new Date(System.currentTimeMillis() + LOCK_TIME));
            }
        }
        return 403;
    }

    public boolean unlock(BaseUser baseUser) {
        long userLockTime = baseUser.getLock_time().getTime();
        long currentTimeMillis = System.currentTimeMillis();

        if(userLockTime <= currentTimeMillis){
            baseUser.setNon_locked(true);
            baseUser.setLock_time(null);
            baseUser.addFailed_attempt(false);
            return true;
        }
        return false;
    }
}
