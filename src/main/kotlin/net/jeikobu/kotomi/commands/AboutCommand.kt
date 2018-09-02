package net.jeikobu.kotomi.commands

import net.jeikobu.jbase.command.AbstractCommand
import net.jeikobu.jbase.command.Command
import net.jeikobu.jbase.command.CommandData
import sx.blah.discord.handle.obj.IMessage

@Command(name = "about", argsLength = 0)
class AboutCommand(data: CommandData) : AbstractCommand(data) {
    override fun run(message: IMessage) {
        destinationChannel.sendMessage(getLocalized("about"))
    }
}