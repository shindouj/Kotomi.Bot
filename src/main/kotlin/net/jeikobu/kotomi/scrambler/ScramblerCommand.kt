package net.jeikobu.kotomi.scrambler

import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import sx.blah.discord.handle.obj.IMessage
import sx.blah.discord.handle.obj.IRole
import sx.blah.discord.handle.obj.Permissions
import java.util.*

@Command(name = "scrambleRoles", argsLength = 1, permissions = [Permissions.ADMINISTRATOR])
class ScramblerCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: IMessage) {
        when (args[0]) {
            "enable" -> setEnabled(true)
            "disable" -> setEnabled(false)
            "addRole" -> addRoles()
            "clearRoles" ->  {
                clearRoles()
                if (getEnabled()) {
                    setEnabled(false)
                }
            }
            "setMode" -> setMode()
            "setInterval" -> setInterval()
        }
    }

    fun setEnabled(enabled: Boolean) {
        val operationName = if (enabled) getLocalized("enabled") else getLocalized("disabled")

        if (enabled && !getInterval().isPresent) {
            destinationChannel.sendMessage(getLocalized("intervalFirst"))
            return
        } else if (getEnabled() == enabled) {
            destinationChannel.sendMessage(getLocalized("alreadySwitched", operationName))
        } else {
            guildConfig.setValue(ScramblerKeys.SCRAMBLER_ENABLED, enabled.toString())
            destinationChannel.sendMessage(getLocalized("enableSwitchSuccessful", operationName))
        }
    }

    private fun getEnabled(): Boolean {
        return (guildConfig.getValue(ScramblerKeys.SCRAMBLER_ENABLED) ?: "false").toBoolean()
    }

    private fun getInterval(): Optional<ScramblerInterval> {
        val interval = guildConfig.getValue(ScramblerKeys.SCRAMBLER_INTERVAL)
        return if (interval != null) {
            Optional.of(ScramblerInterval.fromString(interval))
        } else {
            Optional.empty()
        }
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
                destinationChannel.sendMessage(getLocalized("dataInputRequired"))
                return
            }

            if (!intervalType.isDataValid(data)) {
                destinationChannel.sendMessage(getLocalized("dataInvalid", data))
                return
            }

            guildConfig.setValue(ScramblerKeys.SCRAMBLER_INTERVAL, ScramblerInterval(intervalType, data).toString())
            destinationChannel.sendMessage(getLocalized("intervalSuccess", args[1]))
        } catch (e: IllegalArgumentException) {
            destinationChannel.sendMessage(getLocalized("wrongIntervalType", args[1]))
        }
    }

    private fun addRoles() {
        var omittedRoles = ""
        val currentRolesList = getRoles()

        val splitRegex = Regex("(\".*?\")|([^\\s]+)")

        var rolesList: List<IRole> = splitRegex.findAll(args.subList(1, args.size).joinToString (separator = " ") { it }).mapNotNull { x ->
            var role: IRole?
            var elem = x.value

            if (elem.startsWith("\"") && elem.endsWith("\"")) {
                elem = elem.substring(1, elem.length - 1)
            }

            try {
                role = destinationGuild.getRoleByID(elem.toLong())
            } catch (e: Exception) {
                try {
                    role = destinationGuild.getRolesByName(elem).first()
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
        }.toList()

        if (rolesList.isEmpty()) {
            destinationChannel.sendMessage(getLocalized("noRolesFound") + "\n" + getLocalized("rolesAvailable", currentRolesList.joinToString { it.name }))
            return
        } else if (omittedRoles.isNotEmpty()) {
            destinationChannel.sendMessage(getLocalized("omittedRoles", omittedRoles.substringBeforeLast(", ")))
        }

        destinationChannel.sendMessage(getLocalized("rolesAdded", rolesList.joinToString { it.name }))

        rolesList += currentRolesList
        setRoles(rolesList)

        destinationChannel.sendMessage(getLocalized("rolesAvailable", rolesList.joinToString { it.name }))
    }

    private fun getRoles(): List<IRole> {
        val roleList = guildConfig.getValue(ScramblerKeys.SCRAMBLER_ROLES)

        return if (roleList != null && roleList != "") {
            roleList.split(", ").mapNotNull { x ->
                try {
                    destinationGuild.getRoleByID(x.toLong())
                } catch (e: Exception) {
                    destinationChannel.sendMessage(getLocalized("roleDeleted", x))
                    null
                }
            }
        } else {
            emptyList()
        }
    }

    private fun setRoles(rolesList: List<IRole>) {
        val roleStringList: String = rolesList.joinToString { x -> x.stringID }
        guildConfig.setValue(ScramblerKeys.SCRAMBLER_ROLES, roleStringList)
    }

    private fun clearRoles() {
        guildConfig.setValue(ScramblerKeys.SCRAMBLER_ROLES, "")
        destinationChannel.sendMessage(getLocalized("rolesCleared"))
    }

    private fun setMode() {
        try {
            if (args.size > 1) {
                val mode = ScramblerMode.fromName(args[1].toLowerCase())
                guildConfig.setValue(ScramblerKeys.SCRAMBLER_MODE, mode.modeName)
                destinationChannel.sendMessage(getLocalized("modeSetSuccessfully", mode.modeName))
            } else {
                destinationChannel.sendMessage(getLocalized("noSuchMode", "empty"))
            }
        } catch (e: IllegalArgumentException) {
            destinationChannel.sendMessage(getLocalized("noSuchMode", args[1]))
        }
    }

    override fun usageMessage(): String {
        return getLocalized("usage", configManager.getCommandPrefix(destinationGuild))
    }
}