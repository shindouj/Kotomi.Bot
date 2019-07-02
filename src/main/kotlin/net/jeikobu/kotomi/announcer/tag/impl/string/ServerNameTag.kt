package net.jeikobu.kotomi.announcer.tag.impl.string

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.User
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.announcer.tag.AnnouncerTag

class ServerNameTag(configManager: AbstractConfigManager) : AnnouncerTag<String>(configManager) {
    private val optionsRegex = Regex("(?<=\\{serverName:)(.*)(?=})")

    override fun getRegex(): Regex {
        return Regex("\\{serverName[\\s\\S]*?}")
    }

    override fun getData(tag: String, user: User, guild: Guild): String {
        return guild.name
    }
}