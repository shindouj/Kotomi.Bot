package net.jeikobu.kotomi.announcer.tag

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.User
import net.jeikobu.jbase.Localized
import net.jeikobu.jbase.config.AbstractConfigManager

abstract class AnnouncerTag<T>(configManager: AbstractConfigManager): Localized() {
    abstract fun getRegex(): Regex

    /**
     *
     */
    open fun matches(element:String): Boolean {
        return getRegex().containsMatchIn(element)
    }

    /**
     * This method replaces tag in the message with actual data from the guild config.
     */
    fun replaceData(message: String, user: User, guild: Guild): String {
        return if (matches(message)) {
            var messageCopy = message

            for (tag in getRegex().findAll(message)) {
                messageCopy = messageCopy.replace(tag.value, getData(tag.value, user, guild).toString())
            }

            messageCopy
        } else {
            message
        }
    }

    /**
     * This method simply returns the correct data for the tag.
     */
    abstract fun getData(tag: String, user: User, guild: Guild): T

    /**
     * This method initializes tag's settings and if they're unneeded in the announcement after initialization,
     * gets rid of them and replaces them in the message, hence the String argument and String return type.
     *
     * Override when needed.
     */
    internal open fun initializeSettings(tag: String, guild: Guild) {
        return
    }

    fun initialize(message: String, guild: Guild) {
        if (matches(message)) {
            for (tag in getRegex().findAll(message)) {
                initializeSettings(tag.value, guild)
            }
        }
    }

    fun splitOptions(options: String): Map<String, String> {
        return if (options.isEmpty()) {
            emptyMap()
        } else {
            options.split(",").associateBy({ it.split("=")[0] }, { it.split("=")[1] })
        }
    }
}