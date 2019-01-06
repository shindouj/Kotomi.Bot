package net.jeikobu.kotomi.announcer.tag.impl.numeric

import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.announcer.tag.NumericTag
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser
import java.lang.NumberFormatException

class CustomCounterTag(private val configManager: AbstractConfigManager) : NumericTag(configManager) {
    companion object {
        const val configKey = "customUserCounter"
    }

    private val optionsRegex = Regex("(?<=\\{counter:)(.*)(?=})")

    override fun getRegex(): Regex {
        return Regex("\\{counter[\\s\\S]*?}")
    }

    override fun getData(tag: String, user: IUser, guild: IGuild): Int {
        val data = configManager.getGuildConfig(guild).getValue(configKey, Int::class.java)

        if (data.isPresent) {
            return data.get()
        } else {
            throw UninitializedPropertyAccessException()
        }
    }

    override fun initializeSettings(tag: String, guild: IGuild): String {
        val optionsMap = splitOptions(optionsRegex.find(tag)?.value?.replace("}", "") ?: "")

        val reset = optionsMap["reset"]?.toLowerCase() ?: "true" == "true"
        val startStr = optionsMap["start"]

        if (startStr != null && !reset) {
            try {
                val start = startStr.toInt()
                configManager.getGuildConfig(guild).setValue(configKey, start.toString())
            } catch (e: NumberFormatException) {
                configManager.getGuildConfig(guild).setValue(configKey, "0")
            }
        } else if (reset || !configManager.getGuildConfig(guild).getValue(configKey, String::class.java).isPresent) {
            configManager.getGuildConfig(guild).setValue(configKey, "0")
        } 

        return tag
    }
}