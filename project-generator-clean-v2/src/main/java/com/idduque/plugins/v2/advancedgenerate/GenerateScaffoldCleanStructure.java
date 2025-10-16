package com.idduque.plugins.v2.advancedgenerate;

import com.idduque.plugins.v2.advancedgenerate.utils.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
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

    @Parameter(property = "crearCarpetaRaiz", defaultValue = "true")
    private boolean crearCarpetaRaiz;

    @Parameter(property = "useCurrentProject", defaultValue = "false")
    private boolean useCurrentProject;

    @Parameter(property = "springBootVersion", defaultValue = "3.3.2")
    private String springBootVersion;


    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("🚀 Iniciando generación de proyecto: " + nombreProyecto);
        Path baseDir = determinarDirectorioBase();

        try {
            crearEstructuraProyecto(baseDir);
            generarPoms(baseDir);
            crearArchivosBase(baseDir);

            getLog().info("✅ Proyecto generado exitosamente en: " + baseDir.toAbsolutePath());
        } catch (IOException e) {
            manejarErrorGeneracion(e);
        }
    }

    private void crearEstructuraProyecto(Path baseDir) throws IOException {
        StructureCreator estructura = new StructureCreator(getLog(), packageBase, adapterName, entryName);
        estructura.crearEstructura(baseDir);
        getLog().info("📁 Estructura base creada correctamente en: " + baseDir);
    }

    private void generarPoms(Path baseDir) throws IOException {
        PomGenerator pomGenerator = new PomGenerator(
                getLog(),
                packageBase,
                nombreProyecto,
                javaVersion,
                adapterName,
                entryName,
                springBootVersion
        );
        pomGenerator.crearPomRaiz(baseDir);
        pomGenerator.crearPomsHijos(baseDir);
        getLog().info("📦 Archivos POM generados exitosamente.");
    }

    private void crearArchivosBase(Path baseDir) throws IOException {
        FileUtils fileUtils = new FileUtils(getLog());
        fileUtils.crearMainApplication(baseDir, packageBase);
        fileUtils.crearReadme(baseDir, nombreProyecto, javaVersion);
        getLog().info("📝 Archivos base (MainApplication, README) creados correctamente.");
    }

    private void manejarErrorGeneracion(IOException e) throws MojoExecutionException {
        getLog().error("❌ Error al generar el proyecto: " + e.getMessage());
        throw new MojoExecutionException("Error al generar la estructura del proyecto", e);
    }

    /**
     * Determina el directorio base donde se generará la estructura del proyecto.
     * Si se especifica useCurrentProject=true, el scaffold se genera en el proyecto actual.
     * En caso contrario, crea una nueva carpeta con el nombre del proyecto.
     */
    private Path determinarDirectorioBase() {
        String currentDir = System.getProperty("user.dir");

        // Si se indica que se use el proyecto actual, no crea una nueva carpeta
        if (useCurrentProject) {
            getLog().info("📂 Generando estructura dentro del proyecto actual: " + currentDir);
            return Paths.get(currentDir);
        }

        // Si no, crea una carpeta nueva dentro del directorio actual
        getLog().info("📁 Creando nueva carpeta base para el proyecto: " + nombreProyecto);
        return Paths.get(currentDir, nombreProyecto);
    }

}