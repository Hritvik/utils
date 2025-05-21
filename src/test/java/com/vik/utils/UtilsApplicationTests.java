package com.vik.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {
    "spring.main.lazy-initialization=true",
    "spring.cloud.config.enabled=false",
    "spring.cloud.discovery.enabled=false",
    "spring.cloud.bus.enabled=false"
})
@TestPropertySource(properties = {
    "spring.main.lazy-initialization=true"
})
class UtilsApplicationTests {

	@Test
	void contextLoads() {
	}

}
