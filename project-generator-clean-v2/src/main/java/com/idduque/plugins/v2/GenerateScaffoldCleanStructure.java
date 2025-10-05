package com.idduque.plugins.v2;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mojo(name = "scaffold", requiresProject = false)
public class GenerateScaffoldCleanStructure extends AbstractMojo {

    @Parameter(property = "nombreProyecto", defaultValue = "clean-app")
    private String nombreProyecto;

    @Parameter(property = "packageBase", defaultValue = "com.idduque.clean")
    private String packageBase;

    @Parameter(property = "javaVersion", defaultValue = "21")
    private String javaVersion;

    @Parameter(property = "adapterName", defaultValue = "generic-adapter")
    private String adapterName;

    @Parameter(property = "entryName", defaultValue = "generic-entry")
    private String entryName;


    @Override
    public void execute() throws MojoExecutionException {
        getLog().info(" Iniciando generación del scaffold para: " + nombreProyecto);
        Path baseDir = Paths.get(nombreProyecto);

        try {
            crearEstructuraBase(baseDir);
            crearClaseMain(baseDir);
            crearPomRaiz(baseDir);
            crearPomsHijos(baseDir);
            crearReadme(baseDir);

            getLog().info(" Proyecto generado exitosamente en: " + baseDir.toAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException(" Error al generar el scaffold", e);
        }
    }

    private void crearEstructuraBase(Path baseDir) throws IOException {
        String packagePath = packageBase.replace('.', '/');

        String[] carpetas = {
                // Application layer
                "applications/app-service/src/main/java/" + packagePath,
                "applications/app-service/src/main/resources",
                "applications/app-service/src/test/java/" + packagePath,

                // Domain layer
                "domain/model/src/main/java/" + packagePath,
                "domain/model/src/test/java/" + packagePath,
                "domain/usecase/src/main/java/" + packagePath,
                "domain/usecase/src/test/java/" + packagePath,

                // Infrastructure layer - Driven Adapter y Entry Point dinámicos
                "infrastructure/driven-adapters/" + adapterName + "/src/main/java/" + packagePath,
                "infrastructure/driven-adapters/" + adapterName + "/src/main/resources",
                "infrastructure/driven-adapters/" + adapterName + "/src/test/java/" + packagePath,

                "infrastructure/entry-points/" + entryName + "/src/main/java/" + packagePath,
                "infrastructure/entry-points/" + entryName + "/src/main/resources",
                "infrastructure/entry-points/" + entryName + "/src/test/java/" + packagePath
        };

        for (String carpeta : carpetas) {
            Files.createDirectories(baseDir.resolve(carpeta));
        }

        getLog().info("📁 Estructura de carpetas creada correctamente con módulos dinámicos: "
                + adapterName + " y " + entryName);
    }

    private void crearClaseMain(Path baseDir) throws IOException {
        Path archivoMain = baseDir.resolve("applications/app-service/src/main/java/"
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

        writeFile(archivoMain, contenido.toString());
    }

    private void crearPomRaiz(Path baseDir) throws IOException {
        StringBuilder pom = new StringBuilder();
        pom.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" ")
                .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
                .append("xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 ")
                .append("http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n")
                .append("  <modelVersion>4.0.0</modelVersion>\n")
                .append("  <groupId>").append(packageBase).append("</groupId>\n")
                .append("  <artifactId>").append(nombreProyecto).append("</artifactId>\n")
                .append("  <version>1.0.0</version>\n")
                .append("  <packaging>pom</packaging>\n")
                .append("  <modules>\n")
                .append("    <module>applications/app-service</module>\n")
                .append("    <module>domain/model</module>\n")
                .append("    <module>domain/usecase</module>\n")
                .append("    <module>infrastructure/driven-adapters</module>\n")
                .append("    <module>infrastructure/entry-points</module>\n")
                .append("  </modules>\n")
                .append("  <properties>\n")
                .append("    <java.version>").append(javaVersion).append("</java.version>\n")
                .append("    <maven.compiler.source>").append(javaVersion).append("</maven.compiler.source>\n")
                .append("    <maven.compiler.target>").append(javaVersion).append("</maven.compiler.target>\n")
                .append("  </properties>\n")
                .append("  <build>\n")
                .append("    <pluginManagement>\n")
                .append("      <plugins>\n")
                .append("        <plugin>\n")
                .append("          <groupId>org.springframework.boot</groupId>\n")
                .append("          <artifactId>spring-boot-maven-plugin</artifactId>\n")
                .append("        </plugin>\n")
                .append("      </plugins>\n")
                .append("    </pluginManagement>\n")
                .append("  </build>\n")
                .append("</project>\n");

        writeFile(baseDir.resolve("pom.xml"), pom.toString());
    }

    private void crearPomsHijos(Path baseDir) throws IOException {
        crearPomHijo(baseDir.resolve("applications/app-service"), "app-service");
        crearPomHijo(baseDir.resolve("domain/model"), "model");
        crearPomHijo(baseDir.resolve("domain/usecase"), "usecase");
        crearPomHijo(baseDir.resolve("infrastructure/driven-adapters/" + adapterName), adapterName);
        crearPomHijo(baseDir.resolve("infrastructure/entry-points/" + entryName), entryName);
    }

    private void crearPomHijo(Path moduleDir, String artifactId) throws IOException {
        StringBuilder contenido = new StringBuilder();

        getLog().info("🧩 Generando POM hijo para módulo: " + artifactId);

        contenido.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" ")
                .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
                .append("xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 ")
                .append("http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n")
                .append("  <modelVersion>4.0.0</modelVersion>\n")
                .append("  <parent>\n")
                .append("    <groupId>").append(packageBase).append("</groupId>\n")
                .append("    <artifactId>").append(nombreProyecto).append("</artifactId>\n")
                .append("    <version>1.0.0</version>\n")
                // 🔧 Corrección: la ruta relativa debe ser diferente según la profundidad
                .append("    <relativePath>")
                .append(calcularRutaRelativa(moduleDir))
                .append("</relativePath>\n")
                .append("  </parent>\n\n")
                .append("  <artifactId>").append(artifactId).append("</artifactId>\n")
                .append("  <packaging>jar</packaging>\n\n");

        // Si el módulo es app-service, agregamos dependencias y plugin de Spring Boot
        if ("app-service".equalsIgnoreCase(artifactId)) {
            contenido.append("  <dependencies>\n")
                    .append("    <dependency>\n")
                    .append("      <groupId>org.springframework.boot</groupId>\n")
                    .append("      <artifactId>spring-boot-starter</artifactId>\n")
                    .append("      <version>3.3.2</version>\n")
                    .append("    </dependency>\n")
                    .append("    <dependency>\n")
                    .append("      <groupId>org.springframework.boot</groupId>\n")
                    .append("      <artifactId>spring-boot-starter-test</artifactId>\n")
                    .append("      <version>3.3.2</version>\n")
                    .append("      <scope>test</scope>\n")
                    .append("    </dependency>\n")
                    .append("  </dependencies>\n\n")
                    .append("  <build>\n")
                    .append("    <plugins>\n")
                    .append("      <plugin>\n")
                    .append("        <groupId>org.springframework.boot</groupId>\n")
                    .append("        <artifactId>spring-boot-maven-plugin</artifactId>\n")
                    .append("      </plugin>\n")
                    .append("    </plugins>\n")
                    .append("  </build>\n");
        } else {
            // Módulos restantes sin dependencias de Spring Boot
            contenido.append("  <dependencies>\n")
                    .append("    <!-- Dependencias específicas del módulo -->\n")
                    .append("  </dependencies>\n");
        }

        contenido.append("</project>\n");

        writeFile(moduleDir.resolve("pom.xml"), contenido.toString());
        getLog().info("✅ POM hijo generado correctamente para módulo: " + artifactId);
    }

    private String calcularRutaRelativa(Path moduleDir) {
        // Cuenta los niveles desde el módulo hasta el raíz
        int depth = moduleDir.getNameCount();
        if (depth > 2) {
            return "../".repeat(depth - 2) + "pom.xml";
        }
        return "../pom.xml";
    }



    private void crearReadme(Path baseDir) throws IOException {
        StringBuilder readme = new StringBuilder();
        readme.append("# Proyecto generado con Project Generator Clean\n\n")
                .append("Generado automáticamente por el plugin **Project Generator Clean**\n\n")
                .append("📦 Autor: **Iván David Duque Perdomo**\n")
                .append("🧱 Arquitectura: Clean Architecture (multi-módulo Maven)\n")
                .append("⚙️ Java: ").append(javaVersion).append("\n");

        writeFile(baseDir.resolve("README.md"), readme.toString());
    }

    private void writeFile(Path filePath, String content) throws IOException {
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content);
        getLog().info(" Archivo creado: " + filePath);
    }
}
