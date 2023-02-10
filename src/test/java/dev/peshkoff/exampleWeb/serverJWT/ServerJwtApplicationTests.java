package dev.peshkoff.exampleWeb.serverJWT;

import dev.peshkoff.exampleWeb.serverJWT.controller.MainController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ServerJwtApplicationTests {
    @Autowired
	private MainController mainController;

	@Test
	void contextLoads() {
		assertThat( mainController).isNull();
	}

}
