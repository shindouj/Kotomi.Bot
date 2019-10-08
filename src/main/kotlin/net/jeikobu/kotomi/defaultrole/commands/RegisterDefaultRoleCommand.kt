package net.jeikobu.kotomi.defaultrole.commands

import net.dv8tion.jda.core.entities.Message
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import net.jeikobu.kotomi.defaultrole.DefaultRoleConfigException
import net.jeikobu.kotomi.defaultrole.DefaultRoleTriggers
import net.jeikobu.kotomi.getDefaultRoleConfig
import org.pmw.tinylog.Logger
import java.lang.Exception
import java.lang.NumberFormatException

@Command(name = "registerDefaultRole", argsLength = 2)
class RegisterDefaultRoleCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: Message) {
        val defaultRoleConfig = getDefaultRoleConfig()

        val role = try {
            destinationGuild.getRoleById(args[0].toLong())
        } catch (e: NumberFormatException) {
            try {
                destinationGuild.getRolesByName(args[0], false).first()
            } catch (e: Exception) {
                null
            }
        }

        val trigger = DefaultRoleTriggers.getByName(args[1])

        if (role != null && trigger != null) {
            try {
                defaultRoleConfig.register(trigger, role, destinationGuild)
                destinationChannel.sendMessage("<LOCALIZE_IT> Added role: ${role.name} with a trigger $trigger!").queue()
            } catch (e: DefaultRoleConfigException) {
                destinationChannel.sendMessage("<LOCALIZE_IT> ${e.message}").queue()
                Logger.error(e)
            } catch (e: Exception) {
                destinationChannel.sendMessage("<LOCALIZE_IT> Adding the role has failed. See logs for details.").queue()
                Logger.error(e)
            }
        } else {
            destinationChannel.sendMessage("<LOCALIZE_IT> Failed to add role: ${args[0]}").queue()
        }
    }
}