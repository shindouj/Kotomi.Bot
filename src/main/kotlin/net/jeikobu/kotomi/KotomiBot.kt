package net.jeikobu.kotomi

import net.jeikobu.jbase.AbstractBot
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.commands.AboutCommand
import net.jeikobu.kotomi.scrambler.ScramblerCommand
import sx.blah.discord.api.ClientBuilder

class KotomiBot(clientBuilder: ClientBuilder?, configManager: AbstractConfigManager?) : AbstractBot(clientBuilder, configManager) {
    fun registerCommands() {
        commandManager.registerCommand(AboutCommand::class.java)
        commandManager.registerCommand(ScramblerCommand::class.java)
    }
}

