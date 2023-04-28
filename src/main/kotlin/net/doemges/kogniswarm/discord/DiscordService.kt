package net.doemges.kogniswarm.discord

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.flow.*
import org.springframework.stereotype.Service
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value

@Service
class DiscordService {
    private lateinit var kord: Kord

    @Value("\${discord.token}")
    private lateinit var token: String

    @Value("\${discord.guildId}")
    private lateinit var serverGuildId: String

    init {
        runBlocking {
            kord = Kord(token!!)
            kord.login()
        }
    }


    fun listChannels(guildId: String = serverGuildId): List<TextChannel> = runBlocking {
        val guild = kord.getGuildOrNull(Snowflake(guildId))
        guild?.channels?.filterIsInstance<TextChannel>()
                ?.toList() ?: emptyList()
    }

    fun listUsers(guildId: String = serverGuildId): List<User> = runBlocking {
        val guild = kord.getGuildOrNull(Snowflake(guildId))
        guild?.members?.mapNotNull { it.asUser() }
                ?.toList() ?: emptyList()
    }

    fun sendMessageToChannel(channelId: String, message: String): Message = runBlocking {
        val channel = kord.getChannel(Snowflake(channelId)) as? TextChannel
        channel?.createMessage(message) ?: throw IllegalArgumentException("Invalid channel ID")
    }

    fun sendMessageToUser(userId: String, message: String): Message = runBlocking {
        val user = kord.getUser(Snowflake(userId))
        val dmChannel = user?.getDmChannel()
        dmChannel?.createMessage(message) ?: throw IllegalArgumentException("Invalid user ID")
    }

    fun listen(guildId: String = serverGuildId): Flow<Message> = kord
            .events
            .filterIsInstance<MessageCreateEvent>()
            .mapNotNull { it.message }
            .filter { msg ->
                msg.getGuildOrNull()
                        ?.id
                        ?.toString()
                        ?.let { it == guildId } ?: true
            }

    fun listenToChannel(channelId: String, guildId: String = serverGuildId): Flow<Message> = listen(serverGuildId)
            .filter { it.channelId.toString() == channelId }

    fun listenToConversationWithUser(userId: String, guildId: String = serverGuildId): Flow<Message> =
        listen(serverGuildId)
                .filter { it.author?.id?.toString() == userId }
}
