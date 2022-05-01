package heading.ground.security.config;


import heading.ground.security.jwt.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.authorization.AuthorizationWebFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtRequestFilter jwtRequestFilter;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //TODO /book, profile, messages -> seller, student
    //TODO /menu -> seller
    //TODO /comment -> student
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilter(corsFilter());

        http.authorizeRequests()
                .antMatchers("/book").hasAnyRole("ADMIN", "STUDENT", "SELLER")
                .antMatchers("/profile").hasAnyRole("ADMIN", "STUDENT", "SELLER")
                .antMatchers("/messages").hasAnyRole("ADMIN", "STUDENT", "SELLER")
                .antMatchers("/menu").hasAnyRole("ADMIN", "SELLER")
                .antMatchers("/comment").hasAnyRole("ADMIN", "STUDENT")
                .anyRequest().permitAll();
//                .and()
//                .formLogin()
//                .loginPage("/loginForm")
//                .usernameParameter("loginId");
//                .loginProcessingUrl("/login");
    }
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //내 서버가 응답할때 json을 자바스크립트에서 처리할 수 있게 설정하는것
        config.addAllowedOrigin("*"); //모든 ip에 응답 허용
        config.addAllowedHeader("*"); //모든 헤더에 응답 허용
        config.addAllowedMethod("*"); //모든 post,get,put,delete 요청을 허용하겠다.


        source.registerCorsConfiguration("/**",config);
        return new CorsFilter(source);
    }
}
