pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "from-objects-to-functions"

include(
    "exercises",
    "zettai_step1_http",
    "zettai_step2_domain",
)