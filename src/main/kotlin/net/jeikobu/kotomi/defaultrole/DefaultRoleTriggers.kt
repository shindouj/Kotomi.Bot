package net.jeikobu.kotomi.defaultrole

enum class DefaultRoleTriggers(val commandName: String) {
    JOIN_EVENT("join"),
    FIRST_MESSAGE_EVENT("message");

    companion object {
        fun getByName(commandName: String): DefaultRoleTriggers? {
            return try {
                values().first { it.commandName == commandName }
            } catch (e: NoSuchElementException) {
                null
            }
        }
    }
}