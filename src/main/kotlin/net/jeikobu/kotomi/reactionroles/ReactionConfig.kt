package net.jeikobu.kotomi.reactionroles

import net.dv8tion.jda.core.entities.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception
import javax.sql.DataSource

class ReactionConfig(dataSource: DataSource) {
    private val db by lazy {
        Database.connect(dataSource)
    }

    object ReactionMessage : Table() {
        val messageID = long("messageID").primaryKey(0)
        val setting = enumerationByName("setting", 32, ReactionMessageTypes::class)
    }

    object RoleToReactionMessage : Table() {
        val message = reference("reactionMessage", ReactionMessage.messageID).primaryKey(0)
        val role = long("roleID").primaryKey(1)
        val reactionEmojiID = long("reactionEmojiID")
    }

    fun registerMessage(message: Message, setting: ReactionMessageTypes) {
        transaction(db) {
            create(ReactionMessage)

            ReactionMessage.insertOrUpdate(ReactionMessage.setting) {
                it[ReactionMessage.messageID] = message.idLong
                it[ReactionMessage.setting] = setting
            }
        }
    }

    fun isMessageRegistered(message: Message): Boolean {
        return transaction(db) {
            create(ReactionMessage)
            ReactionMessage.select { ReactionMessage.messageID eq message.idLong }.count() > 0
        }
    }

    fun getRole(message: Message, reaction: MessageReaction, guild: Guild): Role {
        return transaction(db) {
            create(ReactionMessage)
            create(RoleToReactionMessage)

            guild.getRoleById(RoleToReactionMessage.select {
                (RoleToReactionMessage.message eq message.idLong) and (RoleToReactionMessage.reactionEmojiID eq reaction.reactionEmote.idLong)
            }.first()[RoleToReactionMessage.role])
        }
    }

    fun getMode(message: Message): ReactionMessageTypes {
        return transaction(db) {
            create(ReactionMessage)
            ReactionMessage.select{ ReactionMessage.messageID eq message.idLong }.first()[ReactionMessage.setting]
        }
    }

    fun addReactionRole(message: Message, role: Role, emoji: Emote) {
        transaction(db) {
            create(ReactionMessage)
            create(RoleToReactionMessage)

            if (this@ReactionConfig.isMessageRegistered(message)) {
                return@transaction RoleToReactionMessage.insertOrUpdate(RoleToReactionMessage.reactionEmojiID) {
                    it[RoleToReactionMessage.message] = message.idLong
                    it[RoleToReactionMessage.role] = role.idLong
                    it[RoleToReactionMessage.reactionEmojiID] = emoji.idLong
                }
            } else {
                throw ReactionConfigException("Message not registered!")
            }
        }
    }
}

class ReactionConfigException(message: String?) : Exception(message)

fun <T : Table> T.insertOrUpdate(vararg onDuplicateUpdateKeys: Column<*>,
                                 body: T.(InsertStatement<Number>) -> Unit) = InsertOrUpdate<Number>(onDuplicateUpdateKeys, this).apply {
    body(this)
    execute(TransactionManager.current())
}

class InsertOrUpdate<Key : Any>(private val onDuplicateUpdateKeys: Array<out Column<*>>, table: Table,
                                isIgnore: Boolean = false) : InsertStatement<Key>(table, isIgnore) {
    override fun prepareSQL(transaction: Transaction): String {
        val onUpdateSQL = if (onDuplicateUpdateKeys.isNotEmpty()) {
            " ON DUPLICATE KEY UPDATE " + onDuplicateUpdateKeys.joinToString { "${transaction.identity(it)}=VALUES(${transaction.identity(it)})" }
        } else ""
        return super.prepareSQL(transaction) + onUpdateSQL
    }
}