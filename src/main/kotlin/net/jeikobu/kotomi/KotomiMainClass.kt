package net.jeikobu.kotomi

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.jbase.config.AbstractGuildConfig
import net.jeikobu.jbase.config.IGlobalConfig
import net.jeikobu.jbase.impl.config.DBGuildConfig
import net.jeikobu.jbase.impl.config.YAMLGlobalConfig
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.handle.obj.IGuild

private val clientBuilder = ClientBuilder()

private val kotomi = KotomiBot(clientBuilder, object: AbstractConfigManager() {
    val hikariDS = HikariDataSource(HikariConfig("./hikari.properties"))
    val globalConfig = YAMLGlobalConfig()

    override fun getGlobalConfig(): IGlobalConfig {
        return globalConfig
    }

    override fun getGuildConfig(guild: IGuild?): AbstractGuildConfig {
        if (guild == null) throw IllegalArgumentException()
        return DBGuildConfig(guild, hikariDS)
    }
})

fun main(args: Array<String>) {
    kotomi.client.login()
    kotomi.registerCommands()
}