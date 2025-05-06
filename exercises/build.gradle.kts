plugins {
    id("project-conventions")
    id("kotlin-conventions")
}

val pesticideVersion: String by project

dependencies {
    testImplementation("com.ubertob.pesticide:pesticide-core:${pesticideVersion}")
}