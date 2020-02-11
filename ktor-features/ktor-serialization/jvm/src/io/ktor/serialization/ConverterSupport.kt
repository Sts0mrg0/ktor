/*
 * Copyright 2014-2020 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.serialization

import io.ktor.features.*
import io.ktor.http.*
import kotlinx.serialization.*

/**
 * Register kotlinx.serialization converter into [ContentNegotiation] feature
 * wit
 */
fun ContentNegotiation.Configuration.serialization(
    contentType: ContentType,
    format: BinaryFormat
) {
    register(
        contentType,
        SerializationConverter(format)
    )
}

/**
 * Register kotlinx.serialization converter into [ContentNegotiation] feature
 */
fun ContentNegotiation.Configuration.serialization(
    contentType: ContentType,
    format: StringFormat
) {
    register(
        contentType,
        SerializationConverter(format)
    )
}
