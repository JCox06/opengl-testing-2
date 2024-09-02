package uk.co.jcox.gl;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;

public class WindowManager implements AutoCloseable {

    private long windowHandle;



    public void init(int maj, int min) {

        if (! GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to start GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VERSION_MAJOR, maj);
        GLFW.glfwWindowHint(GLFW.GLFW_VERSION_MINOR, min);
        this.windowHandle = GLFW.glfwCreateWindow(800, 800, "Window", 0, 0);

        if (this.windowHandle == 0) {
            GLFW.glfwTerminate();
            throw new IllegalStateException("Window must not be null");
        }

        GLFW.glfwMakeContextCurrent(this.windowHandle);
    }

    public boolean queryKeyPress(int glfwKeyCode) {
        return GLFW.glfwGetKey(this.windowHandle, glfwKeyCode) == GLFW.GLFW_PRESS;
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.windowHandle);
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(this.windowHandle);
    }

    public void pollEvents() {
        GLFW.glfwPollEvents();
    }

    public Vector2i getWindowSize() {
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetWindowSize(this.windowHandle, width, height);
        return new Vector2i(width[0], height[0]);
    }

    public double getCurrentTime() {
        return GLFW.glfwGetTime();
    }

    @Override
    public void close() {
        glfwFreeCallbacks(this.windowHandle);
        GLFW.glfwDestroyWindow(this.windowHandle);
        //GLFW.glfwSetErrorCallback(null).free();
    }
}