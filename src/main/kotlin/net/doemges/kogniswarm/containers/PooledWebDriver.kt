package net.doemges.kogniswarm.containers

import kotlinx.coroutines.runBlocking
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

class PooledWebDriver(
    private val returnFunction: suspend (BrowserContainer) -> Unit,
    private val getContainer: suspend () -> BrowserContainer,
) : AutoCloseable, WebDriver {
    private val webDriver: WebDriver by lazy { container.webDriver }

    private val container: BrowserContainer by lazy {
        runBlocking {
            getContainer()
        }
    }

    override fun findElements(by: By?): MutableList<WebElement> = webDriver.findElements(by)

    override fun findElement(by: By?): WebElement = webDriver.findElement(by)

    override fun get(url: String?) = webDriver.get(url)

    override fun getCurrentUrl(): String = webDriver.currentUrl

    override fun getTitle(): String = webDriver.title

    override fun getPageSource(): String = webDriver.pageSource

    override fun close() = quit()

    override fun quit() {
        webDriver.quit()
        runBlocking {
            returnFunction(container)
        }
    }

    override fun getWindowHandles(): MutableSet<String> = webDriver.windowHandles

    override fun getWindowHandle(): String = webDriver.windowHandle

    override fun switchTo(): WebDriver.TargetLocator = webDriver.switchTo()

    override fun navigate(): WebDriver.Navigation = webDriver.navigate()

    override fun manage(): WebDriver.Options = webDriver.manage()


}