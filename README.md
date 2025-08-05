# Spring Boot + HashiCorp Vault Demo

A simple Spring Boot application demonstrating integration with HashiCorp Vault for configuration management and secrets storage.

The app was designed as something simple to validate Vault connectivity.

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose (or similar)
- Java 17+
- Maven 3.9+
- [Vault CLI](https://developer.hashicorp.com/vault/install)

### 1. Start Vault
```bash
docker compose up -d
```

This starts HashiCorp Vault in development mode:
- **URL**: http://localhost:8200
- **Root Token**: `myroot`
- **UI**: Available at the URL above

### 2. Add Secrets to Vault
```bash
# Set environment variables for Vault CLI
export VAULT_ADDR=http://localhost:8200
export VAULT_TOKEN=myroot

# Add demo secrets
vault kv put secret/vaultdemo database.password=secret123 database.username=admin
```

The application starts on http://localhost:8080

### 4. Test the Integration
Visit these endpoints to see Vault in action:

- **http://localhost:8080/** - Show two specific values from Vault in the app
- **http://localhost:8080/actuator/env** - View configuration loaded from Vault via the actuator endpoints
- **http://localhost:8080/actuator/health** - Detailed health information

### Key Features
- ✅ Automatic secret retrieval from Vault
- ✅ Fail-fast behavior if Vault is unavailable
- ✅ Password masking in API responses
- ✅ Health check integration
- ✅ Debug logging for troubleshooting

## 🔧 Configuration

The application connects to Vault using these key properties:

```properties
spring.application.name=vaultdemo
spring.config.import=vault://
spring.cloud.vault.uri=http://localhost:8200
spring.cloud.vault.token=myroot
```

Secrets are stored at path: `secret/vaultdemo`

## 🐳 Docker Setup

The included `docker-compose.yml` provides:
- HashiCorp Vault in development mode
- In-memory storage (data lost on restart)
- No TLS (perfect for local development)
- Root token authentication

## 🔍 Troubleshooting

### Vault Connection Issues
```bash
# Check if Vault is running
docker ps

# View Vault logs
docker logs vault-dev

# Test Vault connectivity
curl http://localhost:8200/v1/sys/health
```

### Missing Secrets
```bash
# List all secrets
vault kv list secret/

# Get specific secret
vault kv get secret/vaultdemo
```

### Application Won't Start
- Ensure Vault is running and accessible
- Verify the root token is `myroot`
- Check that secrets exist at `secret/vaultdemo`
- Enable debug logging: `logging.level.org.springframework.cloud.vault=DEBUG`

## 📚 Learn More

- [HashiCorp Vault Documentation](https://www.vaultproject.io/docs)
- [Spring Cloud Vault Reference](https://docs.spring.io/spring-cloud-vault/docs/current/reference/html/)
- [Vault KV Secrets Engine](https://www.vaultproject.io/docs/secrets/kv)

## ⚠️ Production Notes

This demo uses Vault in development mode with:
- Root token authentication
- In-memory storage
- No TLS encryption

For production use, configure:
- Proper authentication methods (AppRole, Kubernetes, etc.)
- Persistent storage backend
- TLS encryption
- Sealed/unsealed operations
- Proper secret rotation policies