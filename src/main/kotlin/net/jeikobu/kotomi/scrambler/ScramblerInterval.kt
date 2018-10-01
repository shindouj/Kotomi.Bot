package net.jeikobu.kotomi.scrambler

import org.pmw.tinylog.Logger

class ScramblerInterval(val type: ScramblerIntervalType, val data: String = "") {
    companion object {
        @JvmStatic
        fun fromString(from: String): ScramblerInterval {
            val elems = from.split(", ")

            val type = ScramblerIntervalType.valueOf(elems[0])
            val data = elems[1]

            return ScramblerInterval(type, data)
        }
    }

    override fun toString(): String {
        return type.name + ", " + data
    }
}

enum class ScramblerIntervalType(val intervalName: String) {
    EVERY_N_SECS("everyNSecs"),
    EVERY_MEMBER_JOIN_EVENT("everyMemberJoinEvent");

    companion object {
        @JvmStatic
        fun fromName(name: String): ScramblerIntervalType {
            for (value in values()) {
                if (value.intervalName.equals(name, ignoreCase = true)) {
                    return value
                }
            }

            throw IllegalArgumentException("No such interval type: $name")
        }
    }
}