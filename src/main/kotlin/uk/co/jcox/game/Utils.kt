package uk.co.jcox.game

import imgui.ImGui
import org.joml.Vector3f
import org.joml.minusAssign
import org.joml.plusAssign
import org.lwjgl.glfw.GLFW
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import uk.co.jcox.gl.CameraBasic3D
import uk.co.jcox.gl.TextureData
import uk.co.jcox.gl.WindowManager
import java.io.File

object Utils {

    //todo So so ugly but its easy to do and works for now
    fun camera3DGLFWController(camera: CameraBasic3D, windowManager: WindowManager, deltaTime: Float, camSpeed: Float, camSense: Float) {

        camera.updateCameraCoordinateSystem()

        //Movement across arbitrary coordinate system
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


        //Rot across arbitrary coordinate system
        val deltaX = windowManager.deltaCursorPos.x.toFloat();
        val deltaY = windowManager.deltaCursorPos.y.toFloat();

        //Because the camera is made up of an arbitrary coordinate system
        //Just rotate around this system
        if (windowManager.queryButtonPress(GLFW.GLFW_MOUSE_BUTTON_1) and !ImGui.isAnyItemActive()) {
            camera.forwardDirection.rotateAxis(-deltaX * camSense, camera.up.x, camera.up.y, camera.up.z)
            camera.forwardDirection.rotateAxis(-deltaY * camSense, camera.sideDirection.x, camera.sideDirection.y, camera.sideDirection.z)
        }
    }

    fun loadTexture(textureLocation: File): TextureData {
        val stringloc = textureLocation.absoluteFile.toString()
        val stack = MemoryStack.stackPush()
        stack.use {
            val width = stack.mallocInt(1)
            val height = stack.mallocInt(1)
            val nrChannels = stack.mallocInt(1)
            val data = STBImage.stbi_load(stringloc, width, height, nrChannels, 4)

            if (data == null) {
                throw RuntimeException()
            }

            return TextureData(width.get(), height.get(), nrChannels.get(), data)
        }
    }
}