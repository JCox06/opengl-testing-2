package uk.co.jcox.game

import imgui.ImGui
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.minus
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL15
import uk.co.jcox.gl.CameraBasic3D
import uk.co.jcox.gl.GLGeometry
import uk.co.jcox.gl.Level
import uk.co.jcox.gl.Renderer
import uk.co.jcox.gl.WindowManager

class TestLevel(renderer: Renderer) : Level(renderer) {

    private val vertices = listOf(
        1.0f,  1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f,
        -1.0f,  1.0f, 0.0f
    )

    private val indices = listOf(
        0, 1, 3,
        1, 2, 3
    )

    private val cameraBasic3D: CameraBasic3D = CameraBasic3D(Vector3f(0.0f, 0.0f, 1.0f), Vector3f(0.0f, 0.0f, -1.0f), Vector3f(0.0f, 1.0f, 0.0f))
    private val square: GLGeometry


    init {
        renderer.setClearColour(1.0f, 1.0f, 1.0f)
        square = renderer.createStaticGeometry(vertices.toFloatArray(), indices.toIntArray())
    }

    override fun onLevelUpdate(windowManager: WindowManager, deltaTime: Float) {
        cameraBasic3D.updateCameraCoordinateSystem()
        Utils.camera3DGLFWController(cameraBasic3D, windowManager, deltaTime, 0.75f, 0.0005f)
    }

    override fun onLevelRender() {
        renderer.program.bind()
        renderer.program.uniform("uModel", Matrix4f())
        renderer.program.uniform("uCamera", cameraBasic3D.calculateCamMatrix(4f/ 3))

        renderer.drawGeometry(square)
    }

    override fun onLevelImGuiDebugRender() {

    }

    override fun onLevelDestroy() {
        renderer.destroyGeometry(square)
        renderer.setClearColour(0.0f, 1.0f, 0.0f)
    }

    override fun getLevelName(): String {
        return "GAME/TESTING_LEVEL"
    }
}