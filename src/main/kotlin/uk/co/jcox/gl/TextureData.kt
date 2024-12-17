package uk.co.jcox.gl

import java.nio.ByteBuffer

data class TextureData(
    val width: Int,
    val height: Int,
    val channels: Int,
    val data: ByteBuffer
)
