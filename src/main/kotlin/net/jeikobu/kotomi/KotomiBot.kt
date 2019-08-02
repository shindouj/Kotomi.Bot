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
import net.jeikobu.kotomi.reactionroles.AddReactionRoleCommand
import net.jeikobu.kotomi.reactionroles.ReactionConfig
import net.jeikobu.kotomi.reactionroles.ReactionListener
import net.jeikobu.kotomi.reactionroles.RegisterMessageCommand
import net.jeikobu.kotomi.scrambler.ScramblerCommand
import net.jeikobu.kotomi.scrambler.ScramblerListener

class KotomiBot(val configManager: AbstractConfigManager) : AbstractBot(configManager) {
    val reactionConfig = ReactionConfig(hikariDS)

    fun registerCommands() {
        TagManager.registerTag(RegionalSuffixTag(configManager))
        TagManager.registerTag(CustomCounterTag(configManager))
        TagManager.registerTag(UserCountTag(configManager))
        TagManager.registerTag(ServerNameTag(configManager))
        TagManager.registerTag(UserNameTag(configManager))

        client.addEventListener(AnnouncerListener(configManager))
        client.addEventListener(ScramblerListener(configManager))
        client.addEventListener(ReactionListener(configManager, reactionConfig))


        commandManager.registerCommand(AboutCommand::class)
        commandManager.registerCommand(AnnouncerConfigCommand::class)
        commandManager.registerCommand(ScramblerCommand::class)
        commandManager.registerCommand(DadJokeCommand::class)
        commandManager.registerCommand(RegisterMessageCommand::class)
        commandManager.registerCommand(AddReactionRoleCommand::class)
    }
}

