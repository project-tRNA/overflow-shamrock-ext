plugins {
    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.gmazzo.buildconfig") version "3.1.0"
    id("net.mamoe.mirai-console") version "2.16.0"
}

group = "top.mrxiaom"
version = "0.1.0"

buildConfig {
    className("BuildConstants")
    packageName("${project.group}.overflow.shamrock")
    useKotlinOutput()

    buildConfigField("String", "VERSION", "\"${project.version}\"")
}

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

mirai {
    noTestCore = true
    setupConsoleTestRuntime {
        classpath = classpath.filter {
            !it.nameWithoutExtension.startsWith("mirai-core-jvm")
        }
    }
}

dependencies {
    compileOnly("top.mrxiaom:overflow-core-api:2.16.0")
    compileOnly("net.mamoe:mirai-core-utils:2.16.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")

    testConsoleRuntime("top.mrxiaom:overflow-core:2.16.0")
}
