package RestInn.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

@Configuration
public class EnvironmentConfig {
    private static final String ENV_FILE_PATH = ".env";
    static {
        Dotenv dotenv = Dotenv.load();

        // 1) Base de datos
        String dbHost     = dotenv.get("DB_HOST");
        String dbUser     = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");
        if (dbHost     != null) System.setProperty("DB_HOST",     dbHost);
        if (dbUser     != null) System.setProperty("DB_USERNAME", dbUser);
        if (dbPassword != null) System.setProperty("DB_PASSWORD", dbPassword);

        // 2) JWT
        String jwtSecret       = dotenv.get("JWT_SECRET");
        String jwtExp          = dotenv.get("JWT_EXPIRATION_MS");
        String jwtRefreshExp   = dotenv.get("JWT_REFRESH_EXPIRATION_MS");

        if (jwtSecret == null || jwtSecret.isBlank()) {
            jwtSecret = generateSecureRandomKey(64);
            saveEnvVariable("JWT_SECRET", jwtSecret);
        }
        if (jwtExp == null || jwtExp.isBlank()) {
            jwtExp = "86400000"; // 1 día
            saveEnvVariable("JWT_EXPIRATION_MS", jwtExp);
        }
        if (jwtRefreshExp == null || jwtRefreshExp.isBlank()) {
            jwtRefreshExp = "2592000000"; // 30 días
            saveEnvVariable("JWT_REFRESH_EXPIRATION_MS", jwtRefreshExp);
        }
        System.setProperty("JWT_SECRET",           jwtSecret);
        System.setProperty("JWT_EXPIRATION_MS",    jwtExp);
        System.setProperty("JWT_REFRESH_EXPIRATION_MS", jwtRefreshExp);

        // 3) Gmail
        String gmailUser     = dotenv.get("GMAIL_USER");
        String gmailAppPass  = dotenv.get("GMAIL_APP_PASS");
        if (gmailUser    != null) System.setProperty("GMAIL_USER",    gmailUser);
        if (gmailAppPass != null) System.setProperty("GMAIL_APP_PASS", gmailAppPass);
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
