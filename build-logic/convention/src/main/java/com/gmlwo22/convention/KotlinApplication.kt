package com.gmlwo22.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = 34

        defaultConfig {
            minSdk = 21
            // Required when setting minSdkVersion to 20 or lower
            // multiDexEnabled = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17

            isCoreLibraryDesugaringEnabled = true
            //위의 옵션은 Java 8 을 지원 하지 않는 디바이스(sdk version < 26)에서 Java 8 기능을 지원 해 주기 위한 옵션
            /**
             * 컴파일 과정에서 Desugaring을 수행하게 하면 됩니다.
             * 우리가 사실은 사용할 수 없는데 java.time 을 사용할 수 있게 해주는 것을 Sugaring 이라고 하고
             * 컴파일 과정에서 D8이 그걸 다시 API 26 보다 낮은 기기에서
             * 해석할 수 있는 .dex 코드로 변환시켜주는 과정을 Desugaring 이라고 합니다.
             */
        }
    }
    configureKotlin()
    dependencies {
        /**
         * isCoreLibraryDesugaringEnabled 를 활성화 하기 위한 dependency
         */
        add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
    }
}

internal fun Project.configureKotlinJvm() {
    println("configureKotlinJvm")
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    configureKotlin()
}

private fun Project.configureKotlin() {
    //Kotlin 컴파일 테스크에 대해 설정을 수행함.
    tasks.withType<KotlinCompile>().configureEach {
        println("KotlinCompile")
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
            val warningsAsErrors: String? by project
            println("warningsAsErrors : $warningsAsErrors")
            // gradle.properties에서 정의한 값으로 설정하여 모든 Kotlin 경고를 오류로 처리(기본적으로 비활성화)
            allWarningsAsErrors = warningsAsErrors.toBoolean()
            freeCompilerArgs = freeCompilerArgs + listOf(
                // Flow를 포함한 실험적 코루틴 API 사용
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        }
    }
}