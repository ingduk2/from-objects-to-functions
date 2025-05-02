plugins {
    id("project-conventions")
    id("kotlin-conventions")
}

val http4kVersion: String by project

dependencies {
    implementation("org.http4k:http4k-core:${http4kVersion}")
    implementation("org.http4k:http4k-server-jetty:${http4kVersion}")

    testImplementation("org.http4k:http4k-client-jetty:${http4kVersion}")
}