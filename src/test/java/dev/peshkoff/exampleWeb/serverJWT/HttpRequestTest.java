package dev.peshkoff.exampleWeb.serverJWT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import dev.peshkoff.exampleWeb.serverJWT.model.LoginJWTDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import static org.assertj.core.api.Assertions.assertThat;

// full Spring application context + HTTP Server is started

// mvn test -Dtest=BasicBirthdayServiceTest

// org.springframework.web.reactive.client.WebClient supports sync, async, and streaming scenarios.
// https://www.baeldung.com/spring-5-webclient
// Some changes for newBranch


@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {
    @Value(value="${local.server.port}")
    private int port;

    private String localhost;

    private TestRestTemplate restTemplate;

    @BeforeEach
    public void beforeEach() {
        restTemplate = new TestRestTemplate();
        localhost = "http://localhost:"+port+"/";
        System.out.println( "beforeEach(); localhost="+localhost);
    }

    @Test void index_html() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity( localhost/* + "index.html"*/, String.class);
        System.out.println( "responseEntity = "+responseEntity);
        assertThat( responseEntity.getStatusCode()).isEqualTo( HttpStatus.OK);
        assertThat( responseEntity.getBody()).contains( "Hi Leshka");
    }
    @Test void apiRedirectUnauthorized() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity( localhost + "usver/1", String.class);
        System.out.println( "responseEntity = "+responseEntity);
        assertThat( responseEntity.getStatusCode()).isEqualTo( HttpStatus.OK);
        assertThat( responseEntity.getBody()).contains( "LoginPage");

        /*HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add( "username", "usver");
        body.add( "password", "usver");
        HttpEntity request = new HttpEntity( body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(localhost + "/auth/login", request, String.class);
        //getForEntity( localhost + "/auth/login", request, String.class);
        System.out.println( "responseEntity = "+responseEntity);*/
    }

}
