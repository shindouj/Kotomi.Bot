package net.jeikobu.kotomi.announcer.tag.impl.string

import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.announcer.tag.AnnouncerTag
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser

class UserNameTag(configManager: AbstractConfigManager) : AnnouncerTag<String>(configManager) {
    private val optionsRegex = Regex("(?<=\\{userName:)(.*)(?=})")

    override fun getRegex(): Regex {
        return Regex("\\{userName[\\s\\S]*?}")
    }

    override fun getData(tag: String, user: IUser, guild: IGuild): String {
        val options = splitOptions(optionsRegex.find(tag)?.value ?: "")

        return if (options["noMention"] != null && options["noMention"] == "true") {
            user.name
        } else {
            user.mention()
        }
    }
}