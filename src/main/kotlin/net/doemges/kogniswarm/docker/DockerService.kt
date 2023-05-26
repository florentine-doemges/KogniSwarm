package net.doemges.kogniswarm.docker

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale
import kotlin.streams.asSequence

@Service
class DockerService {

    private val logger = LoggerFactory.getLogger(javaClass)

    enum class OperatingSystem {
        MAC,
        WINDOWS,
        LINUX,
        UNKNOWN
    }

    init {
        when (getOperatingSystem()) {
            OperatingSystem.MAC -> handleDockerOnMac()
            else -> println("Unsupported operating system")
        }
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
            startDocker("open --background -a Docker")
        }

        while (!isDockerRunning("docker info")) {
            try {
                logger.info("Waiting for Docker to start...")
                Thread.sleep(2000)  // wait for 2 seconds
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }


    private fun isDockerRunning(command: String): Boolean {
        return try {
            val process = Runtime.getRuntime()
                .exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            val last = reader.lines()
                .asSequence()
                .last()
            val dockerRunning = !last
                .contains("Cannot connect to the Docker daemon") && !last.contains("Error response from daemon:")
            if (!dockerRunning) {
                logger.error(last)
            }
            dockerRunning
        } catch (e: Exception) {
            false
        }
    }

    private fun startDocker(command: String) {
        try {
            Runtime.getRuntime()
                .exec(command)
            println("Docker has been started.")
        } catch (e: Exception) {
            println("Failed to start Docker.")
        }
    }
}
