package net.doemges.kogniswarm.data

import com.appmattus.kotlinfixture.decorator.fake.javafaker.javaFakerStrategy
import com.appmattus.kotlinfixture.kotlinFixture
import com.github.javafaker.Faker
import kotlin.random.Random

class Fixtures {
    companion object {

        private val fakers: List<Faker.() -> String> = listOf(
            { witcher().character() },
            { witcher().monster() },
            { starTrek().character() },
            { gameOfThrones().character() },
            { "${elderScrolls().firstName()} ${elderScrolls().lastName()}" },
            { buffy().characters() },
            { cat().breed() },
            { rickAndMorty().character() }
        )

        fun fixtureWithFaker() = kotlinFixture {
            javaFakerStrategy {
                putProperty("name") {
                    fakers[Random.nextInt(fakers.size)](this)
                            .split(" ")
                            .joinToString("")
                }
            }
        }
    }
}