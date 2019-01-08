package net.jeikobu.kotomi.scrambler

import net.jeikobu.jbase.config.AbstractGuildConfig
import java.util.*

private fun <T> Optional<T>.unwrap(): T? = orElse(null)

fun AbstractGuildConfig.getValue(key: ScramblerKeys): String? {
    return getValue(key.configKey, String::class.java).unwrap()
}

fun AbstractGuildConfig.setValue(key: ScramblerKeys, value: String) {
    setValue(key.configKey, value)
}

class ScramblerConfig(private val guildConfig: AbstractGuildConfig) {

    val configMap: Map<ScramblerKeys, String> = ScramblerKeys.values().mapNotNull {
        val optionalValue = guildConfig.getValue(it.configKey, String::class.java).unwrap()
        if (optionalValue != null) {
            it to optionalValue
        } else {
            null
        }
    }.toMap()
}

enum class ScramblerKeys(val configKey: String) {
    SCRAMBLER_ENABLED("scramblerEnabled"),
    SCRAMBLER_INTERVAL("scramblerInterval"),
    SCRAMBLER_MODE("scramblerMode"),
    SCRAMBLER_ROLES("scramblerRoles"),
    SCRAMBLER_TICK("scramblerTick")
}