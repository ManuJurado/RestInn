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

        String jwtSecret = dotenv.get("JWT_SECRET"); //TODO No deveria generar una clave nueva.
        if (jwtSecret == null || jwtSecret.isBlank()) {
            jwtSecret = generateSecureRandomKey(64); // 64 bytes = 86 chars en base64
            saveEnvVariable("JWT_SECRET", jwtSecret);
        }

        String jwtExpiration = dotenv.get("JWT_EXPIRATION");
        if (jwtExpiration == null || jwtExpiration.isBlank()) {
            jwtExpiration = "86400000";
            saveEnvVariable("JWT_EXPIRATION", jwtExpiration);
        }

        // Otras variables
        System.out.println(jwtSecret);
        System.setProperty("JWT_SECRET", jwtSecret);
        System.setProperty("JWT_EXPIRATION", jwtExpiration);
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
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
