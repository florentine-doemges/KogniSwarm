package net.doemges.kogniswarm.extraction

import kotlinx.coroutines.flow.Flow
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import java.time.Duration

class SeleniumExtractor(private val webDriver: WebDriver) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun extract(url: String, contentExtractor: ContentExtractor): Flow<Extract> {
        logger.info("fetching with selenium: $url")
        return webDriver.get(url)
            .let {
                contentExtractor.extract(getAllTextOnPage(webDriver))
            }
    }

    private fun getAllTextOnPage(driver: WebDriver): Mono<String> = Mono.create {
        WebDriverWait(
            driver,
            Duration.ofSeconds(10)
        ).until(ExpectedConditions.jsReturnsValue("return document.readyState=='complete';"))
        it.success(driver.findElement(By.tagName("body")).text)
    }


}