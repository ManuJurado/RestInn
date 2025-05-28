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

        String jwtSecret = dotenv.get("JWT_SECRET");
        String jwtExpiration = dotenv.get("JWT_EXPIRATION");

        // Otras variables
        System.out.println(jwtSecret);
        System.setProperty("JWT_SECRET", jwtSecret);
        System.setProperty("JWT_EXPIRATION", jwtExpiration);
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
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
