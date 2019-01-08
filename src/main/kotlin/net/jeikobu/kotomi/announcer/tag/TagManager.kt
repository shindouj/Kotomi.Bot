package net.jeikobu.kotomi.announcer.tag

import sx.blah.discord.handle.obj.IGuild
import sx.blah.discord.handle.obj.IUser

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

        fun processMessage(message: String, user: IUser, guild: IGuild): String {
            var messageCopy = message

            for (tag in tagList) {
                messageCopy = tag.replaceData(messageCopy, user, guild)
            }

            return messageCopy
        }

        fun initializeAnnouncement(announcement: String, guild: IGuild) {
            for (tag in tagList) {
                tag.initialize(announcement, guild)
            }
        }
    }
}