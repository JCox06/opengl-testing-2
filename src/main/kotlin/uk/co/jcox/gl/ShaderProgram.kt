package uk.co.jcox.gl

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.tinylog.Logger

class ShaderProgram (
    val vertSrc: String, val fragSrc: String
) : AutoCloseable {


    private var program: Int = 0

    //Compile and create program
    fun init() {
        val vertexShader = GL30.glCreateShader(GL30.GL_VERTEX_SHADER)
        GL30.glShaderSource(vertexShader, vertSrc)
        GL30.glCompileShader(vertexShader)

        if (! checkShaderCompilation(vertexShader)) {
            Logger.error {"Shader (Vertex) compilation has failed. ${getShaderInfoLog(vertexShader)}"}
        }

        val fragmentShader = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER)
        GL30.glShaderSource(fragmentShader, fragSrc)
        GL30.glCompileShader(fragmentShader)

        if (! checkShaderCompilation(vertexShader)) {
            Logger.error {"Shader (Fragment) compilation has failed. ${getShaderInfoLog(vertexShader)}"}
        }

        Logger.info {"Shaders have compiled successfully. Awaiting linker"}

        this.program = GL30.glCreateProgram()

        GL30.glAttachShader(this.program, vertexShader)
        GL30.glAttachShader(this.program, fragmentShader)
        GL30.glLinkProgram(this.program)

        if (! checkProgramLink()) {
            Logger.error {"Program link failure. ${getProgramInfoLog()}"}
        }

        validateProgram()

        GL30.glDeleteShader(vertexShader)
        GL30.glDeleteShader(fragmentShader)

        Logger.info {"Shader and Program creation successful"}
    }

    private fun checkShaderCompilation(shadType: Int) : Boolean {
        return GL30.glGetShaderi(shadType, GL30.GL_COMPILE_STATUS) == 1
    }

    private fun getShaderInfoLog(shadType: Int) : String {
        return GL30.glGetShaderInfoLog(shadType)
    }

    private fun checkProgramLink() : Boolean {
        return GL30.glGetProgrami(this.program, GL30.GL_LINK_STATUS) == 1
    }

    fun validateProgram() {
        GL30.glValidateProgram(this.program)
        if (GL30.glGetProgrami(this.program, GL30.GL_VALIDATE_STATUS) == 0) {
            Logger.error {"Program validation failed: ${getProgramInfoLog()}"}
        }
    }


    fun bind() {
        GL30.glUseProgram(this.program)
    }


    fun getProgramInfoLog(): String {
        return GL30.glGetProgramInfoLog(this.program)
    }

    fun uniform(name: String, value: Float) {
        GL30.glUniform1f(getUniformLocation(name), value)
    }

    fun uniform(name: String, value: Int) {
        GL30.glUniform1i(getUniformLocation(name), value)
    }

    fun uniform(name: String, value: Vector3f) {
        GL30.glUniform3f(getUniformLocation(name), value.x, value.y, value.z)
    }

    fun uniform(name: String, value: Matrix4f) {
        val location = getUniformLocation(name)
        val buff = BufferUtils.createFloatBuffer(16)
        value[buff]
        GL20.glUniformMatrix4fv(location, false, buff)
    }

    private fun getUniformLocation(name: String): Int {
        val location = GL30.glGetUniformLocation(this.program, name)
        bind()
        return location
    }

    override fun close() {
        GL30.glDeleteProgram(this.program)
    }
}