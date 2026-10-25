package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "socketio.port=0")
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
