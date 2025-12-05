package com.yumi;

import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@ConfigurationPropertiesScan("com.yumi")
@PropertySource({
    "classpath:application.properties",
    "classpath:chatbot.properties"
})
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableCaching
public class YumiApplication {

  @Value("${chatbot.aplication.credentials.local}")
  private String localCredentialsPath;

  public static void main(String[] args) {
  try {
    Dotenv dotenv = Dotenv.load();
    dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
  } catch (Exception e) {
    System.out.println("⚠️ Sin .env local; usando variables de entorno del sistema.");
  }
    SpringApplication.run(YumiApplication.class, args);
    System.out.println("🚀 Proyecto Final UTP - Aplicación iniciada correctamente");
    System.out.println("📱 Accede a: http://localhost:" + System.getProperty("server.port", "8080"));
  }
  
  @PostConstruct
  public void setupGcpCredentials() {
    try {
      Path localPath = Path.of(localCredentialsPath);
      String deployCredentialsJson = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

      if (Files.exists(localPath)) {
        // Estamos en local
        System.out.println("✅ Usando credenciales locales: " + localPath);
      } else if (deployCredentialsJson != null && !deployCredentialsJson.isBlank()) {
        // Estamos en producción
        Path temp = Files.createTempFile("gcp", ".json");
        Files.writeString(temp, deployCredentialsJson);
        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", temp.toString());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          try {
            Files.deleteIfExists(temp);
          } catch (Exception ignore) {}
        }));
        System.out.println("✅ Usando credenciales temporales en producción.");
      } else {
        System.out.println("⚠️ No se encontraron credenciales GCP válidas.");
      }
    } catch (Exception e) {
      System.out.println("⚠️ Error al configurar credenciales GCP: " + e.getMessage());
    }
  }
}