plugins { id("com.android.application") }
android {
    namespace = "org.qingjian.lspds"
    compileSdk = 37
    defaultConfig {
        applicationId = "org.qingjian.lspds"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-dev"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildTypes {
        debug { applicationIdSuffix = ".debug"; versionNameSuffix = "-debug" }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    packaging { resources { merges += "META-INF/xposed/*" } }
    lint { abortOnError = true; checkReleaseBuilds = false }
    testOptions { unitTests.isIncludeAndroidResources = true }
}
dependencies {
    compileOnly("io.github.libxposed:api:102.0.0")
    testImplementation("junit:junit:4.13.2")
}
