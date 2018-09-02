package net.jeikobu.kotomi

import net.jeikobu.jbase.AbstractBot
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.jbase.impl.commands.ChangeLocaleCommand
import net.jeikobu.kotomi.commands.AboutCommand
import sx.blah.discord.api.ClientBuilder

class KotomiBot(clientBuilder: ClientBuilder?, configManager: AbstractConfigManager?) : AbstractBot(clientBuilder, configManager) {
    fun registerCommands() {
        commandManager.registerCommand(AboutCommand::class.java)
    }
}

