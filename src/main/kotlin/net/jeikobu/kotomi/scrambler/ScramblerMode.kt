package net.jeikobu.kotomi.scrambler

enum class ScramblerMode(val modeName: String) {
    MODE_RANDOM("random"),
    MODE_MEMBER_COUNT("memberCount");

    companion object {
        @JvmStatic
        fun fromName(name: String?): ScramblerMode? {
            if (name != null) {
                for (value in values()) {
                    if (value.modeName.equals(name, ignoreCase = true)) {
                        return value
                    }
                }
            }

            return null
        }
    }
}