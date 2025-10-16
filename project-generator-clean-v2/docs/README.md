# 🧱 Project Generator Clean v2

**Autor:** Iván David Duque Perdomo  
**Fecha de creación:** 5 de octubre de 2025  
**Versión:** 2.0.0  
**Lenguaje:** Java 21  
**Plugin Maven:** `com.idduque.plugins.v2:project-generator-clean-v2`

---

## 🚀 Descripción general

El **Project Generator Clean v2** es un **plugin Maven personalizado** que automatiza la creación de proyectos basados en **Arquitectura Limpia (Clean Architecture)** con estructura multi-módulo.

Su propósito principal es agilizar el inicio de nuevos desarrollos, garantizando una estructura ordenada, coherente y alineada con las buenas prácticas de ingeniería de software.

El plugin genera automáticamente:
- Estructura de carpetas base.
- Módulos Maven (`app-service`, `model`, `usecase`, `driven-adapters`, `entry-points`).
- Clases principales (`MainApplication` con `@SpringBootApplication`).
- POM raíz y POMs hijos totalmente configurados.
- Archivo `README.md` con metadatos del proyecto.

---

## 🎯 Objetivo general

Automatizar la generación de proyectos Java basados en **Arquitectura Limpia** bajo un modelo **multi-módulo Maven**, para estandarizar estructuras y reducir tiempos de configuración manual.

---

## 🎯 Objetivos específicos

1. Crear automáticamente las carpetas y submódulos base de un proyecto limpio.
2. Generar los archivos `pom.xml` raíz e hijos con jerarquía Maven válida.
3. Proveer una clase `MainApplication` lista para ejecución con Spring Boot.
4. Incluir metadatos del proyecto en un `README.md` autogenerado.
5. Facilitar la extensibilidad para arquitecturas futuras (como hexagonal o DDD).

---

## 🧩 Estructura generada

``` 
📦 mi-proyecto-demo/
│
├── pom.xml                      # POM raíz con módulos
├── README.md                    # Documentación generada automáticamente
│
├── applications/
│   └── app-service/
│       ├── pom.xml
│       └── src/
│           ├── main/java/com/idduque/clean/MainApplication.java
│           ├── main/resources/
│           └── test/java/
│
├── domain/
│   ├── model/
│   │   ├── pom.xml
│   │   ├── src/main/java/
│   │   └── src/test/java/
│   └── usecase/
│       ├── pom.xml
│       ├── src/main/java/
│       └── src/test/java/
│
└── infrastructure/
    ├── driven-adapters/
    │   ├── pom.xml
    │   └── src/main/java/
    └── entry-points/
        ├── pom.xml
        └── src/main/java/
```
## ⚙️ Parámetros configurables

| 🧩 Parámetro     | 📝 Descripción                                  | 💻 Uso                                   | 🔧 Valor por defecto       |
|------------------|-------------------------------------------------|------------------------------------------|-----------------------------|
| `nombreProyecto` | Nombre base del proyecto a generar              | `-DnombreProyecto=mi_app`                | `clean-app`                 |
| `packageBase`    | Paquete principal del proyecto                  | `-DpackageBase=com.idduque.demo`         | `com.idduque.clean`         |
| `javaVersion`    | Versión de Java para compilar el proyecto       | `-DjavaVersion=21`                      | `21`                        |

## 🧭 Ejemplo de uso
1️⃣ Desde consola Maven
```java
mvn com.idduque.plugins.v2:project-generator-clean-v2:2.0.0:scaffold \
    -DnombreProyecto=ms_demo \
    -DpackageBase=com.idduque.demo \
    -DjavaVersion=21

```
## 🧰 Ejecución dentro de IntelliJ IDEA
1. Abre IntelliJ y asegúrate de tener Maven configurado.

2. Instala el plugin localmente:
```java
mvn clean install
```
Crea un nuevo proyecto vacío donde desees generar la estructura.

Abre la ventana de Maven (View → Tool Windows → Maven).

En el panel de ejecución, usa el siguiente goal:
com.idduque.plugins.v2:project-generator-clean-v2:2.0.0:scaffold
Agrega tus parámetros (-DnombreProyecto, -DpackageBase, -DjavaVersion) en la sección Runner → VM Options.

Ejecutar en cmd



🧩 Diagrama conceptual de la arquitectura generada
``` 
                      ┌────────────────────────────┐
                      │        app-service         │
                      │  (Main + Configuración)    │
                      └────────────┬───────────────┘
                                   │
         ┌─────────────────────────┴─────────────────────────┐
         │                                                   │
┌────────▼────────┐                                  ┌───────▼────────┐
│     usecase     │                                  │     model      │
│ Lógica negocio  │                                  │ Entidades core │
└────────┬────────┘                                  └───────┬────────┘
│                                                   │
▼                                                   ▼
┌────────────────┐                                 ┌────────────────┐
│ driven-adapter │                                 │ entry-points   │
│ Infraestructura│                                 │ Interfaces I/O │
└────────────────┘                                 └────────────────┘
```

🧱 Buenas prácticas aplicadas

Principio de responsabilidad única (SRP) en cada método.

Separación clara entre generación de carpetas, clases y POMs.

Código compatible con Maven 3.9+ y JDK 8–22.

Registros claros con emojis para mayor legibilidad en consola.

Modularidad para extender a arquitecturas como Hexagonal o DDD.

## 🧩 Diagrama UML - Project Generator Clean v2

```mermaid
classDiagram
    class GenerateScaffoldCleanStructure {
        - String nombreProyecto
        - String packageBase
        - String javaVersion
        - String adapterName
        - String entryName
        - boolean crearCarpetaRaiz
        + execute()
        - determinarDirectorioBase()
    }

    class StructureCreator {
        - Log log
        - String packageBase
        - String adapterName
        - String entryName
        + crearEstructura(Path baseDir)
    }

    class PomGenerator {
        - Log log
        - String packageBase
        - String nombreProyecto
        - String javaVersion
        - String adapterName
        - String entryName
        + crearPomRaiz(Path baseDir)
        + crearPomsHijos(Path baseDir)
        - generarPomHijo(Path moduleDir, String artifactId, boolean esAppService)
        - calcularRutaRelativa(Path moduleDir)
    }

    class FileUtils {
        - Log log
        + writeFile(Path filePath, String content, Log log)
        + crearMainApplication(Path baseDir, String packageBase)
        + crearReadme(Path baseDir, String nombreProyecto, String javaVersion)
    }

    GenerateScaffoldCleanStructure --> StructureCreator : usa
    GenerateScaffoldCleanStructure --> PomGenerator : usa
    GenerateScaffoldCleanStructure --> FileUtils : usa
    PomGenerator --> FileUtils : escribe archivos


👨‍💻 Créditos

Este plugin fue desarrollado por Iván David Duque Perdomo,
como parte de un proyecto de automatización de plantillas Java con Arquitectura Limpia.

Repositorio base: https://github.com/davidduque29

Licencia: MIT
Contacto: ivduque@hotmail.com