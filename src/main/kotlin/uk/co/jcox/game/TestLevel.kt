package uk.co.jcox.game

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import uk.co.jcox.gl.*
import java.io.File

class TestLevel(renderer: Renderer) : Level(renderer) {

    private val vertices = listOf(
        //Plane 1
        1.0f,  1.0f, -1.0f, 1.0f, 1.0f,//0
        1.0f, -1.0f, -1.0f, 1.0f, 0.0f, //1
        -1.0f, -1.0f, -1.0f, 0.0f, 0.0f, //2
        -1.0f,  1.0f, -1.0f, 0.0f, 1.0f, //3

        //Plane 2
        1.0f,  1.0f, 1.0f, 1.0f, 1.0f, //4
        1.0f, -1.0f, 1.0f, 1.0f, 0.0f, //5
        -1.0f, -1.0f, 1.0f, 0.0f, 0.0f,//6
        -1.0f,  1.0f, 1.0f, 0.0f, 1.0f, //7

        //Extra vertices for texture coordinates
        //Plane 1/2 Edtis
        1.0f,  1.0f, -1.0f, 0.0f, 1.0f, //0-8
        1.0f, -1.0f, -1.0f, 0.0f, 0.0f, //1-9
        -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, //2-10
        -1.0f,  1.0f, -1.0f, 1.0f, 1.0f, //3-11

    )

    private val indices = listOf(
        //Face 1
        0, 1, 3,
        1, 2, 3,

        //Face 2
        4, 7,5,
        5, 7, 6,

        //Face 3
        4, 5, 8,
        5, 9, 8,

        //Face 4
        7, 11, 6,
        6, 11, 10,

        //Face 5
        8, 3, 7,
        8, 7, 4,

        //Face 6
        1, 6, 2,
        1, 5, 6,
    )

    private val cameraBasic3D: CameraBasic3D = CameraBasic3D(Vector3f(0.0f, 0.0f, 1.0f), Vector3f(0.0f, 0.0f, -1.0f), Vector3f(0.0f, 1.0f, 0.0f))
    private val GLSquare: GLGeometry
    private val GLTexture: Int


    init {
        renderer.setClearColour(0.1f,0.1f, 0.1f)
        GLSquare = renderer.createStaticGeometry(vertices.toFloatArray(), indices.toIntArray())

        //Load texture and then create texture object
        val textureData = Utils.loadTexture(File("data/textures/mountain.jpg"))

        GLTexture = GL11.glGenTextures()
        GL15.glActiveTexture(GL15.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, GLTexture)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, textureData.width, textureData.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, textureData.data)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
        STBImage.stbi_image_free(textureData.data)
    }

    override fun onLevelUpdate(windowManager: WindowManager, deltaTime: Float) {
        cameraBasic3D.updateCameraCoordinateSystem()
        Utils.camera3DGLFWController(cameraBasic3D, windowManager, deltaTime, 1f, 0.0005f)
    }

    override fun onLevelRender() {
        renderer.program.bind()
        renderer.program.uniform("uModel", Matrix4f())
        renderer.program.uniform("uCamera", cameraBasic3D.calculateCamMatrix(4f/ 3))

        renderer.drawGeometry(GLSquare)
    }

    override fun onLevelImGuiDebugRender() {

    }

    override fun onLevelDestroy() {
        renderer.destroyGeometry(GLSquare)
        renderer.setClearColour(0.0f, 1.0f, 0.0f)
    }

    override fun getLevelName(): String {
        return "GAME/TESTING_LEVEL"
    }
}