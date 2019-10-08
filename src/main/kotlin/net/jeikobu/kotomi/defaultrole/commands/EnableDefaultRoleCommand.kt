package net.jeikobu.kotomi.defaultrole.commands

import net.dv8tion.jda.core.entities.Message
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import net.jeikobu.kotomi.defaultrole.DefaultRoleConfig

@Command(name = "defaultRole", argsLength = 1)
class EnableDefaultRoleCommand(data: CommandData): AbstractCommand(data) {
    override fun run(message: Message) {
        when(args[0]) {
            "enable" -> enableDefaultRole()
            "disable" -> disableDefaultRole()
        }
    }

    private fun enableDefaultRole() {
        guildConfig.setValue(DefaultRoleConfig.DEFAULT_ROLE_ENABLED_CONFIG_KEY, true)
        destinationChannel.sendMessage("<LOCALIZE_IT> Default role enabled!").queue()
    }

    private fun disableDefaultRole() {
        guildConfig.setValue(DefaultRoleConfig.DEFAULT_ROLE_ENABLED_CONFIG_KEY, false)
        destinationChannel.sendMessage("<LOCALIZE_IT> Default role disabled!").queue()
    }

}