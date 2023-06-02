package net.doemges.kogniswarm.extraction.util

import org.openqa.selenium.chrome.ChromeOptions
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.UUID

class BrowserContainer private constructor(image: DockerImageName) : BrowserWebDriverContainer<BrowserContainer>(image) {
    val id: UUID = UUID.randomUUID()

    companion object {
        fun create(): BrowserContainer = BrowserContainer(
            DockerImageName.parse("seleniarm/standalone-chromium:latest")
                .asCompatibleSubstituteFor("selenium/standalone-chrome")
        ).apply {
            withCapabilities(ChromeOptions().apply {
                addArguments("--headless")
                addArguments("--no-sandbox")
                addArguments("--disable-gpu")
                addArguments("--disable-dev-shm-usage")
                addArguments("--ignore-ssl-errors=yes")
                addArguments("--ignore-certificate-errors")
            })
            withStartupTimeout(Duration.ofSeconds(45))
            start()
        }
    }
}
