import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.ADOPTIUM
    }
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

dependencies {
    if (configurations.findByName("implementation")?.dependencies?.any { it.name.contains("spring-boot-starter") } == true) {
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    }

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.strikt:strikt-core:0.35.1")
}