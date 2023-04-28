package net.doemges.kogniswarm.data

import com.appmattus.kotlinfixture.decorator.fake.javafaker.javaFakerStrategy
import com.appmattus.kotlinfixture.kotlinFixture
import kotlin.random.Random

class Fixtures {
    companion object{
        fun fixtureWithFaker() = kotlinFixture {
            javaFakerStrategy {
                putProperty("name") {
                    when (Random.nextInt() % 4) {
                        0 -> witcher().character()
                                .split(" ")
                                .joinToString("")

                        1 -> witcher().monster()
                                .split(" ")
                                .joinToString("")

                        2 -> starTrek().character()
                                .split(" ")
                                .joinToString("")

                        else -> gameOfThrones().character()
                                .split(" ")
                                .joinToString("")
                    }
                }
            }
        }
    }
}