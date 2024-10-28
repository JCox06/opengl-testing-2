package uk.co.jcox.gl;

import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class ImGuiGlfwOpenGLImpl implements AutoCloseable{

    private ImGuiImplGlfw glfwImpl;
    private ImGuiImplGl3 openGLImpl;


    public void init(long glfwWindowHandle) {
        ImGui.createContext();
        ImGui.styleColorsClassic();
        glfwImpl = new ImGuiImplGlfw();
        openGLImpl = new ImGuiImplGl3();

        glfwImpl.init(glfwWindowHandle, true);
        openGLImpl.init();

        ImGui.getIO().setFontGlobalScale(2.0f);
    }


    public void newFrame() {
        openGLImpl.newFrame();
        glfwImpl.newFrame();
        ImGui.newFrame();
    }


    public void renderData() {
        ImGui.render();
        openGLImpl.renderDrawData(ImGui.getDrawData());
    }

    @Override
    public void close() {
        openGLImpl.shutdown();
        glfwImpl.shutdown();
        ImGui.destroyContext();
    }
}
