plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.englishapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.englishapp"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/ASL2.0"
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:13.0.0")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:chromecast-sender:0.32")

    // Web và API YouTube
    implementation("com.google.api-client:google-api-client-android:2.2.0")
    implementation("com.google.apis:google-api-services-youtube:v3-rev20230807-2.0.0")
    implementation("com.google.http-client:google-http-client-gson:1.43.3")
    implementation("com.google.http-client:google-http-client-android:1.43.3")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")

    implementation("com.squareup.picasso:picasso:2.8")
    implementation("org.json:json:20210307")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.room3.common.jvm)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation(libs.vision.common)
    implementation(libs.play.services.mlkit.text.recognition.common)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.media:media:1.7.0")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
