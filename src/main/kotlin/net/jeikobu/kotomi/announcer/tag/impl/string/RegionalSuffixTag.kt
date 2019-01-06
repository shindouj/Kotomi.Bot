package net.jeikobu.kotomi.announcer.tag.impl.string

import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.announcer.tag.AnnouncerTag
import net.jeikobu.kotomi.announcer.tag.NumericTag
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser

class RegionalSuffixTag(private val configManager: AbstractConfigManager) : AnnouncerTag<String>(configManager) {
    private val outerRegex = Regex("\\{regionalSuffix:[\\s\\S]*?}}")
    private val innerRegex = Regex("(?<=\\{regionalSuffix:)(.*)(?=})")

    override fun getRegex(): Regex {
        return outerRegex
    }

    override fun matches(element: String): Boolean {
        return outerRegex.containsMatchIn(element) && getInnerTag(outerRegex.find(element)?.value ?: "nothing") != null
    }

    override fun getData(tag: String, user: IUser, guild: IGuild): String {
        val innerTagStringRep = innerRegex.find(tag)?.value
        val innerTag = getInnerTag(tag)

        if (innerTag != null && innerTagStringRep != null) {
            val data = innerTag.getData(innerTagStringRep, user, guild)
            return data.toString() + getLocalized(configManager.getLocale(guild), "endswith_" + data.toString().last())
        }

        return tag
    }

    private fun getInnerTag(outerTag: String): NumericTag? {
        return NumericTag.new(innerRegex.find(outerTag)?.value ?: "nothing")
    }

    override fun initializeSettings(tag: String, guild: IGuild): String {
        val innerTagStringRep = innerRegex.find(tag)?.value
        val innerTag = getInnerTag(tag)

        if (innerTag != null && innerTagStringRep != null) {
            return tag.replace(innerTagStringRep, innerTag.initializeSettings(innerTagStringRep, guild))
        }

        return super.initializeSettings(tag, guild)
    }
}