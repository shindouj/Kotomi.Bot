package net.jeikobu.kotomi.announcer.tag.impl.numeric

import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.announcer.tag.NumericTag
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser

class UserCountTag(configManager: AbstractConfigManager) : NumericTag(configManager) {
    override fun getRegex(): Regex {
        return Regex("\\{userCount[\\s\\S]*?}")
    }

    override fun getData(tag: String, user: IUser, guild: IGuild): Int {
        return guild.totalMemberCount
    }

}