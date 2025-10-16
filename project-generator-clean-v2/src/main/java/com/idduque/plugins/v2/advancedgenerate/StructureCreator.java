package com.idduque.plugins.v2.advancedgenerate;

import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StructureCreator {
    private final Log log;
    private final String packageBase;
    private final String adapterName;
    private final String entryName;

    public StructureCreator(Log log, String packageBase, String adapterName, String entryName) {
        this.log = log;
        this.packageBase = packageBase;
        this.adapterName = adapterName;
        this.entryName = entryName;
    }

    public void crearEstructura(Path baseDir) throws IOException {
        String pkg = packageBase.replace('.', '/');

        String[] paths = {
                // 🏗️ Application Layer
                "applications/app-service/src/main/java/" + pkg,
                "applications/app-service/src/main/resources",
                "applications/app-service/src/test/java/" + pkg,

                // 🧩 Domain Layer
                "domain/model/src/main/java/" + pkg,
                "domain/usecase/src/main/java/" + pkg,

                // 🔌 Infrastructure Layer
                "infrastructure/driven-adapters/" + adapterName + "/src/main/java/" + pkg,
                "infrastructure/entry-points/" + entryName + "/src/main/java/" + pkg,

                // 🚀 Deployment Layer
                "deployment"
        };

        // Crear todas las carpetas necesarias
        for (String p : paths) {
            Path dir = baseDir.resolve(p);
            Files.createDirectories(dir);
            log.debug("📂 Creada carpeta: " + dir);
        }

        crearDockerfile(baseDir);

        log.info("✅ Estructura base creada con módulos dinámicos: "
                + adapterName + " y " + entryName);
    }

    private void crearDockerfile(Path baseDir) throws IOException {
        Path dockerfile = baseDir.resolve("deployment/Dockerfile");

        if (Files.notExists(dockerfile)) {
            StringBuilder dockerContent = new StringBuilder();
            dockerContent.append("FROM eclipse-temurin:22-jdk\n")
                    .append("WORKDIR /app\n")
                    .append("COPY target/*.jar app.jar\n")
                    .append("EXPOSE 8080\n")
                    .append("ENTRYPOINT [\"java\",\"-jar\",\"/app/app.jar\"]\n");

            Files.writeString(dockerfile, dockerContent.toString());
            log.info("🐳 Dockerfile generado en: " + dockerfile.toAbsolutePath());
        } else {
            log.info("📦 Dockerfile ya existente, no se sobrescribió.");
        }
    }
}