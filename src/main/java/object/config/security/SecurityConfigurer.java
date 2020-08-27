package object.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth
                .userDetailsService(myUserDetailService).passwordEncoder(getPasswordEncoder());

//////////////////////////////////////////////////////////////////////
//        auth
//                .jdbcAuthentication()
//                .dataSource(dataSource)
//                .usersByUsernameQuery("SELECT email, password, is_moderator FROM Users  WHERE email=?")
//                .authoritiesByUsernameQuery("SELECT email, is_moderator FROM Users WHERE email=?");
////////////////////////////////////////////////////////////////////////
//        auth
//                    .inMemoryAuthentication()
//                    .withUser("lol")
//                    .password("lol")
//                    .roles("USER")
//                .and()
//                    .withUser("lilo")
//                    .password("lilo")
//                    .roles("ADM");
////////////////////////////////////////////////////////////////////////////
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
        //return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers( "/api/post/moderation", "/api/moderation", "/api/settings").hasAuthority("ADMIN")
                .antMatchers(" /api/post/my",  "/api/image", " /api/comment", "/api/profile/my", "/api/statistics/my", "/api/auth/logout").hasAnyAuthority("USER", "ADMIN")
                .antMatchers( "/**", "/api/post/{id}", "/api/tag/", "/api/post/byTag", "/api/post/byDate", "/api/post/search",
                        "/api/auth/login", "/api/init", "/api/auth/check", "/api/auth/restore", "/api/auth/password", "/api/auth/register",
                        "/api/auth/captcha", "/api/statistics/all", "/api/settings", "/api/calendar").permitAll()
                .anyRequest()
                .authenticated();
    }
}
