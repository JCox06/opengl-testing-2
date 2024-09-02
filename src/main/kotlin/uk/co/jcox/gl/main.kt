package uk.co.jcox.gl

import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.*
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.Platform
import org.tinylog.Logger
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.*
import kotlin.time.times

fun main() {

    Logger.info {"OS: ${Platform.get()}"}

    val windowManager = WindowManager()
    windowManager.init(3, 3)

    val renderer = Renderer()
    renderer.setupRendering()
    renderer.setupDefaultProgram()

    //Textrue creation
    val texture = loadTexture("./data/textures/wall.jpg")

    //Creating the OpenGL geometry for rendering (test)
    val vertices = listOf(
        -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
        -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
        1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
    )

    val indices = listOf(
        0, 2, 1,
        2, 3, 1
    )

    val vertexArray = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vertexArray)

    val vertexBuffer = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer)

    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices.toFloatArray(), GL15.GL_STATIC_DRAW)

    //Linking vertex attributes
    val stride: Int = 5 * Float.SIZE_BYTES
    GL30.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, stride, 0)
    GL30.glEnableVertexAttribArray(0)

    GL30.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, stride, 3 * Float.SIZE_BYTES.toLong())
    GL30.glEnableVertexAttribArray(1)


    val indexBuffer = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices.toIntArray(), GL15.GL_STATIC_DRAW)

    while (! windowManager.shouldClose()) {

        val windowSize = windowManager.windowSize
        renderer.viewport(windowSize.x, windowSize.y)
        renderer.clear()

        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_K)) {
            renderer.setWireframe(true)
        } else {
            renderer.setWireframe(false)
        }

        renderer.program.uniform("uGreenTint", sin(windowManager.currentTime).toFloat())
        renderer.program.uniform("ourTexture", 0)

        //CUSTOM RENDER START
        renderer.program.bind()
        GL30.glBindVertexArray(vertexArray)
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0)
        //CUSTOM RENDER END

        windowManager.swapBuffers()
        windowManager.pollEvents()

    }

    Logger.info {"Shutting Down!"}

    GL30.glDeleteVertexArrays(vertexArray)
    GL30.glDeleteBuffers(vertexBuffer)

    renderer.close()
    windowManager.close()
}

private fun loadTexture(texturePath: String) : Int {
    val texture = GL11.glGenTextures()
    GL15.glActiveTexture(GL15.GL_TEXTURE0)
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
    val stack = MemoryStack.stackPush()
    stack.use {
        val width = stack.mallocInt(1)
        val height = stack.mallocInt(1)
        val nrChannels = stack.mallocInt(1)
        val data = STBImage.stbi_load(texturePath, width, height, nrChannels, 0)
        if (data != null) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width.get(), height.get(), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, data)
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            STBImage.stbi_image_free(data)
        } else {
            Logger.error {"STB Image failed to load texture"}
        }
    }

    return texture
}
