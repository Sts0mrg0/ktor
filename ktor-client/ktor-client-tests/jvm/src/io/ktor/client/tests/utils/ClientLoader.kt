/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.tests.utils

import io.ktor.client.*
import io.ktor.client.engine.*
import kotlinx.coroutines.debug.*
import kotlinx.coroutines.debug.junit4.*
import org.junit.*
import org.junit.runner.*
import org.junit.runners.*
import java.util.*

/**
 * Helper interface to test client.
 */
@RunWith(Parameterized::class)
actual abstract class ClientLoader {

    @Parameterized.Parameter
    lateinit var engine: HttpClientEngineContainer

    @get:Rule
    open val timeout = CoroutinesTimeout.seconds(60)

    /**
     * Perform test against all clients from dependencies.
     */
    actual fun clientTests(
        skipEngines: List<String>,
        block: suspend TestClientBuilder<HttpClientEngineConfig>.() -> Unit
    ) {
        for (skipEngine in skipEngines) {
            val skipEngineArray = skipEngine.toLowerCase().split(":")

            val (platform, engine) = when (skipEngineArray.size) {
                2 -> skipEngineArray[0] to skipEngineArray[1]
                1 -> "*" to skipEngineArray[0]
                else -> throw IllegalStateException("Wrong skip engine format, expected 'engine' or 'platform:engine'")
            }

            val platformShouldBeSkipped = "*" == platform || OS_NAME == platform
            val engineShouldBeSkipped = "*" == engine || this.engine.toString().toLowerCase() == engine

            if (platformShouldBeSkipped && engineShouldBeSkipped) {
                return
            }
        }
        if (skipEngines.map { it.toLowerCase() }.contains(engine.toString().toLowerCase())) return
        testWithEngine(engine.factory, block)
    }

    actual fun dumpCoroutines() {
        DebugProbes.dumpCoroutines()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun engines(): List<HttpClientEngineContainer> = HttpClientEngineContainer::class.java.let {
            ServiceLoader.load(it, it.classLoader).toList()
        }
    }
}

private val OS_NAME: String
    get() {
        val os = System.getProperty("os.name", "unknown").toLowerCase()
        return when {
            os.contains("win") -> "win"
            os.contains("mac") -> "mac"
            os.contains("nux") -> "unix"
            else -> "unknown"
        }
    }
