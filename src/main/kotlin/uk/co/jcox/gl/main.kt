package uk.co.jcox.gl

import imgui.ImGui
import imgui.ImGui.render
import imgui.ImGuiIO
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import jdk.incubator.vector.VectorOperators
import org.joml.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.*
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.Platform
import org.tinylog.Logger
import java.lang.Math
import java.util.Vector
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
    imGUiIO.addConfigFlags(ImGuiConfigFlags.DockingEnable)
    ImGui.styleColorsClassic()
    val imGuiImpGlfw = ImGuiImplGlfw()
    val imGuiImpGl3 = ImGuiImplGl3()

    imGuiImpGlfw.init(windowManager.windowHandle, true)
    imGuiImpGl3.init()

    var wireframe = false
    val newCubePos = floatArrayOf(0.0f, 0.0f, 0.0f)
    val cubes = mutableListOf<Vector3f>()

    val cameraPos = Vector3f(0.0f, 0.0f, 3.0f)
    val cameraDir = Vector3f(0.0f, 0.0f, -1.0f)
    val worldUp = Vector3f(0.0f, 1.0f, 0.0f)
    val camSpeed = 1.5f
    val camsense = 0.0005f

    var deltaX = 0.0f
    var deltaY = 0.0f

    var lastX = 0.0f
    var lastY = 0.0f



    //Textrue creation
    val texture = loadTexture("./data/textures/wall.jpg")

    //Creating the OpenGL geometry for rendering (test)
    val vertices = listOf(
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
        0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
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

    val projection = Matrix4f().perspective(Math.toRadians(45.0).toFloat(), 4/3f, 0.1f, 100.0f)

    var deltaTime = 0.0f
    var lastFrameTime = 0.0f

    while (! windowManager.shouldClose()) {

        val currentTime = windowManager.currentTime.toFloat()
        deltaTime = currentTime - lastFrameTime
        lastFrameTime = currentTime

        val windowSize = windowManager.windowSize
        renderer.viewport(windowSize.x, windowSize.y)
        renderer.clear()

//        renderer.setWireframe(windowManager.queryKeyPress(GLFW.GLFW_KEY_K))

        renderer.program.uniform("uTint", Vector3f(colourArray[0], colourArray[1], colourArray[2]))

//        val model = Matrix4f()
//        model.rotate(windowManager.currentTime.toFloat(), Vector3f(1.0f, 1.0f, 1.0f).normalize())


        val view = Matrix4f()

        view.lookAt(cameraPos, cameraPos + cameraDir, worldUp)

        renderer.program.uniform("uView", view)
        renderer.program.uniform("uProjection", projection)


        renderer.program.uniform("ourTexture", 0)

        cubes.forEach {
            val model = Matrix4f().translate(it)
            renderer.program.uniform("uModel", model)
            renderer.program.bind()
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36)
        }


//        //CUSTOM RENDER START
//        renderer.program.bind()
//        GL30.glBindVertexArray(vertexArray)
////        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0)
//        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36)
//        //CUSTOM RENDER END


        imGuiImpGl3.newFrame()
        imGuiImpGlfw.newFrame()
        ImGui.newFrame()

        ImGui.begin("OpenGL Debug Window")
        ImGui.text("Platform: ${Platform.get()} + ${Platform.getArchitecture()}")
        if (ImGui.button("Toggle wireframe ")) {
            wireframe = !wireframe
            renderer.setWireframe(wireframe)
        }

        ImGui.text("Mouse X: ${windowManager.mousePosition.x} Y: ${windowManager.mousePosition.y}")

        ImGui.inputFloat3("Cube Pos", newCubePos)

        if (ImGui.button("Add to Scene")) {
            cubes.add(Vector3f(newCubePos[0], newCubePos[1], newCubePos[2]))
        }

        ImGui.colorEdit3("Colour Array", colourArray)
        ImGui.end()

        ImGui.showMetricsWindow()

        ImGui.render()
        imGuiImpGl3.renderDrawData(ImGui.getDrawData())

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            val backupCurrentContext = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupCurrentContext);
        }


        //Process input from WindowManager
        //Remember to normalize after so you don't move faster when moving diagonally
        val inputMoveResult = Vector3f(0.0f, 0.0f, 0.0f)
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_W)) {
            inputMoveResult += cameraDir
        }
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_S)) {
            inputMoveResult -= cameraDir
        }
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_D)) {
            inputMoveResult += cameraDir.cross(worldUp, Vector3f().normalize())
        }
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_A)) {
            inputMoveResult -= cameraDir.cross(worldUp, Vector3f().normalize())
        }
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_SPACE)) {
            inputMoveResult += worldUp
        }
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            inputMoveResult -= worldUp
        }

        if (!inputMoveResult.equals(0.0f, 0.0f, 0.0f)) {
            inputMoveResult.normalize()
            inputMoveResult.mul(camSpeed * deltaTime)
            cameraPos+= inputMoveResult
        }

        //And now mouse position
        val cursorPos = windowManager.mousePosition
        val cursorX = cursorPos.x.toFloat()
        val cursorY = cursorPos.y.toFloat()
        deltaX = cursorX - lastX
        deltaY = cursorY - lastY


        //Because the camera is made up of an arbitrary coordinate system
        //Just rotate around this system
        if (windowManager.queryButtonPress(GLFW.GLFW_MOUSE_BUTTON_1)) {
            cameraDir.rotateAxis(-deltaX * camsense, worldUp.x, worldUp.y, worldUp.z)
            val posXDir = cameraDir.cross(worldUp, Vector3f())
            //Screen coordinates go diff direction = angle negative
            cameraDir.rotateAxis(-deltaY * camsense, posXDir.x, posXDir.y, posXDir.z)
        }

        lastX = cursorX
        lastY = cursorY

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