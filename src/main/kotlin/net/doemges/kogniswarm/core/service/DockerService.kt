package net.doemges.kogniswarm.core.service

import net.doemges.kogniswarm.core.util.CommandExecutor
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.util.Locale

@Service
@Lazy
class DockerService(private val commandExecutor: CommandExecutor) {

    private val logger = LoggerFactory.getLogger(javaClass)

    enum class OperatingSystem {
        MAC,
        WINDOWS,
        LINUX,
        UNKNOWN
    }


    init {
        logger.debug("initializing DockerService...")
        val operatingSystem = getOperatingSystem()
        logger.debug("Operating System: $operatingSystem")
        when (operatingSystem) {
            OperatingSystem.MAC -> handleDockerOnMac()
            else -> logger.debug("Unsupported operating system")
        }
        logger.debug("DockerService initialized.")
    }

    private fun getOperatingSystem(): OperatingSystem {
        val os = System.getProperty("os.name")
            .lowercase(Locale.getDefault())
        return when {
            os.contains("mac") -> OperatingSystem.MAC
            os.contains("win") -> OperatingSystem.WINDOWS
            os.contains("nux") -> OperatingSystem.LINUX
            else -> OperatingSystem.UNKNOWN
        }
    }

    private fun handleDockerOnMac() {
        if (!isDockerRunning("docker info")) {
            logger.debug("Docker is not running. Starting Docker...")
            startDocker("open --background -a Docker")
        }

        while (!isDockerRunning("docker info")) {
            try {
                logger.debug("Waiting for Docker to start...")
                Thread.sleep(2000)  // wait for 2 seconds
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        logger.debug("Docker has been started.")
    }


    @Suppress("SameParameterValue")
    private fun isDockerRunning(command: String): Boolean {
        return try {
            val output = commandExecutor.executeCommand(command)
            logger.debug(output)
            val dockerRunning =
                !output.contains("Cannot connect to the Docker daemon") && !output.contains("Error response from daemon:")
            if (!dockerRunning) {
                logger.error(output)
            }
            dockerRunning
        } catch (e: Exception) {
            false
        }
    }

    @Suppress("SameParameterValue")
    private fun startDocker(command: String) {
        try {
            commandExecutor.executeCommand(command)
            logger.debug("Docker has been started.")
        } catch (e: Exception) {
            logger.error("Failed to start Docker: ${e.message}")
        }
    }
}
