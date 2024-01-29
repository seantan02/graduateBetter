package com.graduatebetter.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"DATABASE_URL=jdbc:postgresql://localhost:5432/graduate_better", "DATABASE_USER=master", "DATABASE_PWD=7412563j", "PRODUCTION_STATUS=development"})
class WebAppApplicationTests {
	@Test
	void contextLoads() {
	}
}
