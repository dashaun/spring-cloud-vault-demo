package dev.dashaun.vaultdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

@Component
class VaultSeeder implements ApplicationRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(VaultSeeder.class);
	
	@Autowired
	private VaultTemplate vaultTemplate;
	
	@Override
	public void run(org.springframework.boot.ApplicationArguments args) throws Exception {
		String secretPath = "secret/data/vaultdemo";
		
		try {
			// Check if secrets already exist
			VaultResponse existingSecrets = vaultTemplate.read(secretPath);
			
			if (existingSecrets == null || existingSecrets.getData() == null) {
				logger.info("Seeding initial data into Vault...");
				
				// Create initial seed data
				Map<String, Object> seedData = new HashMap<>();
				seedData.put("database.username", "vault-user");
				seedData.put("database.password", "vault-password-123");
				seedData.put("api.key", "api-key-xyz789");
				seedData.put("jwt.secret", "jwt-secret-abc456");
				seedData.put("app.version", "1.0.0");
				seedData.put("app.environment", "development");
				
				// Wrap data for KV v2 engine
				Map<String, Object> requestData = new HashMap<>();
				requestData.put("data", seedData);
				
				vaultTemplate.write(secretPath, requestData);
				logger.info("Successfully seeded {} secrets into Vault at path: {}", seedData.size(), secretPath);
			} else {
				logger.info("Secrets already exist in Vault at path: {}. Skipping seeding.", secretPath);
			}
		} catch (Exception e) {
			logger.error("Failed to seed vault data: {}", e.getMessage(), e);
		}
	}
}

@RestController
class MyController {

	@Autowired
	private VaultTemplate vaultTemplate;

	@Value("${database.password:not-set}")
	private String dbPassword;

	@Value("${database.username:not-set}")
	private String dbUsername;

	@Value("${api.key:not-set}")
	private String apiKey;

	@Value("${jwt.secret:not-set}")
	private String jwtSecret;

	@Value("${app.version:not-set}")
	private String appVersion;

	@Value("${app.environment:not-set}")
	private String appEnvironment;

	@GetMapping("/")
	String hello(){
		return "Hello World! " + dbUsername + " " + dbPassword;
	}

	@GetMapping("/vault-data")
	Map<String, Object> getVaultData() {
		Map<String, Object> data = new HashMap<>();
		
		try {
			// Read directly from vault to get all seeded data
			String secretPath = "secret/data/vaultdemo";
			VaultResponse response = vaultTemplate.read(secretPath);
			
			if (response != null && response.getData() != null) {
				Object dataObj = response.getData().get("data");
				if (dataObj instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<String, Object> vaultData = (Map<String, Object>) dataObj;
					data.put("vault-data", vaultData);
				}
			}
			
			// Also include values from Spring configuration (showing integration)
			data.put("spring-config-values", Map.of(
				"database.username", dbUsername,
				"database.password", dbPassword,
				"api.key", apiKey,
				"jwt.secret", jwtSecret,
				"app.version", appVersion,
				"app.environment", appEnvironment
			));
			
		} catch (Exception e) {
			data.put("error", "Failed to read vault data: " + e.getMessage());
		}
		
		return data;
	}
}
