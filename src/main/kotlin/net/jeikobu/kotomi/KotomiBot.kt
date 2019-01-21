package net.jeikobu.kotomi

import net.jeikobu.jbase.AbstractBot
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.announcer.AnnouncerConfigCommand
import net.jeikobu.kotomi.announcer.AnnouncerListener
import net.jeikobu.kotomi.announcer.tag.TagManager
import net.jeikobu.kotomi.announcer.tag.impl.numeric.CustomCounterTag
import net.jeikobu.kotomi.announcer.tag.impl.numeric.UserCountTag
import net.jeikobu.kotomi.announcer.tag.impl.string.RegionalSuffixTag
import net.jeikobu.kotomi.announcer.tag.impl.string.ServerNameTag
import net.jeikobu.kotomi.announcer.tag.impl.string.UserNameTag
import net.jeikobu.kotomi.commands.AboutCommand
import net.jeikobu.kotomi.commands.DadJokeCommand
import net.jeikobu.kotomi.scrambler.ScramblerCommand
import net.jeikobu.kotomi.scrambler.ScramblerListener
import net.jeikobu.kotomi.scrambler.ScramblerTask
import sx.blah.discord.api.ClientBuilder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class KotomiBot(clientBuilder: ClientBuilder?, configManager: AbstractConfigManager?) : AbstractBot(clientBuilder, configManager) {
    fun registerCommands() {
        TagManager.registerTag(RegionalSuffixTag(configManager))
        TagManager.registerTag(CustomCounterTag(configManager))
        TagManager.registerTag(UserCountTag(configManager))
        TagManager.registerTag(ServerNameTag(configManager))
        TagManager.registerTag(UserNameTag(configManager))

        client.dispatcher.registerListener(AnnouncerListener(configManager))
        client.dispatcher.registerListener(ScramblerListener(configManager))

        commandManager.registerCommand(AboutCommand::class.java)
        commandManager.registerCommand(AnnouncerConfigCommand::class.java)
        commandManager.registerCommand(ScramblerCommand::class.java)
        commandManager.registerCommand(DadJokeCommand::class.java)
    }


}

