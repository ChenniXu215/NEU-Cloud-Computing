package com.csye6225_ChenniXu.healthcheck;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = HealthcheckApplication.class)
class HealthcheckApplicationTests {
	@MockBean
	private AmazonS3 amazonS3;

	@Test
	void contextLoads() {
	}

}
