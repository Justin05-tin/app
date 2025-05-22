// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    // Chú ý: Bạn cần thêm cả classpath cho google-services
}

buildscript {
    repositories {
        google()
        mavenCentral()
      
    }


    dependencies {
        // Đảm bảo rằng bạn đã thêm đúng classpath cho Firebase plugin
        classpath("com.google.gms:google-services:4.3.15")
    // Phiên bản mới nhất của google-services plugin
    }
}

// Tăng cường các phụ thuộc và cài đặt cho các thư viện khác nếu cần.
