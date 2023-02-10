package dev.peshkoff.exampleWeb.serverJWT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import dev.peshkoff.exampleWeb.serverJWT.model.LoginJWTDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import static org.assertj.core.api.Assertions.assertThat;

// full Spring application context + HTTP Server started
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {
    @Value(value="${local.server.port}")
    private int port;
    private String localhost;

    @Autowired private TestRestTemplate restTemplate;

    @BeforeEach void beforeEach() {
        localhost = "http://localhost:"+port+"/";
        System.out.println( "beforeEach(); localhost="+localhost);
    }

    // '/auth/login_jwt'; 'POST'; 'Content-Type': 'application/json'; body: { name: 'user', password : 'user'}
    @Test void apiLoginJWT() throws Exception {
        LoginJWTDTO loginJWTDTO = new LoginJWTDTO("user","user");
        HttpEntity request = new HttpEntity( loginJWTDTO);
        ResponseEntity<String> resp = restTemplate.postForEntity(localhost + "auth/login_jwt", request, String.class);

        System.out.println( "responseEntity = "+resp);
        assertThat( resp.getStatusCode()).isEqualTo( HttpStatus.OK);
        JsonMapper mapper = new JsonMapper();
        JsonNode root = mapper.readTree( resp.getBody());
        JsonNode name = root.get( "login");
        JsonNode token = root.get( "token");

        assertThat( name.asText()).isNotNull().isEqualTo( "user");
        assertThat( token.asText()).isNotNull();
        System.out.println( "name = "+name.asText()+"; token="+token);
    }

    // '/auth/logout'; 'POST';
    @Test void apiLogoutJWT() throws Exception {
        ResponseEntity<String> resp = restTemplate.postForEntity( localhost + "auth/logout", HttpEntity.EMPTY, String.class);
        System.out.println( "responseEntity = "+resp+"; resp.getHeaders().getFirst( \"Location\")="+resp.getHeaders().getFirst( "Location"));
        assertThat( resp.getStatusCode()).isEqualTo( HttpStatus.FOUND);
        assertThat( resp.getHeaders().getFirst( "Location")).contains( "/auth/login");
    }



}
