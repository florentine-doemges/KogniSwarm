package net.doemges.kogniswarm.core

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isSuccess
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.apache.camel.AsyncCallback
import org.apache.camel.Exchange
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutionException

@ExperimentalCoroutinesApi
class CoroutineAsyncProcessorTest {

    private val testScope = TestCoroutineScope()

    private val mockExchange = mockk<Exchange>()
    private val mockAsyncCallback = mockk<AsyncCallback>(relaxed = true)

    private val testProcessor = spyk(object : CoroutineAsyncProcessor(testScope) {
        private val logger = LoggerFactory.getLogger(javaClass)
        override suspend fun processSuspend(exchange: Exchange) {
            logger.info("processSuspend")
        }
    })

    @Test
    fun `process should not throw exception when processSuspend is successful`() = testScope.runBlockingTest {
        coEvery { testProcessor.processSuspend(mockExchange) } just Runs

        assertThat { testProcessor.process(mockExchange) }.isSuccess()
        coVerify(exactly = 1) { testProcessor.processSuspend(mockExchange) }
    }

    @Test
    fun `process should set exception when processSuspend throws exception`() = testScope.runBlockingTest {
        val exception = Exception("Test exception")
        coEvery { testProcessor.processSuspend(mockExchange) } throws exception
        every { mockExchange.setException(exception) } just Runs

        assertThat { testProcessor.process(mockExchange) }.isSuccess()
        verify(exactly = 1) { mockExchange.setException(exception) }
    }

    @Test
    fun `processAsync should complete successfully when processSuspend is successful`() = testScope.runBlockingTest {
        coEvery { testProcessor.processSuspend(mockExchange) } just Runs

        val future = testProcessor.processAsync(mockExchange)
        assertThat(future.get()).isEqualTo(mockExchange)
        coVerify(exactly = 1) { testProcessor.processSuspend(mockExchange) }
    }

    @Test
    fun `processAsync should complete exceptionally when processSuspend throws exception`() =
        testScope.runBlockingTest {
            val exception = Exception("Test exception")
            coEvery { testProcessor.processSuspend(mockExchange) } throws exception

            val future = testProcessor.processAsync(mockExchange)
            assertThat { future.get() }.isFailure()
                .isInstanceOf(ExecutionException::class)
            coVerify(exactly = 1) { testProcessor.processSuspend(mockExchange) }
        }

    @Test
    fun `process with callback should not throw exception when processSuspend is successful`() =
        testScope.runBlockingTest {
            coEvery { testProcessor.processSuspend(mockExchange) } just Runs

            testProcessor.process(mockExchange, mockAsyncCallback)
            coVerify(exactly = 1) { testProcessor.processSuspend(mockExchange) }
            verify(exactly = 1) { mockAsyncCallback.done(false) }
        }

    @Test
    fun `process with callback should set exception when processSuspend throws exception`() =
        testScope.runBlockingTest {
            val exception = Exception("Test exception")
            coEvery { testProcessor.processSuspend(mockExchange) } throws exception
            every { mockExchange.setException(exception) } just Runs

            testProcessor.process(mockExchange, mockAsyncCallback)
            coVerify(exactly = 1) { testProcessor.processSuspend(mockExchange) }
            verify(exactly = 1) { mockExchange.setException(exception) }
            verify(exactly = 1) { mockAsyncCallback.done(false) }
        }
}
