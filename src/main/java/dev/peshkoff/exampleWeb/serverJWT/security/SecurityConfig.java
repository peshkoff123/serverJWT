package dev.peshkoff.exampleWeb.serverJWT.security;


import dev.peshkoff.exampleWeb.serverJWT.model.Role;
import dev.peshkoff.exampleWeb.serverJWT.model.Usver;
import dev.peshkoff.exampleWeb.serverJWT.repository.UsverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity( prePostEnabled = true)
public class SecurityConfig  {
    @Autowired private JwtTokenProvider jwtTokenProvider;

    @Autowired UsverRepository usverRepository;

/*    private UserDetailsService userDetailsService;
@Bean
public UserDetailsService userDetailsService() {
    if( userDetailsService == null)
        userDetailsService =  new UserDetailsService() {
        @Override
        public UserDetails loadUserByUsername( String name) throws UsernameNotFoundException {
            Usver usver = Usver.getUsverByName( name);
            if( usver == null) return null;
            UserDetails u = User.withUsername( usver.getName())
                                //.password( passwordEncoder().encode( usver.getPassword()))
                                .password( usver.getPassword())
                                .roles( usver.getRolesArr())
                                .build();
            return u;
        }
    };
    return userDetailsService;
}*/

    private class JwtTokenFilter extends GenericFilterBean {
        @Override
        public void doFilter(ServletRequest request, ServletResponse response,
                             FilterChain filterChain) throws IOException, ServletException {
            String token = ((HttpServletRequest) request).getHeader(jwtTokenProvider.getAuthorizationHeader());
            if( token != null && jwtTokenProvider.validateToken( token)) {
                String userName = jwtTokenProvider.getUsername( token);
                Usver usver = usverRepository.findByName( userName);

                UserDetails userDetails = User.withUsername( userName)
                                              .password( usver.getPassword())
                                              .roles( usver.rolesArr()).build();
                Authentication authentication = new UsernamePasswordAuthenticationToken( userDetails, "", userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication( authentication);
            }

            filterChain.doFilter( request, response);
        }
    }

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

@Bean
public SecurityFilterChain filterChain( HttpSecurity http) throws Exception {
        http.addFilterAfter( new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/index.html").permitAll()
            .antMatchers( "/auth/login_jwt").permitAll()
                //.antMatchers( HttpMethod.GET,"/usver/byName**").hasRole( Role.ADMIN)
                //.antMatchers( HttpMethod.GET,"/usver/**").hasAnyRole( Role.USER, Role.ADMIN, Role.SUPER_USER)
                .antMatchers( HttpMethod.POST, "/usver/**").hasRole( Role.ADMIN)
                .antMatchers( HttpMethod.PUT, "/usver/**").hasRole( Role.ADMIN)
                .antMatchers( HttpMethod.DELETE, "/usver/**").hasRole( Role.ADMIN)
            .anyRequest().authenticated();
            /*.and()
            .formLogin()
              .loginPage("/auth/login").permitAll()
              .defaultSuccessUrl("/auth/success")
            .and()
            .logout()
              .logoutRequestMatcher( new AntPathRequestMatcher("/auth/logout", "POST"))
              .invalidateHttpSession( true)
              .clearAuthentication( true)
              .deleteCookies( "JSESSIONID")
              .logoutSuccessUrl( "/auth/login");*/
    return http.build();
}

}

