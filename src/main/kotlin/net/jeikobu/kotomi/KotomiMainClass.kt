package net.jeikobu.kotomi

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.jbase.config.AbstractGuildConfig
import net.jeikobu.jbase.config.IGlobalConfig
import net.jeikobu.jbase.impl.config.DBGuildConfig
import net.jeikobu.jbase.impl.config.YAMLGlobalConfig
import net.jeikobu.kotomi.scrambler.ScramblerTask
import org.pmw.tinylog.Logger
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.handle.obj.ActivityType
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.StatusType
import java.lang.Thread.sleep
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private val clientBuilder = ClientBuilder()

private val kotomi = KotomiBot(clientBuilder, object: AbstractConfigManager() {
    val hikariDS = HikariDataSource(HikariConfig("config/hikari.properties"))
    val globalConfig = YAMLGlobalConfig()

    override fun getGlobalConfig(): IGlobalConfig {
        return globalConfig
    }

    override fun getGuildConfig(guild: IGuild?): AbstractGuildConfig {
        if (guild == null) throw IllegalArgumentException()
        return DBGuildConfig(guild, hikariDS)
    }
})

fun getVersion(): String {
    return kotomi.javaClass.`package`.implementationVersion ?: "Dev"
}

fun main(args: Array<String>) {
    kotomi.client.login()
    kotomi.registerCommands()

    var sleepCounter = 0

    while(!kotomi.client.isReady) {
        sleepCounter += 10
        sleep(10)

        if (sleepCounter > 10000) {
            Logger.error("Discord Client did not manage to login after 10 seconds. Exiting.")
            throw Error("Discord Client did not manage to login after 10 seconds. Exiting.")
        }
    }

    kotomi.client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, "v" + getVersion())

    val tickExecutor = Executors.newScheduledThreadPool(1)
    tickExecutor.scheduleAtFixedRate(ScramblerTask(kotomi.configManager, kotomi.client.guilds),
                                     0, 1, TimeUnit.MINUTES)
}