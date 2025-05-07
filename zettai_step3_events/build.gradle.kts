plugins {
    id("project-conventions")
    id("kotlin-conventions")
}

val http4kVersion: String by project
val pesticideVersion: String by project
val jsoupVersion: String by project

dependencies {
    implementation("org.http4k:http4k-core:${http4kVersion}")
    implementation("org.http4k:http4k-server-jetty:${http4kVersion}")

    testImplementation("com.ubertob.pesticide:pesticide-core:${pesticideVersion}")
    testImplementation("org.http4k:http4k-client-jetty:${http4kVersion}")
    testImplementation("org.jsoup:jsoup:${jsoupVersion}")
}