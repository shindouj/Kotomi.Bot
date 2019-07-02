package net.jeikobu.kotomi.scrambler

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.Role
import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import java.util.*

@Command(name = "scrambleRoles", argsLength = 1, permissions = [Permission.ADMINISTRATOR])
class ScramblerCommand(data: CommandData) : AbstractCommand(data) {
    val scramblerConfig = ScramblerConfig(guildConfig)

    override fun run(message: Message) {
        when (args[0]) {
            "enable" -> setEnabled(true)
            "disable" -> setEnabled(false)
            "addRole" -> addRoles()
            "clearRoles" -> {
                clearRoles()
                if (getEnabled()) {
                    setEnabled(false)
                }
            }
            "setMode" -> setMode()
            "setInterval" -> setInterval()
        }
    }

    private fun setEnabled(enabled: Boolean) {
        val operationName = if (enabled) getLocalized("enabled") else getLocalized("disabled")

        if (enabled && getInterval() == null) {
            destinationChannel.sendMessage(getLocalized("intervalFirst")).queue()
            return
        } else if (enabled && scramblerConfig.scramblerMode == null) {
            destinationChannel.sendMessage(getLocalized("modeFirst")).queue()
            return
        } else if (getEnabled() == enabled) {
            destinationChannel.sendMessage(getLocalized("alreadySwitched", operationName)).queue()
        } else {
            scramblerConfig.scramblerEnabled = enabled
            scramblerConfig.scramblerTick = 0
            destinationChannel.sendMessage(getLocalized("enableSwitchSuccessful", operationName)).queue()
        }
    }

    private fun getEnabled(): Boolean {
        return scramblerConfig.scramblerEnabled ?: false
    }

    private fun getInterval(): ScramblerInterval? {
        return scramblerConfig.scramblerInterval
    }

    private fun setInterval() {
        try {
            val intervalType = ScramblerIntervalType.fromName(args[1])

            val data: String = if (args.size > 2) {
                args[2]
            } else {
                ""
            }

            if (data == "" && intervalType.dataRequired) {
                destinationChannel.sendMessage(getLocalized("dataInputRequired")).queue()
                return
            }

            if (!intervalType.isDataValid(data)) {
                destinationChannel.sendMessage(getLocalized("dataInvalid", data)).queue()
                return
            }

            scramblerConfig.scramblerInterval = ScramblerInterval(intervalType, data)
            destinationChannel.sendMessage(getLocalized("intervalSuccess", args[1])).queue()
        } catch (e: IllegalArgumentException) {
            destinationChannel.sendMessage(getLocalized("wrongIntervalType", args[1])).queue()
        }
    }

    private fun addRoles() {
        var omittedRoles = ""
        val currentRolesList = getRoles()

        val splitRegex = Regex("(\".*?\")|([^\\s]+)")

        val rolesList: MutableList<Role> = splitRegex.findAll(
            args.subList(1, args.size).joinToString(separator = " ") { it }).mapNotNull { x ->
            var role: Role?
            var elem = x.value

            if (elem.startsWith("\"") && elem.endsWith("\"")) {
                elem = elem.substring(1, elem.length - 1)
            }

            try {
                role = destinationGuild.getRoleById(elem.toLong())
            } catch (e: Exception) {
                try {
                    role = destinationGuild.getRolesByName(elem, true).first()
                } catch (e: Exception) {
                    omittedRoles += "$elem, "
                    role = null
                }
            }

            if (role == null || currentRolesList.contains(role)) {
                null
            } else {
                role
            }
        }.toList().toMutableList()

        if (rolesList.isEmpty()) {
            destinationChannel.sendMessage(getLocalized("noRolesFound") + "\n" + getLocalized("rolesAvailable",
                                                                                              currentRolesList.joinToString { it.name }))
                    .queue()
            return
        } else if (omittedRoles.isNotEmpty()) {
            destinationChannel.sendMessage(getLocalized("omittedRoles", omittedRoles.substringBeforeLast(", "))).queue()
        }

        destinationChannel.sendMessage(getLocalized("rolesAdded", rolesList.joinToString { it.name })).queue()

        rolesList += currentRolesList
        setRoles(rolesList)

        destinationChannel.sendMessage(getLocalized("rolesAvailable", rolesList.joinToString { it.name })).queue()
    }

    private fun getRoles(): List<Role> {
        return scramblerConfig.scramblerRoles ?: emptyList()
    }

    private fun setRoles(rolesList: List<Role>) {
        scramblerConfig.scramblerRoles = rolesList
    }

    private fun clearRoles() {
        scramblerConfig.scramblerRoles = null
        destinationChannel.sendMessage(getLocalized("rolesCleared")).queue()
    }

    private fun setMode() {
        try {
            if (args.size > 1) {
                val mode = ScramblerMode.fromName(args[1].toLowerCase())
                if (mode != null) {
                    scramblerConfig.scramblerMode = mode
                    destinationChannel.sendMessage(getLocalized("modeSetSuccessfully", mode.modeName)).queue()
                    return
                }
            }
            destinationChannel.sendMessage(getLocalized("noSuchMode", "empty")).queue()
        } catch (e: IllegalArgumentException) {
            destinationChannel.sendMessage(getLocalized("noSuchMode", args[1])).queue()
        }
    }

    override fun usageMessage(): String {
        return getLocalized("usage", configManager.getCommandPrefix(destinationGuild))
    }
}