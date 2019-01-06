package net.jeikobu.kotomi.announcer.tag.impl.string

import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.announcer.tag.AnnouncerTag
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser

class ServerNameTag(configManager: AbstractConfigManager) : AnnouncerTag<String>(configManager) {
    private val optionsRegex = Regex("(?<=\\{serverName:)(.*)(?=})")

    override fun getRegex(): Regex {
        return Regex("\\{serverName[\\s\\S]*?}")
    }

    override fun getData(tag: String, user: IUser, guild: IGuild): String {
        return guild.name
    }
}