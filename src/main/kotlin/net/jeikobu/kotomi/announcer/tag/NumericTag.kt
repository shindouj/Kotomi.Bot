package net.jeikobu.kotomi.announcer.tag

import net.jeikobu.jbase.config.AbstractConfigManager

abstract class NumericTag(private val configManager: AbstractConfigManager) : AnnouncerTag<Int>(configManager) {
    companion object {

        fun new(element: String): NumericTag? {
            for (tag in TagManager.getTags()) {
                if (tag is NumericTag && tag.matches(element)) {
                    return tag
                }
            }
            return null
        }
    }
}