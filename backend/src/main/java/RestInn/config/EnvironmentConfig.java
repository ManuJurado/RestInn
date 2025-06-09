package RestInn.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

public class EnvironmentConfig {
    private static final String ENV_FILE_PATH = ".env";

    static {
        Dotenv dotenv = Dotenv.load();

        // JWT secret (64 bytes => 86 chars Base64 URL-safe)
        String jwtSecret = dotenv.get("JWT_SECRET");
        if (jwtSecret == null || jwtSecret.isBlank()) {
            jwtSecret = generateSecureRandomKey(64);
            saveEnvVariable("JWT_SECRET", jwtSecret);
        }

        // JWT access token expiration (ms)
        String jwtExpirationMs = dotenv.get("JWT_EXPIRATION_MS");
        if (jwtExpirationMs == null || jwtExpirationMs.isBlank()) {
            jwtExpirationMs = "900000"; // 15 minutes
            saveEnvVariable("JWT_EXPIRATION_MS", jwtExpirationMs);
        }

        // JWT refresh token expiration (ms)
        String jwtRefreshMs = dotenv.get("JWT_REFRESH_EXPIRATION_MS");
        if (jwtRefreshMs == null || jwtRefreshMs.isBlank()) {
            jwtRefreshMs = "2592000000"; // 30 days
            saveEnvVariable("JWT_REFRESH_EXPIRATION_MS", jwtRefreshMs);
        }

        // Set properties for Spring Boot to pick up
        System.setProperty("JWT_SECRET", jwtSecret);
        System.setProperty("JWT_EXPIRATION_MS", jwtExpirationMs);
        System.setProperty("JWT_REFRESH_EXPIRATION_MS", jwtRefreshMs);
        // Spring maps env vars/system props JWT_SECRET -> jwt.secret
        // and JWT_EXPIRATION_MS -> jwt.expiration-ms, JWT_REFRESH_EXPIRATION_MS -> jwt.refresh-expiration-ms

        // Database properties
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
    }

    /**
     * Genera y guarda un nuevo JWT_SECRET en .env y en propiedades del sistema.
     */
    public static void regenerateJwtSecret() {
        String newSecret = generateSecureRandomKey(64);
        saveEnvVariable("JWT_SECRET", newSecret);
        System.out.println("Nuevo JWT_SECRET generado: " + newSecret);
        System.setProperty("JWT_SECRET", newSecret);
    }

    private static String generateSecureRandomKey(int byteLength) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[byteLength];
        random.nextBytes(key);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(key);
    }

    private static void saveEnvVariable(String key, String value) {
        try {
            File envFile = new File(ENV_FILE_PATH);
            Properties props = new Properties();
            if (envFile.exists()) {
                try (FileReader reader = new FileReader(envFile)) {
                    props.load(reader);
                }
            }
            props.setProperty(key, value);
            try (FileWriter writer = new FileWriter(envFile)) {
                props.store(writer, "Updated by EnvironmentConfig");
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar " + key + " en el archivo .env", e);
        }
    }
}
