package com.jimmy.settings.preferences

interface SafeCloseable : AutoCloseable {
    override fun close()
}