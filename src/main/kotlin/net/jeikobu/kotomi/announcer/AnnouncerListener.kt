package net.jeikobu.kotomi.announcer

import net.jeikobu.jbase.config.AbstractConfigManager
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.member.UserBanEvent
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser

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
        var announcementCopy: String = announcement.replace("{userName}", user.mention())
                .replace("{serverName}", guild.name)
                .replace("{channelName}", channel.name)
                .replace("{userCount}", guild.totalMemberCount.toString())

        val channelList = announcementCopy.split(" ").mapNotNull { it ->
            if (it.startsWith("{#") && it.endsWith("}")) {
                val list = guild.getChannelsByName(it.substringAfterLast("#").substringBeforeLast("}"))
                if (list.size == 1) {
                    list.first()
                } else null
            } else null
        }

        for (mentionChannel in channelList) {
            announcementCopy = announcementCopy.replace("{#" + mentionChannel.name + "}", mentionChannel.mention())
        }

        return announcementCopy
    }

    private fun sendAnnouncement(announcementEvent: Announcements, guild: IGuild, user: IUser) {
        val guildConfig = configManager.getGuildConfig(guild);
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

    @EventSubscriber
    fun onUserJoin(e: UserJoinEvent) {
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