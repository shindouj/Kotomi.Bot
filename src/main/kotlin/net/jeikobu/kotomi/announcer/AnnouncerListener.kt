package net.jeikobu.kotomi.announcer

import net.jeikobu.jbase.config.AbstractConfigManager
import net.jeikobu.kotomi.GuildConfigKeys
import net.jeikobu.kotomi.announcer.tag.TagManager
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser
import java.lang.NullPointerException

class AnnouncerListener(private val configManager: AbstractConfigManager) {
    private fun getAnnouncementChannel(guild: IGuild): IChannel {
        val channelName = configManager.getGuildConfig(guild)
                .getValue(AnnouncerConfigKeys.ANNOUNCER_CHANNEL.configKey, String::class.java)

        return if (channelName.isPresent) {
            guild.getChannelsByName(channelName.get()).first()
        } else {
            guild.defaultChannel
        }
    }

    private fun prepareAnnouncement(announcement: String, user: IUser, guild: IGuild, channel: IChannel): String {
        return TagManager.processMessage(announcement, user, guild)
    }

    private fun sendAnnouncement(announcementEvent: Announcements, guild: IGuild, user: IUser) {
        val guildConfig = configManager.getGuildConfig(guild)
        val isEnabled = guildConfig.getValue(
            announcementEvent.announcementName + AnnouncerConfigKeys.ANNOUNCER_ENABLED.configKey, String::class.java)

        if (isEnabled.isPresent && isEnabled.get() == "true") {
            val announcement = configManager.getGuildConfig(guild)
                    .getValue(announcementEvent.announcementName + AnnouncerConfigKeys.ANNOUNCEMENT.configKey,
                              String::class.java)
            if (announcement.isPresent) {
                val announcementChannel = getAnnouncementChannel(guild)
                announcementChannel.sendMessage(
                    prepareAnnouncement(announcement.get(), user, guild, announcementChannel))
            }
        }
    }

    fun incrementCounters(guild: IGuild) {
        val guildConfig = configManager.getGuildConfig(guild)

        val userJoinCounter = guildConfig.getValue(GuildConfigKeys.USER_JOIN_COUNTER.key, Int::class.java)
        if (userJoinCounter.isPresent) {
            guildConfig.setValue(GuildConfigKeys.USER_JOIN_COUNTER.key, (userJoinCounter.get() + 1).toString())
        } else {
            guildConfig.setValue(GuildConfigKeys.USER_JOIN_COUNTER.key, guild.totalMemberCount.toString())
        }

        val customUserCounter = guildConfig.getValue(GuildConfigKeys.CUSTOM_USER_COUNTER.key, Int::class.java)
        if (customUserCounter.isPresent) {
            guildConfig.setValue(GuildConfigKeys.CUSTOM_USER_COUNTER.key, (customUserCounter.get() + 1).toString())
        }
    }

    @EventSubscriber
    fun onUserJoin(e: UserJoinEvent) {
        incrementCounters(e.guild)
        sendAnnouncement(Announcements.ANNOUNCEMENT_JOIN, e.guild, e.user)
    }

    @EventSubscriber
    fun onUserLeave(e: UserLeaveEvent) {
        sendAnnouncement(Announcements.ANNOUNCEMENT_LEAVE, e.guild, e.user)
    }

    @EventSubscriber
    fun onUserBan(e: UserBanEvent) {
        sendAnnouncement(Announcements.ANNOUNCEMENT_BAN, e.guild, e.user)
    }


}