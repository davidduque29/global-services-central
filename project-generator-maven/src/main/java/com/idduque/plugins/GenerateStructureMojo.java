package com.idduque.plugins; // 👉 Paquete donde vive la clase del plugin (debe coincidir con tu estructura de carpetas)

import org.apache.maven.plugin.AbstractMojo; // 👉 Clase base para crear plugins Maven (te da acceso a getLog(), manejo de errores, etc.)
import org.apache.maven.plugin.MojoExecutionException; // 👉 Excepción específica para errores de ejecución del plugin
import org.apache.maven.plugins.annotations.Mojo; // 👉 Anotación que define el "goal" (comando) del plugin
import org.apache.maven.plugins.annotations.Parameter; // 👉 Anotación para recibir parámetros externos desde Maven

import java.io.IOException; // 👉 Para manejar errores de entrada/salida al crear archivos o carpetas
import java.nio.file.*; // 👉 Librería moderna de Java para manipular rutas y archivos (desde Java NIO)

// Genera solo la estructura de carpetas, sin modulos, sin pom ni test, solo carpetas
//  Esta anotación registra el plugin con el nombre del comando "create"
//  requiresProject = false → permite ejecutarlo fuera de un proyecto Maven (por ejemplo, en una carpeta vacía)
@Mojo(name = "create", requiresProject = false)
public class GenerateStructureMojo extends AbstractMojo {

    // Parámetro configurable desde línea de comandos: -DnombreProyecto=mi-app
    // Si no se pasa, usa "demo-app" por defecto
    @Parameter(property = "nombreProyecto", defaultValue = "demo-app")
    private String nombreProyecto;

    // Este método es obligatorio: Maven lo ejecuta cuando se llama al goal "create"
    @Override
    public void execute() throws MojoExecutionException {

        //  Log informativo en consola: indica que se inicia la generación del proyecto
        getLog().info("🚀 Generando estructura para: " + nombreProyecto);

        // Crea un objeto Path con el nombre del proyecto
        //    Ejemplo: si pasas -DnombreProyecto=ms-tareas → baseDir = ./ms-tareas
        Path baseDir = Paths.get(nombreProyecto);

        try {
            // ======================================================
            // 🧩 ESTRUCTURA BASE TIPO ARQUITECTURA HEXAGONAL
            // ======================================================

            // Crea la carpeta de aplicación principal
            Files.createDirectories(baseDir.resolve("applications/app-service/src/main/java"));

            //  Crea la capa de dominio: modelos de negocio y casos de uso
            Files.createDirectories(baseDir.resolve("domain/model/src/main/java"));
            Files.createDirectories(baseDir.resolve("domain/usecase/src/main/java"));

            // Crea la capa de infraestructura (adaptadores, entradas/salidas)
            Files.createDirectories(baseDir.resolve("infrastructure/driven-adapters/src/main/java"));
            Files.createDirectories(baseDir.resolve("infrastructure/entry-points/src/main/java"));

            // Crea carpeta de despliegue (Dockerfile, scripts, etc.)
            Files.createDirectories(baseDir.resolve("deployment"));

            // Crea carpetas para pruebas unitarias e integradas
            Files.createDirectories(baseDir.resolve("tests/unit"));
            Files.createDirectories(baseDir.resolve("tests/integration"));

            //  Log de éxito si todoo se creó correctamente
            getLog().info("✅ Estructura creada exitosamente.");

        } catch (IOException e) {
            // Si ocurre un error (por permisos, rutas inválidas, etc.), Maven lo muestra en consola
            throw new MojoExecutionException("Error al crear estructura", e);
        }
    }
}
