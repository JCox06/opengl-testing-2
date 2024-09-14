package uk.co.jcox.gl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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


    @Override
    public void close() {
        if (this.debugCallback != null) {
            this.debugCallback.close();
        }
        this.program.close();
    }
}
