package heading.ground.security.jwt;

import heading.ground.security.user.MyUserDetails;
import heading.ground.security.user.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("filter Call");
        //토큰 검사
        String header = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        if(header!=null && header.startsWith("Bearer")){
            jwt = header.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            }
            catch (BadCredentialsException e){
                log.info("BadCredential");
                response.sendError(403,"Not a Valid Token");
            }
        }

        if(username!= null && SecurityContextHolder.getContext().getAuthentication()==null){
            log.info("jwt checking Call");
            MyUserDetails userDetails = (MyUserDetails) myUserDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request,response);
    }
}
