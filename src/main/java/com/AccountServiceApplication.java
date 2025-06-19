package com;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.easynet.util.ConfigurationValues;
import com.easynet.util.common;

@EnableAspectJAutoProxy
@SpringBootApplication
public class AccountServiceApplication extends SpringBootServletInitializer{
	
	public static void main(String[] args) throws Exception {				
		String ls_configLocation;

		ConfigurationValues.PROJECT_NAME="Enfinity-AccountService";
		//this method used to set the project configuration detail
		common.ofSetProjectConfigDtl();		
		ls_configLocation="--spring.config.location="+ConfigurationValues.SPRING_CONFIG_LOCATION;
					
		SpringApplication.run(AccountServiceApplication.class,ls_configLocation);		
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		//this method used to set the project configuration detail
		try {
			ConfigurationValues.PROJECT_NAME="Enfinity-AccountService";
			common.ofSetProjectConfigDtl();
		} catch (Exception e) {			
			e.printStackTrace();
		}		
		
		servletContext.setInitParameter("spring.config.location",ConfigurationValues.SPRING_CONFIG_LOCATION);
		super.onStartup(servletContext);		
	}
}