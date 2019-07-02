package net.jeikobu.kotomi.reactionroles

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import sx.blah.discord.handle.obj.*
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

    fun registerMessage(message: IMessage, setting: ReactionMessageTypes) {
        transaction(db) {
            create(ReactionMessage)

            ReactionMessage.insertOrUpdate(ReactionMessage.setting) {
                it[ReactionMessage.messageID] = message.longID
                it[ReactionMessage.setting] = setting
            }
        }
    }

    fun isMessageRegistered(message: IMessage): Boolean {
        return transaction(db) {
            create(ReactionMessage)
            ReactionMessage.select { ReactionMessage.messageID eq message.longID }.count() > 0
        }
    }

    fun getRole(message: IMessage, reaction: IReaction, guild: IGuild): IRole {
        return transaction(db) {
            create(ReactionMessage)
            create(RoleToReactionMessage)

            guild.getRoleByID(RoleToReactionMessage.select {
                (RoleToReactionMessage.message eq message.longID) and (RoleToReactionMessage.reactionEmojiID eq reaction.emoji.longID)
            }.first()[RoleToReactionMessage.role])
        }
    }

    fun getMode(message: IMessage): ReactionMessageTypes {
        return transaction(db) {
            create(ReactionMessage)
            ReactionMessage.select{ ReactionMessage.messageID eq message.longID }.first()[ReactionMessage.setting]
        }
    }

    fun addReactionRole(message: IMessage, role: IRole, emoji: IEmoji) {
        transaction(db) {
            create(ReactionMessage)
            create(RoleToReactionMessage)

            if (this@ReactionConfig.isMessageRegistered(message)) {
                return@transaction RoleToReactionMessage.insertOrUpdate(RoleToReactionMessage.reactionEmojiID) {
                    it[RoleToReactionMessage.message] = message.longID
                    it[RoleToReactionMessage.role] = role.longID
                    it[RoleToReactionMessage.reactionEmojiID] = emoji.longID
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