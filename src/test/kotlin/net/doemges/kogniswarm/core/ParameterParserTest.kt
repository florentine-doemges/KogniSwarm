package net.doemges.kogniswarm.core

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isSuccess
import assertk.assertions.key
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class ParameterParserTest {
    private val logger = LoggerFactory.getLogger(javaClass)

    //
    @Test
    fun testParseParameters1() {
        parseAndCheckResult(
            paramString = "\"digital marxism\" start=0 num=20",
            args = mapOf("query" to "digital marxism", "start" to "0", "num" to "20")
        )
    }

    @Test
    fun testParseParameters2() {
        parseAndCheckResult(
            paramString = "\"digital marxism books\" start:0 num:10",
            args = mapOf("query" to "digital marxism books", "start" to "0", "num" to "10")
        )
    }

    @Test
    fun testParseParameter3() {
        parseAndCheckResult(
            paramString = "\"digital marxism theory\" start:10 num=20",
            args = mapOf("query" to "digital marxism theory", "start" to "10", "num" to "20")
        )
    }

    @Test
    fun testParseParameter4() {
        parseAndCheckResult(
            paramString = " url:https://en.wikipedia.org/wiki/Digital_Marxism, contentType:text",
            args = mapOf("url" to "https://en.wikipedia.org/wiki/Digital_Marxism", "contentType" to "text")
        )
    }

    @Test
    fun testParseParameter5() {
        parseAndCheckResult(
            paramString = "url:'https://en.wikipedia.org/wiki/Digital_Marxism' contentType:text",
            args = mapOf("url" to "https://en.wikipedia.org/wiki/Digital_Marxism", "contentType" to "text")
        )
    }

    @Test
    fun testParseParameter6() {
        parseAndCheckResult(
            paramString = "args: [url: 'https://www.google.com/search?q=digital+marxism&oq=digital+marxism&aqs=chrome.0.35i39l2j0j46j69i60l4.7177j0j7&sourceid=chrome&ie=UTF-8']",
            args = mapOf("url" to "https://www.google.com/search?q=digital+marxism&oq=digital+marxism&aqs=chrome.0.35i39l2j0j46j69i60l4.7177j0j7&sourceid=chrome&ie=UTF-8")
        )
    }

    //

    @Test
    fun testParseParameter7() {
        parseAndCheckResult(
            paramString = "url: 'https://en.wikipedia.org/wiki/Digital_Marxism', contentType: 'text', selenium: false.",
            args = mapOf(
                "url" to "https://en.wikipedia.org/wiki/Digital_Marxism",
                "contentType" to "text",
                "selenium" to "false"
            )
        )
    }

    private fun parseAndCheckResult(
        paramString: String,
        args: Map<String, String>
    ) {

        ParameterParser(jacksonObjectMapper()).also { parser ->
            logger.info(
                parser.parseParameters(
                    paramString,
                    args.keys.toList()
                )
                    .toString()
            )
            assertThat {
                parser.parseParameters(
                    paramString,
                    args.keys.toList()
                )
            }
                .isSuccess()
                .all {
                    isInstanceOf(Map::class.java)
                    args.forEach { (key, value) ->
                        key(key).all {
                            isNotNull()
                            isInstanceOf(String::class.java)
                            isEqualTo(value)
                        }
                    }
                }
        }
    }
}