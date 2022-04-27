package heading.ground.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //TODO /book, profile, messages -> seller, student
    //TODO /menu -> seller
    //TODO /comment -> student
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/book").hasAnyRole("ADMIN","STUDENT","SELLER")
                .antMatchers("/profile").hasAnyRole("ADMIN","STUDENT","SELLER")
                .antMatchers("/messages").hasAnyRole("ADMIN","STUDENT","SELLER")
                .antMatchers("/menu").hasAnyRole("ADMIN","SELLER")
                .antMatchers("/comment").hasAnyRole("ADMIN","STUDENT")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .usernameParameter("loginId")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/");
    }
}
