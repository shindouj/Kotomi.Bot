package net.jeikobu.kotomi.reactionroles.commands

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.exceptions.ErrorResponseException
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import net.jeikobu.kotomi.getReactionConfig
import net.jeikobu.kotomi.reactionroles.RRVolatileKeys
import net.jeikobu.kotomi.reactionroles.ReactionMessageTypes
import java.lang.Exception
import java.lang.NumberFormatException

private const val noMessageErrorCode = 10008

@Command(name = "reactionRole", argsLength = 2, permissions = [Permission.ADMINISTRATOR])
class RegisterMessageCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: Message) {
        val reactionConfig = getReactionConfig()
        val mode = ReactionMessageTypes.valueOf(args[0].toUpperCase())

        val reactionMessageID = try {
            args[1].toLong()
        } catch (e: NumberFormatException) {
            0L
        }

        var reactionMessage: Message? = null

        for (channel in destinationGuild.textChannels) {
            reactionMessage = try {
                channel.getMessageById(reactionMessageID).complete()
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

        if (reactionMessage != null) {
            reactionConfig.registerMessage(reactionMessage, mode)
            setVolatile(RRVolatileKeys.LAST_MESSAGE.name, reactionMessageID.toString())
            destinationChannel.sendMessage(getLocalized("success", reactionMessageID)).queue()
        } else {
            destinationChannel.sendMessage(getLocalized("failure", reactionMessageID)).queue()
        }
    }
}