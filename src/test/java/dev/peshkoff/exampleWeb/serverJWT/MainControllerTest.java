package dev.peshkoff.exampleWeb.serverJWT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.peshkoff.exampleWeb.serverJWT.model.LoginJWTDTO;
import dev.peshkoff.exampleWeb.serverJWT.model.Role;
import dev.peshkoff.exampleWeb.serverJWT.model.Usver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

// full Spring application context + HTTP Server started
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MainControllerTest {
    @Value( value="${local.server.port}")
    private int port;
    private String localhost;

    @Value( "${jwt.header}")
    private String authorizationHeader;

    private String userName, userToken;
    private Usver[] usverArray;
    private Role[] roleArray;

    @Autowired private TestRestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @BeforeAll public static void beforeAll() {; }

    @BeforeEach public void beforeEach() throws Exception {
        if( userName != null && userToken != null) return;

        restTemplate = new TestRestTemplate();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule( new JavaTimeModule());

        localhost = "http://localhost:"+port;
        System.out.println( "beforeEach(); localhost="+localhost);

        //LoginJWTDTO loginJWTDTO = new LoginJWTDTO("user","user");
        LoginJWTDTO loginJWTDTO = new LoginJWTDTO("admin","admin");
        HttpEntity<LoginJWTDTO> request = new HttpEntity<>( loginJWTDTO);
        ResponseEntity<String> resp = restTemplate.postForEntity(localhost + "/auth/login_jwt", request, String.class);
        assertThat( resp.getStatusCode()).isEqualTo( HttpStatus.OK);
        assertThat( resp.getBody()).isNotNull();
        JsonMapper mapper = new JsonMapper();
        JsonNode root = mapper.readTree( resp.getBody());
        userName = root.get( "login").asText();
        userToken = root.get( "token").asText();
        assertThat( userName).isNotNull().isEqualTo( loginJWTDTO.getName());
        assertThat( userToken).isNotNull();
        System.out.println( "userName = "+userName+"; userToken="+userToken);
    }

    // "/usver" => Usver[]
    @Test void getUsverList() throws Exception {
        HttpEntity<String> request = new HttpEntity<>( "", getAuthorizationHeaders());
        ResponseEntity<String> resp = restTemplate.exchange( localhost+"/usver", HttpMethod.GET, request, String.class);

        System.out.println( "responseEntity = "+resp);
        assertThat( resp.getStatusCode()).isEqualTo( HttpStatus.OK);
        assertThat( resp.getBody()).isNotNull();

        usverArray = objectMapper.readValue( resp.getBody(), Usver[].class);
        assertThat( usverArray).isNotNull().isNotEmpty();
        for( Usver nextUser : usverArray)
            System.out.println("nextUser = " + nextUser);
    }

    // "/usver/roles" => Role[]
    @Test void getRoleList() throws Exception {
        HttpEntity<String> request = new HttpEntity<>( "", getAuthorizationHeaders());
        ResponseEntity<String> resp = restTemplate.exchange( localhost+"/usver/roles", HttpMethod.GET, request, String.class);

        System.out.println( "responseEntity = "+resp);
        assertThat( resp.getStatusCode()).isEqualTo( HttpStatus.OK);
        assertThat( resp.getBody()).isNotNull();

        roleArray = objectMapper.readValue( resp.getBody(), Role[].class);
        assertThat( roleArray).isNotNull().isNotEmpty();
        for( Role nextRole : roleArray)
            System.out.println("nextRole = " + nextRole);
    }

    //"//usver/byName?name=Leshka" => Usver
    @Test void getUsverByName() {
        String usverName = "Leshka";
        HttpEntity<String> request = new HttpEntity<>( "", getAuthorizationHeaders());
        ResponseEntity<Usver> resp = restTemplate.exchange(  localhost+"/usver/byName?name="+usverName, HttpMethod.GET, request, Usver.class);
        System.out.println( "responseEntity = "+resp);
        assertThat( resp.getStatusCode()).isEqualTo( HttpStatus.OK);
        Usver usver = resp.getBody();
        assertThat( usver).isNotNull();
        assertThat( usver.getName()).isEqualTo( usverName);
    }

    // "/usver/{Id}" => Usver
    @Test void getUsverById() {
        String usverId = "63dfc4265992f3273e24219a";
        HttpEntity<String> request = new HttpEntity<>( "", getAuthorizationHeaders());
        ResponseEntity<Usver> resp = restTemplate.exchange(  localhost+"/usver/"+usverId, HttpMethod.GET, request, Usver.class);
        System.out.println( "responseEntity = "+resp);
        assertThat( resp.getStatusCode()).isEqualTo( HttpStatus.OK);
        Usver usver = resp.getBody();
        assertThat( usver).isNotNull();
        assertThat( usver.get_id()).isEqualTo( usverId);
    }

    // '/usver'; POST; body: Usver
    @Test void addUsver() throws Exception {
        getRoleList();
        Usver u = new Usver( "NewUser");
        u.addRole( roleArray[ 0]);
        HttpEntity<Usver> request = new HttpEntity<>( u, getAuthorizationHeaders());
        ResponseEntity<Usver> resp = restTemplate.postForEntity( localhost+"/usver", request, Usver.class);
        System.out.println( "responseEntity = "+resp);
        assertThat( resp.getStatusCode()).isEqualTo( HttpStatus.OK);
        Usver newUsver = resp.getBody();
        assertThat( newUsver).isNotNull();
        assertThat( newUsver.get_id()).isNotNull();
    }

    // '/usver'; PUT; body: { _id: "63e4ba7376fde4020a3c59d4", name: "NewUser" }
    @Test void updateUsver() throws Exception {
        getUsverList();
        getRoleList();
        Usver u = usverArray[ usverArray.length-1];
        u.setName( "UpdateUserName");
        u.setPassword( "UpdateUserName");
        u.addRole( roleArray[ 1]);
        u.addRole( roleArray[ 2]);
        u.addRole( roleArray[ 3]);
        //u.removeRole( roleArray[ 1]);
        //u.removeRole( roleArray[ 2]);
        //u.removeRole( roleArray[ 3]);
        HttpEntity<Usver> request = new HttpEntity<>( u, getAuthorizationHeaders());
        ResponseEntity<Usver> resp = restTemplate.exchange( localhost+"/usver", HttpMethod.PUT, request, Usver.class);
        System.out.println( "responseEntity = "+resp);
        assertThat( resp.getStatusCode()).isEqualTo( HttpStatus.OK);
        Usver newUsver = resp.getBody();
        assertThat( newUsver).isNotNull();
        assertThat( newUsver.get_id()).isNotNull();
        assertThat( newUsver.getRoles().size()).isGreaterThan( 0);
    }

    // '/usver/{Id}'; DELETE;
    @Test void deleteUsver() throws Exception {
        getUsverList();
        Usver u = usverArray[ usverArray.length-1];
        String deleteUsverId = u.get_id();
        HttpEntity<?> request = new HttpEntity<>( null, getAuthorizationHeaders());
        ResponseEntity<Void> delResp = restTemplate.exchange( localhost+"/usver/"+deleteUsverId, HttpMethod.DELETE, request, Void.class);
        System.out.println( "deletResponseEntity = "+delResp);
        assertThat( delResp.getStatusCode()).isEqualTo( HttpStatus.OK);

        ResponseEntity<String> getResp = restTemplate.exchange( localhost+"/usver/"+deleteUsverId, HttpMethod.GET, request, String.class);
        System.out.println( "getResponseEntity = "+getResp);
        assertThat( getResp.getStatusCode()).isEqualTo( HttpStatus.NOT_FOUND);
    }

    @Test void addUsver_getUsverList() throws Exception {
        addUsver();
        getUsverList();
    }

    private HttpHeaders getAuthorizationHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set( authorizationHeader, userToken);
        return headers;
    }
}
