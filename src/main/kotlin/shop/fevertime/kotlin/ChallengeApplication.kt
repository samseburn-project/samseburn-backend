package shop.fevertime.kotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class ChallengeApplication

fun main(args: Array<String>) {
    runApplication<ChallengeApplication>(*args)
}
