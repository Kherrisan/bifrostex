package cn.kherrisan.bifrostex_engine.telegram

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.Privacy

@Component
class TelegramBot(
        @Value("\${bifrostex.telegram.bot.token}")
        private val token: String,

        @Value("\${bifrostex.telegram.bot.name}")
        private val name: String,

        @Value("\${bifrostex.telegram.target}")
        private val target: Long,

        @Value("\${bifrostex.telegram.bot.creator}")
        private val creatorId: Int
) : AbilityBot(token, name) {

    private val logger = LogManager.getLogger()

    override fun creatorId(): Int = creatorId

    fun sendMessage(text: String) {
        logger.debug("Send telegram message: $text")
        silent.send(text, target)
    }

    fun sayHello(): Ability = Ability.builder()
            .name("hello")
            .info("say hello world~")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action {
                silent.send("Hello World!", it.chatId())
            }
            .build()
}