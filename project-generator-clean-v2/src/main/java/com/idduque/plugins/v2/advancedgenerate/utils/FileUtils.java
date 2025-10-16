package com.idduque.plugins.v2.advancedgenerate.utils;

import org.apache.maven.plugin.logging.Log;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    private final Log log;

    public FileUtils(Log log) {
        this.log = log;
    }

    public static void writeFile(Path filePath, String content, Log log) throws IOException {
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);
        log.info("📄 Archivo creado: " + filePath);
    }

    public void crearMainApplication(Path baseDir, String packageBase) throws IOException {
        Path mainFile = baseDir.resolve("applications/app-service/src/main/java/"
                + packageBase.replace('.', '/') + "/MainApplication.java");

        StringBuilder contenido = new StringBuilder();
        contenido.append("package ").append(packageBase).append(";\n\n")
                .append("import org.springframework.boot.SpringApplication;\n")
                .append("import org.springframework.boot.autoconfigure.SpringBootApplication;\n\n")
                .append("@SpringBootApplication\n")
                .append("public class MainApplication {\n")
                .append("    public static void main(String[] args) {\n")
                .append("        SpringApplication.run(MainApplication.class, args);\n")
                .append("    }\n")
                .append("}\n");

        writeFile(mainFile, contenido.toString(), log);
    }

    public void crearReadme(Path baseDir, String nombreProyecto, String javaVersion) throws IOException {
        Path readme = baseDir.resolve("README.md");

        StringBuilder contenido = new StringBuilder();
        contenido.append("# ").append(nombreProyecto).append("\n\n")
                .append("Proyecto generado automáticamente con **Project Generator Clean**\n\n")
                .append("📦 Autor: **Iván David Duque Perdomo**\n")
                .append("🧱 Arquitectura: Clean Architecture (multi-módulo Maven)\n")
                .append("⚙️ Java: ").append(javaVersion).append("\n");

        writeFile(readme, contenido.toString(), log);
    }
}
