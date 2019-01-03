package net.jeikobu.kotomi

import net.jeikobu.jbase.AbstractBot
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.jbase.impl.commands.ChangeLocaleCommand
import net.jeikobu.kotomi.announcer.AnnouncerConfigCommand
import net.jeikobu.kotomi.announcer.AnnouncerListener
import net.jeikobu.kotomi.commands.AboutCommand
import sx.blah.discord.api.ClientBuilder

class KotomiBot(clientBuilder: ClientBuilder?, configManager: AbstractConfigManager?) : AbstractBot(clientBuilder, configManager) {
    fun registerCommands() {
        client.dispatcher.registerListener(AnnouncerListener(configManager))
        commandManager.registerCommand(AboutCommand::class.java)
        commandManager.registerCommand(AnnouncerConfigCommand::class.java)
    }
}

