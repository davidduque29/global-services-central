package com.idduque.plugins.v2.advancedgenerate;


import com.idduque.plugins.v2.advancedgenerate.utils.FileUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PomGenerator {
    private final Log log;
    private final String packageBase;
    private final String nombreProyecto;
    private final String javaVersion;
    private final String adapterName;
    private final String entryName;
    private final String springBootVersion;

    public PomGenerator(Log log, String packageBase, String nombreProyecto, String javaVersion, String adapterName
            , String entryName, String springBootVersion) {
        this.log = log;
        this.packageBase = packageBase;
        this.nombreProyecto = nombreProyecto;
        this.javaVersion = javaVersion;
        this.adapterName = adapterName;
        this.entryName = entryName;
        this.springBootVersion = springBootVersion;
    }

    public void crearPomRaiz(Path baseDir) throws IOException {
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
                .append("    <module>infrastructure/driven-adapters/")
                .append(adapterName).append("</module>\n")
                .append("    <module>infrastructure/entry-points/")
                .append(entryName).append("</module>\n")
                .append("  </modules>\n")

                .append("  <properties>\n")
                .append("    <java.version>").append(javaVersion).append("</java.version>\n")
                .append("    <spring-boot.version>").append(springBootVersion).append("</spring-boot.version>\n")
                .append("  </properties>\n")
                .append("  <build>\n")
                .append("    <pluginManagement>\n")
                .append("      <plugins>\n")
                .append("        <plugin>\n")
                .append("          <groupId>org.springframework.boot</groupId>\n")
                .append("          <artifactId>spring-boot-maven-plugin</artifactId>\n")
                .append("          <version>${spring-boot.version}</version>\n")
                .append("        </plugin>\n")
                .append("      </plugins>\n")
                .append("    </pluginManagement>\n")
                .append("  </build>\n")
                .append("</project>\n");

        FileUtils.writeFile(baseDir.resolve("pom.xml"), pom.toString(), log);
    }

    public void crearPomsHijos(Path baseDir) throws IOException {
        generarPomHijo(baseDir.resolve("applications/app-service"), "app-service", true);
        generarPomHijo(baseDir.resolve("domain/model"), "model", false);
        generarPomHijo(baseDir.resolve("domain/usecase"), "usecase", false);
        generarPomHijo(baseDir.resolve("infrastructure/driven-adapters/" + adapterName), adapterName, false);
        generarPomHijo(baseDir.resolve("infrastructure/entry-points/" + entryName), entryName, false);
    }

    private void generarPomHijo(Path moduleDir, String artifactId, boolean esAppService) throws IOException {
        StringBuilder contenido = new StringBuilder();

        contenido.append("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" ")
                .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
                .append("xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 ")
                .append("http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n")
                .append("  <modelVersion>4.0.0</modelVersion>\n")
                .append("  <parent>\n")
                .append("    <groupId>").append(packageBase).append("</groupId>\n")
                .append("    <artifactId>").append(nombreProyecto).append("</artifactId>\n")
                .append("    <version>1.0.0</version>\n")
                .append("    <relativePath>").append(calcularRutaRelativa(moduleDir)).append("</relativePath>\n")
                .append("  </parent>\n\n")
                .append("  <artifactId>").append(artifactId).append("</artifactId>\n")
                .append("  <packaging>jar</packaging>\n\n");

        if (esAppService) {
            contenido.append("  <dependencies>\n")
                    .append("    <dependency>\n")
                    .append("      <groupId>org.springframework.boot</groupId>\n")
                    .append("      <artifactId>spring-boot-starter</artifactId>\n")
                    .append("      <version>").append(springBootVersion).append("</version>\n")
                    .append("    </dependency>\n")
                    .append("    <dependency>\n")
                    .append("      <groupId>org.springframework.boot</groupId>\n")
                    .append("      <artifactId>spring-boot-starter-test</artifactId>\n")
                    .append("      <version>").append(springBootVersion).append("</version>\n")
                    .append("      <scope>test</scope>\n")
                    .append("    </dependency>\n")
                    .append("  </dependencies>\n\n")
                    .append("  <build>\n")
                    .append("    <plugins>\n")
                    .append("      <plugin>\n")
                    .append("        <groupId>org.springframework.boot</groupId>\n")
                    .append("        <artifactId>spring-boot-maven-plugin</artifactId>\n")
                    .append("        <version>").append(springBootVersion).append("</version>\n")
                    .append("      </plugin>\n")
                    .append("    </plugins>\n")
                    .append("  </build>\n");
        } else {
            contenido.append("  <dependencies>\n")
                    .append("    <!-- Dependencias específicas del módulo -->\n")
                    .append("  </dependencies>\n");
        }

        contenido.append("</project>\n");

        FileUtils.writeFile(moduleDir.resolve("pom.xml"), contenido.toString(), log);
        log.info("✅ POM hijo generado correctamente para módulo: " + artifactId);
    }

    private String calcularRutaRelativa(Path moduleDir) {
        // Detecta la carpeta del proyecto raíz
        Path projectRoot = Paths.get(System.getProperty("user.dir")).toAbsolutePath();

        // Calcula cuántos niveles hay entre el módulo y el pom.xml raíz
        Path relative = projectRoot.relativize(moduleDir.toAbsolutePath());
        int depth = relative.getNameCount();

        // Si el módulo está dentro del proyecto raíz, sube esos niveles
        if (depth > 0) {
            return "../".repeat(depth) + "pom.xml";
        }

        // Valor por defecto
        return "../pom.xml";
    }


}