package net.jeikobu.kotomi.announcer.tag.impl.numeric

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.User
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.announcer.tag.NumericTag
import java.lang.NumberFormatException

class CustomCounterTag(private val configManager: AbstractConfigManager) : NumericTag(configManager) {
    companion object {
        const val configKey = "customUserCounter"
    }

    private val optionsRegex = Regex("(?<=\\{counter:)(.*)(?=})")

    override fun getRegex(): Regex {
        return Regex("\\{counter[\\s\\S]*?}")
    }

    override fun getData(tag: String, user: User, guild: Guild): Int {
        return configManager.getGuildConfig(guild).getValue(configKey) ?: throw UninitializedPropertyAccessException()
    }

    override fun initializeSettings(tag: String, guild: Guild) {
        val optionsMap = splitOptions(optionsRegex.find(tag)?.value?.replace("}", "") ?: "")

        val initWithCurrentUserCount = optionsMap["initWithUserCount"]?.toLowerCase() ?: "false" == "true"
        val reset = optionsMap["reset"]?.toLowerCase() ?: "true" == "true"
        val startStr = optionsMap["start"]

        if (initWithCurrentUserCount) {
            configManager.getGuildConfig(guild).setValue(configKey, guild.members.size.toString())
            return
        }

        if (startStr != null && !reset) {
            try {
                val start = startStr.toInt()
                configManager.getGuildConfig(guild).setValue(configKey, start.toString())
            } catch (e: NumberFormatException) {
                configManager.getGuildConfig(guild).setValue(configKey, "0")
            }
        } else if (reset || configManager.getGuildConfig(guild).getValue(configKey, valueType = String::class) == null) {
            configManager.getGuildConfig(guild).setValue(configKey, "0")
        }
    }
}