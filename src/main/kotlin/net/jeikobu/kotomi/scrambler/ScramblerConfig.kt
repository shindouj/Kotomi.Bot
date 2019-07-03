package net.jeikobu.kotomi.scrambler

import net.dv8tion.jda.core.entities.Role
import net.jeikobu.jbase.config.AbstractGuildConfig

class ScramblerConfig(private val guildConfig: AbstractGuildConfig) {
    private fun getRoleList(): List<Role> {
        val scramblerRoles: String? = guildConfig.getValue(ScramblerKeys.SCRAMBLER_ROLES.configKey)

        return scramblerRoles?.split(", ")?.mapNotNull { x ->
            try {
                guildConfig.guild.getRoleById(x.toLong())
            } catch (e: Exception) {
                null
            }
        } ?: emptyList()
    }

    var scramblerEnabled: Boolean?
        get() = guildConfig.getValue(ScramblerKeys.SCRAMBLER_ENABLED.configKey)
        set(value) = guildConfig.setValue(ScramblerKeys.SCRAMBLER_ENABLED.configKey, value)

    var scramblerInterval: ScramblerInterval?
        get() = ScramblerInterval.fromString(guildConfig.getValue(ScramblerKeys.SCRAMBLER_INTERVAL.configKey))
        set(value) = guildConfig.setValue(ScramblerKeys.SCRAMBLER_INTERVAL.configKey, value.toString())

    var scramblerMode: ScramblerMode?
        get() = ScramblerMode.fromName(guildConfig.getValue(ScramblerKeys.SCRAMBLER_MODE.configKey))
        set(value) = guildConfig.setValue(ScramblerKeys.SCRAMBLER_MODE.configKey, value?.modeName)

    var scramblerRoles: List<Role>?
        get() = getRoleList()
        set(value) = guildConfig.setValue(ScramblerKeys.SCRAMBLER_ROLES.configKey, value?.joinToString { it.id })

    var scramblerTick: Int?
        get() = guildConfig.getValue(ScramblerKeys.SCRAMBLER_TICK.configKey)
        set(value) = guildConfig.setValue(ScramblerKeys.SCRAMBLER_TICK.configKey, value)
}

enum class ScramblerKeys(val configKey: String) {
    SCRAMBLER_ENABLED("scramblerEnabled"),
    SCRAMBLER_INTERVAL("scramblerInterval"),
    SCRAMBLER_MODE("scramblerMode"),
    SCRAMBLER_ROLES("scramblerRoles"),
    SCRAMBLER_TICK("scramblerTick")
}