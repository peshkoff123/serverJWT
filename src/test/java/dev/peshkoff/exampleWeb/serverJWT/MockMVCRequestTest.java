package dev.peshkoff.exampleWeb.serverJWT;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

// full Spring application context is started but without the server
// https://www.bezkoder.com/spring-boot-webmvctest/ - example
//@SpringBootTest
//@AutoConfigureMockMvc

// Spring Boot instantiates only the web layer rather than the whole context
@WebMvcTest
//@WebMvcTest( MainController.class)
//@ContextConfiguration(classes = {BirthdayInfoController.class, BasicBirthdayService.class})
public class MockMVCRequestTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() throws  Exception {
        String s =
        mockMvc.perform( MockMvcRequestBuilders.get( "/index.html"))
               .andDo( MockMvcResultHandlers.print())
               .andExpect( MockMvcResultMatchers.status().isOk())
               .andExpect( MockMvcResultMatchers.content().string( Matchers.containsString( "Hi Leshka")))
               .andReturn().getResponse().getContentAsString();
        System.out.println( "s="+s);
    }

}
