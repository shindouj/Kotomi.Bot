package net.jeikobu.kotomi.announcer.tag

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.User

class TagManager {
    companion object {
        private val tagList: MutableList<AnnouncerTag<out Any>> = mutableListOf()

        fun registerTag(tag: AnnouncerTag<out Any>) {
            tagList.add(tag)
        }

        fun registerAll(tags: List<AnnouncerTag<out Any>>) {
            tagList.addAll(tags)
        }

        fun getTags(): List<AnnouncerTag<out Any>> {
            return tagList
        }

        fun processMessage(message: String, user: User, guild: Guild): String {
            var messageCopy = message

            for (tag in tagList) {
                messageCopy = tag.replaceData(messageCopy, user, guild)
            }

            return messageCopy
        }

        fun initializeAnnouncement(announcement: String, guild: Guild) {
            for (tag in tagList) {
                tag.initialize(announcement, guild)
            }
        }
    }
}