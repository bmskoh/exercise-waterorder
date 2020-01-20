package code.challenge.rubicon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * This class sets up Spring security. For this exercise, we create only 1 user
 * with FARMER role. Username and password are defined in
 * application.properties.
 */
@EnableWebSecurity
public class BasicAuthenticationConfig extends WebSecurityConfigurerAdapter {

    private final String farmerRole = "FARMER";

    @Value("${tempcredential.username:farmer}")
    private String userName;
    @Value("${tempcredential.password:password}")
    private String password;

    // Create 1 user just for exercise
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(this.userName).password(this.encoder().encode(this.password))
                .roles(this.farmerRole);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and().authorizeRequests().antMatchers(HttpMethod.POST, "/waterorders").hasRole(this.farmerRole)
                .antMatchers(HttpMethod.PUT, "/waterorders/**").hasRole(this.farmerRole)
                .antMatchers(HttpMethod.GET, "/waterorders/**").hasRole(this.farmerRole).and().csrf().disable()
                .formLogin().disable();
    }
}
