package net.doemges.kogniswarm.core

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isSuccess
import assertk.assertions.key
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class ParameterParserTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    //
    @Test
    fun testParseParameters1() {
        parse("\"digital marxism\" start=0 num=20", "digital marxism", "0", "20")
    }
    @Test
    fun testParseParameters2() {
        parse("\"digital marxism books\" start:0 num:10", "digital marxism books", "0", "10")
    }

    private fun parse(paramString: String, query: String, start: String, num: String) {
        ParameterParser().also { parser ->
            logger.info(
                parser.parseParameters(
                    paramString,
                    listOf("query", "start", "num")
                )
                    .toString()
            )
            assertThat {
                parser.parseParameters(
                    paramString,
                    listOf("query", "start", "num")
                )
            }
                .isSuccess()
                .all {
                    isInstanceOf(Map::class.java)
                    key("query").all {
                        isNotNull()
                        isInstanceOf(String::class.java)
                        isEqualTo(query)
                    }
                    key("start").all {
                        isNotNull()
                        isInstanceOf(String::class.java)
                        isEqualTo(start)
                    }
                    key("num").all {
                        isNotNull()
                        isInstanceOf(String::class.java)
                        isEqualTo(num)
                    }
                }
        }
    }
}