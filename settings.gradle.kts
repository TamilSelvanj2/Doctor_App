pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://github.com/QuickBlox/quickblox-android-sdk-releases/raw/master/")
        }
    }
}


rootProject.name = "DoctorModule"
include(":app")
 