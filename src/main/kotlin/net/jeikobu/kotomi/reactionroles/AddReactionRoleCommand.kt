package net.jeikobu.kotomi.reactionroles

import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import net.jeikobu.kotomi.getReactionConfig
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.Permissions
import java.lang.NumberFormatException

@Command(name = "addRole", argsLength = 2, permissions = [Permissions.ADMINISTRATOR])
class AddReactionRoleCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: IMessage) {
        val message = destinationGuild.getMessageByID(getVolatile(RRVolatileKeys.LAST_MESSAGE.name)?.toLong() ?: 0L)

        val roleID = try {
            args[0].toLong()
        } catch (e: NumberFormatException) {
            0L
        }

        val role = destinationGuild.getRoleByID(roleID)
        val emoji = destinationGuild.getEmojiByName(args[1])

        if (message != null && role != null && emoji != null) {
            getReactionConfig().addReactionRole(message, role, emoji)
            message.addReaction(emoji)
        }
    }
}