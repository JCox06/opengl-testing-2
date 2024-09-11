package uk.co.jcox.gl

import imgui.ImGui
import imgui.ImGuiIO
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.*
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.Platform
import org.tinylog.Logger
import kotlin.math.*

fun main() {

    Logger.info {"OS: ${Platform.get()} ${Platform.getArchitecture()}"}

    val windowManager = WindowManager()
    windowManager.init(3, 3)

    val renderer = Renderer()
    renderer.setupRendering()
    renderer.setupDefaultProgram()


    //Imgui init
    ImGui.createContext()
    val imGUiIO = ImGui.getIO()
//    imGUiIO.addConfigFlags(ImGuiConfigFlags.ViewportsEnable)
    val imGuiImpGlfw = ImGuiImplGlfw()
    val imGuiImpGl3 = ImGuiImplGl3()

    imGuiImpGlfw.init(windowManager.windowHandle, true)
    imGuiImpGl3.init()


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


    val colourArray = floatArrayOf(1.0f, 1.0f, 1.0f)

    while (! windowManager.shouldClose()) {

        val windowSize = windowManager.windowSize
        renderer.viewport(windowSize.x, windowSize.y)
        renderer.clear()

        renderer.setWireframe(windowManager.queryKeyPress(GLFW.GLFW_KEY_K))
        renderer.program.uniform("uTint", Vector3f(colourArray[0], colourArray[1], colourArray[2]))
        renderer.program.uniform("ourTexture", 0)

        //CUSTOM RENDER START
        renderer.program.bind()
        GL30.glBindVertexArray(vertexArray)
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0)
        //CUSTOM RENDER END


        imGuiImpGl3.newFrame()
        imGuiImpGlfw.newFrame()
        ImGui.newFrame()

        ImGui.begin("OpenGL Debug Window")
        ImGui.text("Platform: ${Platform.get()} + ${Platform.getArchitecture()}")
        ImGui.text("OpenGL version: ${GL11.glGetString(GL11.GL_VERSION)}")
        ImGui.text("OpenGL Renderer: ${GL11.glGetString(GL11.GL_RENDERER)}")
        ImGui.text("OpenGL Vendor: ${GL11.glGetString(GL11.GL_VENDOR)}")
        ImGui.colorEdit3("GreenTint", colourArray)

        ImGui.end()

        ImGui.render()
        imGuiImpGl3.renderDrawData(ImGui.getDrawData())

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupCurrentContext = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupCurrentContext);
        }


        windowManager.swapBuffers()
        windowManager.pollEvents()

    }

    Logger.info {"Shutting Down!"}

    GL30.glDeleteVertexArrays(vertexArray)
    GL30.glDeleteBuffers(vertexBuffer)
    imGuiImpGl3.shutdown()
    imGuiImpGlfw.shutdown()
    ImGui.destroyContext()
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
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            STBImage.stbi_image_free(data)
        } else {
            Logger.error {"STB Image failed to load texture"}
        }
    }

    return texture
}
