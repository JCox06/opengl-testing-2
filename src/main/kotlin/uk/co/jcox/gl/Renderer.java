package uk.co.jcox.gl;

import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.opengl.*;
import org.lwjgl.system.Callback;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

//Should be responsible for most things OpenGL
public class Renderer implements AutoCloseable {

    private Callback debugCallback;
    private ShaderProgram program;


    public void setupRendering(boolean glDebug) {
        GL.createCapabilities();
        Logger.info("OpenGL renderer imported OpenGL function pointers");
        Logger.info("OpenGL Version: {}", GL11.glGetString(GL11.GL_VERSION));
        Logger.info("OpenGL Vendor: {} ", GL11.glGetString(GL11.GL_VENDOR));
        Logger.info("OpenGL Renderer: {}", GL11.glGetString(GL11.GL_RENDERER));
        Logger.info("OpenGL Shading Language: {}", GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
        if (glDebug){
            this.debugCallback = GLUtil.setupDebugMessageCallback();
        }
        GL11.glClearColor(0.01f, 0.01f, 0.01f, 1.0f);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        Logger.info("OpenGL Renderer ready...");
    }

    public void setClearColour(float x, float y, float z) {
        GL11.glClearColor(x, y, z, 1.0f);
    }

    public void setupDefaultProgram() {
        Logger.info("Using default shader program");
        try {
            String vertexSource = Files.readString(Path.of("./data/glsl/default.vert"));
            String fragmentSource = Files.readString(Path.of("./data/glsl/default.frag"));
            this.program = new ShaderProgram(vertexSource, fragmentSource);
        } catch (IOException e) {
            Logger.error(e);
        }
        this.program.init();
    }

    public ShaderProgram getProgram() {
        return this.program;
    }

    public void viewport(int x, int y) {
        GL11.glViewport(0, 0, x, y);
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void setWireframe(boolean wireframe) {
        if (wireframe) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }
    }

    public GLGeometry createStaticGeometry(float[] vertexData, int[] indexData) {
        int vertexArray = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(vertexArray);

        int vertexBuffer = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vertexBuffer);

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertexData, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL33.glEnableVertexAttribArray(0);

        int indexBuffer = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

        GL33.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexData, GL33.GL_STATIC_DRAW);

        return new GLGeometry(vertexArray, vertexBuffer, indexBuffer, vertexData.length);
    }

    public void destroyGeometry(GLGeometry geometry) {
        GL15.glDeleteBuffers(geometry.getVertexBuffer());
        GL15.glDeleteBuffers(geometry.getIndexBuffer());
        GL33.glDeleteVertexArrays(geometry.getVertexArray());
    }

    public void drawGeometry(GLGeometry geometry) {
        GL11.glDrawElements(GL11.GL_TRIANGLES, geometry.getCount(), GL11.GL_UNSIGNED_INT, 0);
    }

    @Override
    public void close() {
        if (this.debugCallback != null) {
            this.debugCallback.close();
        }
        this.program.close();
    }
}
