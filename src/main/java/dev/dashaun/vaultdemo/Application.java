package dev.dashaun.vaultdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

@RestController
class MyController {

	@Value("${database.password}")
	private String dbPassword;

	@Value("${database.username}")
	private String dbUsername;

	@GetMapping("/")
	String hello(){
		return "Hello World! " + dbUsername + " " + dbPassword;
	}
}
