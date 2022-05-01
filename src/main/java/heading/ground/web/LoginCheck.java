package heading.ground.web;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheck implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String authorization = response.getHeader("Authorization");
        log.info("postHandle = {} ",authorization);

        response.setHeader("Authorization",authorization);
    }
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String requestURI = request.getRequestURI();
//
//        if(handler instanceof HandlerMethod){
//            HandlerMethod hm = (HandlerMethod) handler;
//            log.info("handler = {}",hm.getMethod());
//            log.info("handler 2 = {}",hm.getReturnType());
//        }
//
//        HttpSession session = request.getSession(false);
//        if(session==null || session.getAttribute("user")==null){
//            response.sendRedirect("/login?redirectFrom="+requestURI);
//            return false;
//        }
//        return true;
//    }

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String requestURI = request.getRequestURI();
//
//        if (handler instanceof HandlerMethod) {
//            HandlerMethod hm = (HandlerMethod) handler;
//            log.info("handler = {}", hm.getMethod());
//            log.info("handler 2 = {}", hm.getReturnType());
//        }
//
//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("user") == null) {
//            response.sendRedirect("/login?redirectFrom=" + requestURI);
//            return false;
//        }
//        return true;
//    }
}
