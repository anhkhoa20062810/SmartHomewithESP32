pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // Toolchain giúp quản lý phiên bản JDK tự động, rất tốt cho dự án Android hiện đại
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // BẮT BUỘC: Để tải thư viện biểu đồ MPAndroidChart
        maven { url = uri("https://jitpack.io") }

        // Cần thiết nếu bạn dùng một số thư viện Voice/AI khác
        mavenLocal()
    }
}

rootProject.name = "ESP32_LED"
include(":app")