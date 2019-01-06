package net.jeikobu.kotomi.announcer.tag

import net.jeikobu.jbase.Localized
import net.jeikobu.jbase.config.AbstractConfigManager
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser

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
    fun replaceData(message: String, user: IUser, guild: IGuild): String {
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
    abstract fun getData(tag: String, user: IUser, guild: IGuild): T

    /**
     * This method initializes tag's settings and if they're unneeded in the announcement after initialization,
     * gets rid of them and replaces them in the message, hence the String argument and String return type.
     *
     * Override when needed.
     */
    internal open fun initializeSettings(tag:String, guild: IGuild): String {
        return tag
    }

        fun initialize(message:String, guild: IGuild): String {
        return if (matches(message)) {
            var messageCopy = message

            for (tag in getRegex().findAll(message)) {
                messageCopy = messageCopy.replace(tag.value, initializeSettings(tag.value, guild))
            }

            messageCopy
        } else {
            message
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