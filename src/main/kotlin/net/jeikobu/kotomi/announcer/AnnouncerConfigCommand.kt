package net.jeikobu.kotomi.announcer

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import net.jeikobu.kotomi.announcer.tag.TagManager

@Command(name = "announcer", argsLength = 2, permissions = [Permission.ADMINISTRATOR])
class AnnouncerConfigCommand(data: CommandData) : AbstractCommand(data) {
    private val announcerName = if (args.isNotEmpty()) { args[0].toLowerCase() } else { "" }

    override fun run(message: Message) {
        // not using announcerName to denote that it's not about the announcer name but an exceptional command
        if (args[0].toLowerCase() == "setchannel") {
            setChannel()
            return
        }

        if (!Announcements.values().map { it.announcementName }.contains(announcerName)) {
            destinationChannel.sendMessage(getLocalized("unsupportedAnnouncement", announcerName)).queue()
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
            guildConfig.setValue(channelConfigKey, destinationGuild.getTextChannelById(args[1].toLong()).name)
        } catch (e: NumberFormatException) {
            val searchResults = destinationGuild.getTextChannelsByName(args[1], true)

            if (searchResults.isNotEmpty()) {
                if (searchResults.size > 1) {
                    destinationChannel.sendMessage(getLocalized("ambiguousChannelName", args[1])).queue()
                    return
                }

                guildConfig.setValue(channelConfigKey, searchResults.first().name)
            } else {
                destinationChannel.sendMessage(getLocalized("channelNotFound", args[1])).queue()
                return
            }
        }

        destinationChannel.sendMessage(getLocalized("channelSet", args[1])).queue()
    }

    private fun setAnnouncerEnabled(enabled: Boolean) {
        guildConfig.setValue(announcerName + AnnouncerConfigKeys.ANNOUNCER_ENABLED.configKey, enabled.toString())
        destinationChannel.sendMessage(
            getLocalized("announcementEnabled", getLocalized(announcerName), getLocalized(enabled.toString()))).queue()
    }

    private fun setAnnouncer() {
        val announcement = args.subList(2, args.size).joinToString(separator = " ")

        TagManager.initializeAnnouncement(announcement, destinationGuild)
        guildConfig.setValue(announcerName + AnnouncerConfigKeys.ANNOUNCEMENT.configKey, announcement)
        destinationChannel.sendMessage(getLocalized("announcementSet", getLocalized(announcerName), announcement)).queue()
    }

    override fun usageMessage(): String {
        return getLocalized("usage", configManager.getCommandPrefix(destinationGuild))
    }
}