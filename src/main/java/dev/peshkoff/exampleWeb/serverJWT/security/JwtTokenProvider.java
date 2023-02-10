package dev.peshkoff.exampleWeb.serverJWT.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value( "${jwt.secret_key}")
    private String secretKey;
    @Value( "${jwt.header}")
    private String authorizationHeader;
    @Value( "${jwt.expiration}")
    private long validityInMilliseconds;

    //@PostConstruct
    //protected void init() { secretKey = Base64.getEncoder().encodeToString( secretKey.getBytes()); }

    public String createToken( String username, String[] role) {
        Claims claims = Jwts.claims().setSubject( username);
        claims.put("role", role);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds * 1000);
        return Jwts.builder().setClaims( claims)
                             .setIssuedAt( now)
                             .setExpiration( validity)
                             .signWith( SignatureAlgorithm.HS256, secretKey)
                             .compact();
    }
    public boolean validateToken( String token) {
        try{  Jws<Claims> claimsJws = Jwts.parser().setSigningKey( secretKey).parseClaimsJws( token);
              return !claimsJws.getBody().getExpiration().before( new Date());
        }catch( JwtException | IllegalArgumentException e) {
              //throw new JwtAuthenticationException("JWT token is expired or invalid", HttpStatus.UNAUTHORIZED);
              throw new AuthenticationException( "JWT token is expired or invalid") {};
        }
    }

    public String getUsername( String token) {
        return Jwts.parser().setSigningKey( secretKey).parseClaimsJws( token).getBody().getSubject();
    }

    public String getAuthorizationHeader() { return authorizationHeader; }
}
