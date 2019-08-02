package net.jeikobu.kotomi

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Guild
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.jbase.config.AbstractGuildConfig
import net.jeikobu.jbase.impl.config.DBGuildConfig
import net.jeikobu.jbase.impl.config.YAMLGlobalConfig
import net.jeikobu.kotomi.reactionroles.ReactionConfig
import net.jeikobu.kotomi.scrambler.ScramblerCommand
import net.jeikobu.kotomi.scrambler.ScramblerConfig
import net.jeikobu.kotomi.scrambler.ScramblerTask
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val clientBuilder = JDABuilder()
val hikariDS = HikariDataSource(HikariConfig("config/hikari.properties"))

private val kotomi = KotomiBot(object : AbstractConfigManager() {
    override val globalConfig = YAMLGlobalConfig()

    override fun getGuildConfig(guild: Guild): AbstractGuildConfig {
        return DBGuildConfig(guild, hikariDS)
    }
})

fun <T : AbstractCommand> T.getReactionConfig(): ReactionConfig {
    return kotomi.reactionConfig
}

fun getVersion(): String {
    return kotomi.javaClass.`package`.implementationVersion ?: "Dev"
}

fun main() {
    kotomi.registerCommands()
    kotomi.client.presence.setPresence(OnlineStatus.ONLINE, Game.playing("v" + getVersion()))
    val tickExecutor = Executors.newScheduledThreadPool(1)
    tickExecutor.scheduleAtFixedRate(ScramblerTask(kotomi.configManager, kotomi.client.guilds), 0, 10, TimeUnit.SECONDS)
}