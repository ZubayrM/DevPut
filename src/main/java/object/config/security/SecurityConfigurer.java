package object.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

//    @Autowired
//    private DataSource dataSource;

    @Autowired
    private MyUserDetailService myUserDetailService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth
                .userDetailsService(myUserDetailService);

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
        return NoOpPasswordEncoder.getInstance();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/**", "/api/post/**","/api/auth/login").permitAll()
                .anyRequest()
                .authenticated();
    }
}
