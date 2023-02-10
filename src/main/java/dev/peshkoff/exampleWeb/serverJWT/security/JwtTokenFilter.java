package dev.peshkoff.exampleWeb.serverJWT.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

//@Component
public class JwtTokenFilter extends GenericFilterBean {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        String token = ((HttpServletRequest) request).getHeader(jwtTokenProvider.getAuthorizationHeader());
        if( token != null && jwtTokenProvider.validateToken( token)) {
            String userName = jwtTokenProvider.getUsername( token);
            UserDetails userDetails = userDetailsService.loadUserByUsername( userName);
            Authentication authentication = new UsernamePasswordAuthenticationToken( userDetails, "", userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication( authentication);
        }

        filterChain.doFilter( request, response);
    }
}