package net.jeikobu.kotomi.announcer

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.guild.GuildBanEvent
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.core.hooks.EventListener
import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.GuildConfigKeys
import net.jeikobu.kotomi.announcer.tag.TagManager
import java.lang.IllegalArgumentException

class AnnouncerListener(private val configManager: AbstractConfigManager) : EventListener {
    private fun getAnnouncementChannel(guild: Guild): TextChannel {
        val channelName: String? = configManager.getGuildConfig(guild).getValue(AnnouncerConfigKeys.ANNOUNCER_CHANNEL.configKey)

        return if (channelName != null) {
            guild.getTextChannelsByName(channelName, true).first()
        } else {
            guild.systemChannel ?: throw IllegalArgumentException()
        }
    }

    private fun prepareAnnouncement(announcement: String, user: User, guild: Guild): String {
        return TagManager.processMessage(announcement, user, guild)
    }

    private fun sendAnnouncement(announcementEvent: Announcements, guild: Guild, user: User) {
        val guildConfig = configManager.getGuildConfig(guild)
        val isEnabled: Boolean? = guildConfig.getValue(
            announcementEvent.announcementName + AnnouncerConfigKeys.ANNOUNCER_ENABLED.configKey)

        if (isEnabled != null && isEnabled) {
            val announcement: String? = configManager.getGuildConfig(guild)
                    .getValue(announcementEvent.announcementName + AnnouncerConfigKeys.ANNOUNCEMENT.configKey)
            if (announcement != null) {
                val announcementChannel = getAnnouncementChannel(guild)
                announcementChannel.sendMessage(prepareAnnouncement(announcement, user, guild)).queue()
            }
        }
    }

    fun incrementCounters(guild: Guild) {
        val guildConfig = configManager.getGuildConfig(guild)

        val userJoinCounter: Int? = guildConfig.getValue(GuildConfigKeys.USER_JOIN_COUNTER.key)
        if (userJoinCounter != null) {
            guildConfig.setValue(GuildConfigKeys.USER_JOIN_COUNTER.key, (userJoinCounter + 1).toString())
        } else {
            guildConfig.setValue(GuildConfigKeys.USER_JOIN_COUNTER.key, guild.members.size.toString())
        }

        val customUserCounter: Int? = guildConfig.getValue(GuildConfigKeys.CUSTOM_USER_COUNTER.key)
        if (customUserCounter != null) {
            guildConfig.setValue(GuildConfigKeys.CUSTOM_USER_COUNTER.key, (customUserCounter + 1).toString())
        }
    }

    override fun onEvent(event: Event) {
        when {
            event is GuildMemberJoinEvent -> {
                incrementCounters(event.guild)
                sendAnnouncement(Announcements.ANNOUNCEMENT_JOIN, event.guild, event.user)
            }
            event is GuildMemberLeaveEvent -> {
                sendAnnouncement(Announcements.ANNOUNCEMENT_LEAVE, event.guild, event.user)
            }
            event is GuildBanEvent -> {
                sendAnnouncement(Announcements.ANNOUNCEMENT_BAN, event.guild, event.user)
            }
        }
    }
}