package net.jeikobu.kotomi.reactionroles

import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import net.jeikobu.kotomi.getReactionConfig
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.Permissions

@Command(name = "reactionRole", argsLength = 2, permissions = [Permissions.ADMINISTRATOR])
class RegisterMessageCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: IMessage) {
        val reactionConfig = getReactionConfig()
        val mode = ReactionMessageTypes.valueOf(args[0].toUpperCase())
        val reactionMessageID = args[1].toLong()
        val reactionMessage = destinationGuild.getMessageByID(reactionMessageID)

        if (reactionMessage != null) {
            reactionConfig.registerMessage(reactionMessage, mode)
            setVolatile(RRVolatileKeys.LAST_MESSAGE.name, reactionMessageID.toString())
            destinationChannel.sendMessage(getLocalized("success", reactionMessageID))
        } else {
            destinationChannel.sendMessage(getLocalized("failure", reactionMessageID))
        }
    }
}