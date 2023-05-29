package net.doemges.kogniswarm.extraction

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import reactor.core.publisher.Mono
import java.time.Duration

class SeleniumExtractor(private val webDriver: WebDriver) {
    fun extract(url: String, contentExtractor: ContentExtractor) = webDriver.get(url)
        .let {
            contentExtractor.extract(getAllTextOnPage(webDriver))
        }

    private fun getAllTextOnPage(driver: WebDriver): Mono<String> = Mono.create {
        WebDriverWait(
            driver,
            Duration.ofSeconds(10)
        ).until(ExpectedConditions.jsReturnsValue("return document.readyState=='complete';"))
        it.success(driver.findElement(By.tagName("body")).text)
    }


}