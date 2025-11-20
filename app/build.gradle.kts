import org.gradle.kotlin.dsl.libs
import java.util.Properties

plugins {
   alias(libs.plugins.android.application)
   alias(libs.plugins.kotlin.android)
   alias(libs.plugins.kotlin.compose)
   id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
   alias(libs.plugins.ksp)
   alias(libs.plugins.dagger.hilt)
   alias(libs.plugins.apollo.graphql)
   id("kotlin-parcelize")
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()

if (localPropertiesFile.exists()) {
   localPropertiesFile.inputStream().use {
      localProperties.load(it)
   }
} else error("ERROR: No local.properties file found")

android {
   namespace = "com.example.anyme"
   compileSdk = 36

   defaultConfig {
      applicationId = "com.example.anyme"
      minSdk = 24
      targetSdk = 36
      versionCode = 1
      versionName = "1.0"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   buildTypes {
      release {
         isMinifyEnabled = false
         proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
         )
      }
   }
   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_22
      targetCompatibility = JavaVersion.VERSION_22
      // jvmTarget = "23"
   }
   buildFeatures {
      compose = true
      buildConfig = true
   }

}

apollo {
   service("service") {
      packageName.set("com.example")

      introspection {
         val endpoint = localProperties.getProperty("ANILIST_ENDPOINT")
            ?: error("ERROR: No anilist endpoint url found")
         endpointUrl.set(endpoint)
         schemaFile.set(file("src/main/graphql/schema.graphqls"))
      }
   }
}

dependencies {

   implementation(libs.androidx.core.ktx)
   implementation(libs.androidx.lifecycle.runtime.ktx)
   implementation(libs.androidx.activity.compose)
   implementation(platform(libs.androidx.compose.bom))
   implementation(libs.androidx.ui)
   implementation(libs.androidx.ui.graphics)
   implementation(libs.androidx.ui.tooling.preview)
   implementation(libs.androidx.material3)
   implementation(libs.androidx.compose.material.icons.extended)
   implementation(libs.androidx.hilt.navigation.compose)
   implementation(libs.androidx.datastore.preferences)
   implementation(libs.androidx.foundation)
   implementation(libs.androidx.navigation.compose)
   testImplementation(libs.junit)
   androidTestImplementation(libs.androidx.junit)
   androidTestImplementation(libs.androidx.espresso.core)
   androidTestImplementation(platform(libs.androidx.compose.bom))
   androidTestImplementation(libs.androidx.ui.test.junit4)
   debugImplementation(libs.androidx.ui.tooling)
   debugImplementation(libs.androidx.ui.test.manifest)

   implementation(libs.androidx.appcompat)

   // Coroutines
   implementation(libs.kotlinx.coroutines.android)

   //AppAuth
   implementation(libs.appauth)
   implementation(libs.jwtdecode)

   // Retrofit
   implementation(libs.retrofit)
   implementation(libs.converter.gson)
   implementation(libs.converter.scalars)

   // Gson
   implementation(libs.gson)

   // Dagger-Hilt
   implementation(libs.hilt.android)
   ksp(libs.hilt.android.compiler)

   //Room
   implementation(libs.androidx.room.runtime)
   testImplementation(libs.androidx.room.testing)
   implementation(libs.androidx.room.ktx)
   ksp(libs.androidx.room.compiler)

   // Jsoup
   implementation(libs.jsoup)

   // Glide
   implementation(libs.compose)

   // ViewModel x Compose
   implementation(libs.androidx.lifecycle.viewmodel.compose)

   // Swipe-To-Refresh
   implementation(libs.accompanist.swiperefresh)

   // Pagination
   implementation(libs.androidx.room.paging)
   implementation(libs.androidx.paging.runtime.ktx)
   implementation(libs.androidx.paging.compose)

   // Kotlinx DateTime
   implementation(libs.kotlinx.datetime)
   coreLibraryDesugaring(libs.desugar.jdk.libs)

   // Apollo GraphQL
   implementation(libs.apollo.graphql)

   // Kotlin Reflections
   implementation(libs.kotlin.reflect)

}