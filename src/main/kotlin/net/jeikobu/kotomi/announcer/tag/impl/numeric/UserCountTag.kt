package net.jeikobu.kotomi.announcer.tag.impl.numeric

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.User
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.announcer.tag.NumericTag

class UserCountTag(configManager: AbstractConfigManager) : NumericTag(configManager) {
    override fun getRegex(): Regex {
        return Regex("\\{userCount[\\s\\S]*?}")
    }

    override fun getData(tag: String, user: User, guild: Guild): Int {
        return guild.members.size
    }

}