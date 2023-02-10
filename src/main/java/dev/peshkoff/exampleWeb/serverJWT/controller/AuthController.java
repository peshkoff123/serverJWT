package dev.peshkoff.exampleWeb.serverJWT.controller;

import dev.peshkoff.exampleWeb.serverJWT.model.LoginJWTDTO;
import dev.peshkoff.exampleWeb.serverJWT.model.Usver;
import dev.peshkoff.exampleWeb.serverJWT.repository.UsverRepository;
import dev.peshkoff.exampleWeb.serverJWT.security.JwtTokenProvider;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping( "/auth")
public class AuthController {
    @Autowired JwtTokenProvider jwtTokenProvider;
    @Autowired UsverRepository usverRepository;

@GetMapping( "/login")
public String loginPage() {
    return "login";
}

@GetMapping( "/success")
public String successPage() {
    return "success";
}

/* //login_jwt { "name": "user", "password" : "user"}
fetch('/auth/login_jwt', { method: 'POST',
                          headers: { 'Content-Type': 'application/json' },
                             body: JSON.stringify( { name: 'user', password : 'user'})
                         }).then( result => result.json().then( console.log))

fetch('/usver/1', { method: 'GET', headers: { 'Authorization': 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZSI6W…g3N30.zSNGPINTICBz7W4PR8_vXGeHF0G3OjSUwm-ros-oZlU' }
                  }).then( result => result.json().then( console.log))
*/

@PostMapping( "/login_jwt")
public ResponseEntity<?> loginJWT(@RequestBody(required=false) LoginJWTDTO loginJWTDTO) {
    System.out.println( "loginJWT :: loginJWTDTO = "+loginJWTDTO);
    if( loginJWTDTO.getName() == null || loginJWTDTO.getPassword() == null)
        return new ResponseEntity<>("Empty userName or password ", HttpStatus.NOT_FOUND);

    Usver u = usverRepository.findByName( loginJWTDTO.getName());
    if( u == null)
        return new ResponseEntity<>("Invalid userName ", HttpStatus.BAD_REQUEST);
    if( ! u.getPassword().equals( loginJWTDTO.getPassword()))
        return new ResponseEntity<>("Invalid login/password combination", HttpStatus.FORBIDDEN);

    String accessToken = jwtTokenProvider.createToken( u.getName(), u.rolesArr());
    Map<Object, Object> response = new HashMap<>();
    response.put("login", u.getName());
    response.put("token", accessToken);
    return ResponseEntity.ok( response);
}

@PostMapping( "/logout")
public void logout( HttpServletRequest request, HttpServletResponse response) {
    HttpSession session = request.getSession(false);
    if( session != null) session.invalidate();
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication((Authentication)null);
    SecurityContextHolder.clearContext();
}
/*
    @ExceptionHandler( RuntimeException.class)
    public final ResponseEntity<Exception> handleAllExceptions( RuntimeException ex) {
        return new ResponseEntity<Exception>( ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
*/
}
