package net.jeikobu.kotomi.announcer

import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.Permissions

@Command(name = "announcer", argsLength = 2, permissions = [Permissions.ADMINISTRATOR])
class AnnouncerConfigCommand(data: CommandData) : AbstractCommand(data) {
    private val announcerName = args[0].toLowerCase()

    override fun run(message: IMessage?) {
        // not using announcerName to denote that it's not about the announcer name but an exceptional command
        if (args[0].toLowerCase() == "setchannel") {
            setChannel()
            return
        }

        if (!Announcements.values().map { it -> it.announcementName }.contains(announcerName)) {
            destinationChannel.sendMessage(getLocalized("unsupportedAnnouncement", announcerName))
            return
        }

        when (args[1]) {
            "enable" -> setAnnouncerEnabled(true)
            "disable" -> setAnnouncerEnabled(false)
            "set" -> setAnnouncer()
        }
    }

    private fun setChannel() {
        val channelConfigKey = AnnouncerConfigKeys.ANNOUNCER_CHANNEL.configKey

        try {
            guildConfig.setValue(channelConfigKey, destinationGuild.getChannelByID(args[1].toLong()).name)
        } catch (e: NumberFormatException) {
            val searchResults = destinationGuild.getChannelsByName(args[1])

            if (searchResults.isNotEmpty()) {
                if (searchResults.size > 1) {
                    destinationChannel.sendMessage(getLocalized("ambiguousChannelName", args[1]))
                    return
                }

                guildConfig.setValue(channelConfigKey, searchResults.first().name)
            } else {
                destinationChannel.sendMessage(getLocalized("channelNotFound", args[1]))
                return
            }
        }

        destinationChannel.sendMessage(getLocalized("channelSet", args[1]))
    }

    private fun setAnnouncerEnabled(enabled: Boolean) {
        guildConfig.setValue(announcerName + AnnouncerConfigKeys.ANNOUNCER_ENABLED.configKey, enabled.toString())
        destinationChannel.sendMessage(
            getLocalized("announcementEnabled", getLocalized(announcerName), getLocalized(enabled.toString())))
    }

    private fun setAnnouncer() {
        val announcement = args.subList(2, args.size).joinToString(separator = " ")
        guildConfig.setValue(announcerName + AnnouncerConfigKeys.ANNOUNCEMENT.configKey, announcement)
        destinationChannel.sendMessage(getLocalized("announcementSet", getLocalized(announcerName), announcement))
    }
}