package net.doemges.kogniswarm.agent

class AgentCommandParser {
    private val commands = listOf(
        AgentCommand(
            "defineMission", "Defines a new mission", listOf(
                "mission" to "The mission to define", "description" to "The description of the mission"
            )
        ) {
            Mission(it["mission"]!!, it["description"]!!).let { mission ->
                missions.add(mission)
                "Mission ${mission.name} defined: ${mission.description}"
            }
        },
        AgentCommand(
            "listMissions", "Lists all missions"
        ) {
            missions.joinToString("\n") { mission -> "${mission.name}: ${mission.description}" }
        },
        AgentCommand(
            "stopMission", "Stops a mission", listOf("mission" to "The mission to stop")
        ) {
            missions.find { mission -> mission.name == it["mission"] }
                ?.let { mission ->
                    mission.status = MissionStatus.INACTIVE
                    "Mission ${mission.name} stopped"
                } ?: error("Mission ${it["mission"]} not found")

        },
        AgentCommand(
            "startMission", "Starts a mission", listOf(
                "mission" to "The mission to start"
            )
        ) {
            missions.find { mission -> mission.name == it["mission"] }
                ?.let { mission ->
                    mission.status = MissionStatus.ACTIVE
                    "Mission ${mission.name} started"
                } ?: error("Mission ${it["mission"]} not found")
        },
        AgentCommand(
            "missionStatus", "Shows the status of a mission", listOf(
                "mission" to "The mission to show the status of"
            )
        ) {
            missions.find { mission -> mission.name == it["mission"] }
                ?.let { mission ->
                    "Mission ${mission.name} is ${mission.status}"
                } ?: error("Mission ${it["mission"]} not found")
        },
    )

    fun processCommand(agent: Agent, content: String): String =
        (this.commands
            .filter { command -> content.contains(command.name) }
            .takeIf { it.isNotEmpty() }
            ?: error("No command found in '$content'")
            )
            .first()
            .let { command ->
                val parts = content
                    .substringAfter(command.name)
                    .trim()
                    .split(" ")

                val args = mutableMapOf<String, String>()

                parts.forEachIndexed { index, part ->
                    if (index < command.args.size) {
                        args[command.args[index].first] = part
                    } else {
                        val lastArgKey = command.args.last().first
                        args[lastArgKey] = args.getOrDefault(lastArgKey, "") + " " + part
                    }
                }

                command.block(agent, args)
            }


}