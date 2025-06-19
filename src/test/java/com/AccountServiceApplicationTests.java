package com;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.easynet.util.ConfigurationValues;
import com.easynet.util.common;

@SpringBootTest(classes=AccountServiceApplicationTests.class)
class AccountServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	static{		
		try {
			ConfigurationValues.PROJECT_NAME="Enfinity-AccountService";
			//this method used to set the project configuration detail
			common.ofSetProjectConfigDtl();
			//the below line only set for this class and for execute test only.
			//do not set it from classes.
			//this property will be applicable for application deployed on tomcat.
			System.setProperty("spring.config.location",ConfigurationValues.SPRING_CONFIG_LOCATION);
		} catch (Exception e) {			
			e.printStackTrace();
		}		
	}
}
