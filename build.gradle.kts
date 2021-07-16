plugins {
    val kotlinVersion = "1.4.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.6.7"
}

group = "indi.goldenwater"
version = "1.0.1"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
