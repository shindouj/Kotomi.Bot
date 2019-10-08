package net.jeikobu.kotomi.reactionroles.commands

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.exceptions.ErrorResponseException
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import net.jeikobu.kotomi.getReactionConfig
import net.jeikobu.kotomi.reactionroles.RRVolatileKeys
import java.lang.Exception
import java.lang.NumberFormatException

private const val noMessageErrorCode = 10008

@Command(name = "addRole", argsLength = 2, permissions = [Permission.ADMINISTRATOR])
class AddReactionRoleCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: Message) {
        var reactionMessage: Message? = null

        for (channel in destinationGuild.textChannels) {
            reactionMessage = try {
                channel.getMessageById(getVolatile(RRVolatileKeys.LAST_MESSAGE.name)?.toLong() ?: 0L).complete()
            } catch (e: ErrorResponseException) {
                if (e.errorResponse.code != noMessageErrorCode) {
                    destinationChannel.sendMessage(getLocalized("discordError", e.errorResponse.code, e.errorResponse.meaning)).queue()
                }
                null
            } catch (e: Exception) {
                destinationChannel.sendMessage(getLocalized("generalError", e.localizedMessage)).queue()
                null
            }

            if (reactionMessage != null) {
                break
            }
        }

        val role = try {
            destinationGuild.getRoleById(args[0].toLong())
        } catch (e: NumberFormatException) {
            try {
                destinationGuild.getRolesByName(args[0], false).first()
            } catch (e: Exception) {
                null
            }
        }

        val emojiId = try {
            args[1].toLong()
        } catch (e: NumberFormatException) {
            try {
                (args[1].removePrefix("<").removeSuffix(">").split(":")[2]).toLong()
            } catch (e: Exception) {
                0L
            }
        }

        val nameElems = args[1].removePrefix("<").removeSuffix(">").split(":")

        val name = if (nameElems.size > 2) {
            null
        } else args[1]

        val emoji = destinationGuild.jda.getEmoteById(emojiId)

        if (reactionMessage != null && role != null && (emoji != null || name != null)) {
            if (emoji != null) {
                getReactionConfig().addReactionRole(reactionMessage, role, emoji)
                reactionMessage.addReaction(emoji).complete()
            } else if (name != null) {
                getReactionConfig().addReactionRole(reactionMessage, role, name)
                reactionMessage.addReaction(name).complete()
            }

            destinationChannel.sendMessage(getLocalized("success")).queue()
        } else {
            destinationChannel.sendMessage(getLocalized("failure")).queue()
        }
    }
}