package io.github.configure

import io.github.extension.libs
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.*

internal fun Project.configureVerifyDetekt() {
    pluginManager.apply("io.gitlab.arturbosch.detekt")

    setupDetekt(extensions.getByType<DetektExtension>())

    dependencies {
        "detektPlugins"(libs.findLibrary("verify.detektFormatting").get())
    }
}

fun Project.setupDetekt(extension: DetektExtension) {
    extension.apply {
        allRules = false // 안정적인 규칙만 사용
        parallel = true // 병렬 처리 활성화
        buildUponDefaultConfig = true // 기본 설정 기반 추가 설정
        ignoreFailures = false // 분석 실패 시 빌드 실패 처리
        autoCorrect = false // 자동 수정 비활성화

        // 설정 파일 경로 지정
        config.setFrom("${project.rootDir}/config/detekt/detekt.yml")
        // 기준 파일 경로 지정
        baseline = file("${project.rootDir}/config/detekt/baseline.xml")
    }

    // Detekt 리포트 병합 태스크 등록
    val reportMerge = rootProject.tasks.findByName("reportMerge") ?: rootProject.tasks.register("reportMerge", ReportMergeTask::class.java) {
        output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.xml"))
    }

    plugins.withType<DetektPlugin> {
        tasks.withType<Detekt>().configureEach {
            // Detekt 실행 후 병합 태스크 실행
            finalizedBy(reportMerge)

            // 소스 파일 경로 설정
            source = fileTree(".") {
                include("**/*.kt", "**/*.kts") // .kt와 .kts 파일만 포함
                exclude("**/resources/**", "**/build/**") // 리소스와 빌드 폴더 제외
            }

            // 병합 태스크 입력으로 XML 리포트 전달
            @Suppress("UNCHECKED_CAST")
            (reportMerge as TaskProvider<ReportMergeTask>).configure {
                input.from(xmlReportFile)
            }
        }
    }
}
