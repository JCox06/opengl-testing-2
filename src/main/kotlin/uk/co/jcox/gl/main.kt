package uk.co.jcox.gl

import imgui.ImGui
import imgui.flag.ImGuiConfigFlags
import imgui.gl3.ImGuiImplGl3
import imgui.glfw.ImGuiImplGlfw
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.minusAssign
import org.joml.plusAssign
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.Platform
import org.tinylog.Logger

fun main() {

    val windowManager = WindowManager()
    windowManager.init(3, 3)

    val renderer = Renderer()
    renderer.setupRendering(false)
    renderer.setupDefaultProgram()


    //Imgui init
    ImGui.createContext()
    ImGui.styleColorsClassic()
    val imGuiImpGlfw = ImGuiImplGlfw()
    val imGuiImpGl3 = ImGuiImplGl3()

    imGuiImpGlfw.init(windowManager.windowHandle, true)
    imGuiImpGl3.init()

    var wireframe = false
    val newCubePos = floatArrayOf(0.0f, 0.0f, 0.0f)
    val cubes = mutableListOf<Vector3f>()

    val camSpeed = 1.5f
    val camSense = 0.0005f

    var deltaX: Float
    var deltaY: Float

    var lastX = 0.0f
    var lastY = 0.0f


    //Texture creation
    val texture = loadTexture("./data/textures/wall.jpg")


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

    val cube = 10
    val step = 5

    for (i in 1..cube step step) {
        for (j in 1..cube step step) {
            for (k in 1.. cube step step) {
                cubes.add(Vector3f(i.toFloat(), j.toFloat(), k.toFloat()))
            }
        }
    }

    val indices = listOf(
        0, 2, 1,
        2, 3, 1
    )

    val vertexArray = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vertexArray)

    val vertexBuffer = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer)

    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices.toFloatArray(), GL15.GL_STATIC_DRAW)

    val stride: Int = 5 * Float.SIZE_BYTES
    GL30.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, stride, 0)
    GL30.glEnableVertexAttribArray(0)

    GL30.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, stride, 3 * Float.SIZE_BYTES.toLong())
    GL30.glEnableVertexAttribArray(1)


    val indexBuffer = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices.toIntArray(), GL15.GL_STATIC_DRAW)


    val colourArray = floatArrayOf(1.0f, 1.0f, 1.0f)

    var deltaTime: Float
    var lastFrameTime = 0.0f

    val camera = CameraBasic3D()

    while (! windowManager.shouldClose()) {

        val currentTime = windowManager.currentTime.toFloat()
        deltaTime = currentTime - lastFrameTime
        lastFrameTime = currentTime

        val windowSize = windowManager.windowSize
        renderer.viewport(windowSize.x, windowSize.y)
        renderer.clear()

        renderer.program.uniform("uTint", Vector3f(colourArray[0], colourArray[1], colourArray[2]))


        renderer.program.uniform("uCamera", camera.calculateCamMatrix(4/3f))


        renderer.program.uniform("ourTexture", 0)

        cubes.forEach {
            val model = Matrix4f().translate(it)
            renderer.program.uniform("uModel", model)
            renderer.program.bind()
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36)
        }

        imGuiImpGl3.newFrame()
        imGuiImpGlfw.newFrame()
        ImGui.newFrame()

        ImGui.begin("OpenGL Debug Window")
        ImGui.text("Platform: ${Platform.get()} + ${Platform.getArchitecture()}")
        ImGui.text("OpenGL version: ${GL11.glGetString(GL11.GL_VERSION)}")
        ImGui.text("OpenGL Renderer: ${GL11.glGetString(GL11.GL_RENDERER)}")
        ImGui.text("OpenGL Vendor: ${GL11.glGetString(GL11.GL_VENDOR)}")

        ImGui.text("Cam Pos (${camera.position.x}, ${camera.position.y}, ${camera.position.z})")

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
            val backupCurrentContext = GLFW.glfwGetCurrentContext()
            ImGui.updatePlatformWindows()
            ImGui.renderPlatformWindowsDefault()
            GLFW.glfwMakeContextCurrent(backupCurrentContext)
        }

        camera.updateCameraCoordinateSystem()
        val inputMoveResult = Vector3f(0.0f, 0.0f, 0.0f)
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_W)) {
            inputMoveResult += camera.forwardDirection
        }
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_S)) {
            inputMoveResult -= camera.forwardDirection
        }
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_D)) {
            inputMoveResult += camera.sideDirection
        }
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_A)) {
            inputMoveResult -= camera.sideDirection
        }
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_SPACE)) {
            inputMoveResult += camera.up
        }
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            inputMoveResult -= camera.up
        }
        if (!inputMoveResult.equals(0.0f, 0.0f, 0.0f)) {
            inputMoveResult.normalize()
            inputMoveResult.mul(camSpeed * deltaTime)
            camera.position.add(inputMoveResult)
        }

        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_UP)) {
            camera.cameraFov++
        }
        if (windowManager.queryKeyPress(GLFW.GLFW_KEY_DOWN)) {
            camera.cameraFov--
        }
        //And now mouse position
        val cursorPos = windowManager.mousePosition
        val cursorX = cursorPos.x.toFloat()
        val cursorY = cursorPos.y.toFloat()
        deltaX = cursorX - lastX
        deltaY = cursorY - lastY

        //Because the camera is made up of an arbitrary coordinate system
        //Just rotate around this system
        if (windowManager.queryButtonPress(GLFW.GLFW_MOUSE_BUTTON_1) and !ImGui.isAnyItemActive()) {
            camera.forwardDirection.rotateAxis(-deltaX * camSense, camera.up.x, camera.up.y, camera.up.z)
            camera.forwardDirection.rotateAxis(-deltaY * camSense, camera.sideDirection.x, camera.sideDirection.y, camera.sideDirection.z)
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