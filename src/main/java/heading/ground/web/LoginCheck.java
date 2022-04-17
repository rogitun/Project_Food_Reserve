package heading.ground.web;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheck implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        if(handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod) handler;
            log.info("handler = {}",hm.getMethod());
            log.info("handler 2 = {}",hm.getReturnType());
        }

        HttpSession session = request.getSession(false);
        if(session==null || session.getAttribute("user")==null){
            response.sendRedirect("/login?redirectFrom="+requestURI);
            return false;
        }
        return true;
    }
}
