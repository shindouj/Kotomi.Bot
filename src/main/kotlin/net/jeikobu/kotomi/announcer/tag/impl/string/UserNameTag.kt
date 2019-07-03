package net.jeikobu.kotomi.announcer.tag.impl.string

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.User
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.announcer.tag.AnnouncerTag

class UserNameTag(configManager: AbstractConfigManager) : AnnouncerTag<String>(configManager) {
    private val optionsRegex = Regex("(?<=\\{userName:)(.*)(?=})")

    override fun getRegex(): Regex {
        return Regex("\\{userName[\\s\\S]*?}")
    }

    override fun getData(tag: String, user: User, guild: Guild): String {
        val options = splitOptions(optionsRegex.find(tag)?.value ?: "")

        return if (options["noMention"] != null && options["noMention"] == "true") {
            user.name
        } else {
            user.asMention
        }
    }
}