package uk.co.jcox.gl

import imgui.ImGui
import imgui.type.ImInt
import org.lwjgl.opengl.GL11
import org.lwjgl.system.Platform
import org.tinylog.Logger
import uk.co.jcox.game.EmptyLevel

class Engine {
    private val windowManager: WindowManager = WindowManager()
    private val renderer: Renderer = Renderer()
    private var isRunning: Boolean = false

    private val levelList: MutableList<LevelCreator> = mutableListOf()
    private var activeLevel: Level = EmptyLevel(renderer)

    private val imGuiImp: ImGuiGlfwOpenGLImpl = ImGuiGlfwOpenGLImpl()

    //ImGui Primitive Wrapper
    val levelSelector: ImInt = ImInt()

    fun init() {
        windowManager.init(3, 3)
        renderer.setupRendering(false)
        renderer.setupDefaultProgram()

    }

    fun start() {
        imGuiImp.init(windowManager.windowHandle)

        isRunning = true
        loop()
        end()
    }

    private fun loop() {
        while (isRunning) {
            gameRender()
            imGuiRender()
            update()

            renderBuffer()
            if (windowManager.shouldClose()) {
                isRunning = false
            }
        }
    }

    private fun end() {
        imGuiImp.close()
        renderer.close()
        windowManager.close()
    }

    private fun renderBuffer() {
        windowManager.swapBuffers()
        windowManager.pollEvents()
    }

    private fun gameRender() {
        val windowSize = windowManager.windowSize
        renderer.viewport( windowSize.x, windowSize.y)
        renderer.clear()

        activeLevel.onLevelRender()
    }

    private fun imGuiRender() {
        imGuiImp.newFrame()
        engineUiRender()
        activeLevel.onLevelImGuiDebugRender()
        imGuiImp.renderData()
    }

    private fun engineUiRender() {
        ImGui.begin("===ENGINE CORELIB UI===")

        ImGui.textColored(1.0f, 1.0f, 0.0f, 1.0f, "OpenGL Renderer Info")
        ImGui.text("Platform: ${Platform.get()} ${Platform.getArchitecture()}")
        ImGui.text("OpenGL version: ${GL11.glGetString(GL11.GL_VERSION)}")
        ImGui.text("OpenGL Renderer: ${GL11.glGetString(GL11.GL_RENDERER)}")
        ImGui.text("OpenGL Vendor: ${GL11.glGetString(GL11.GL_VENDOR)}")

        ImGui.newLine()
        ImGui.separator()
        ImGui.textColored(1.0f, 1.0f, 0.0f, 1.0f, "Engine Corelib Info")
        ImGui.text("Information about the currently loaded level:")
        ImGui.text("Level Name: ${activeLevel.getLevelName()}")
        ImGui.text("Level ID: -1")
        ImGui.inputInt("Type Level ID to load", levelSelector)
        if (ImGui.button("Load level")) {
            activeLevel.onLevelDestroy()
            activeLevel = levelList[levelSelector.get()].get(renderer)
        }

        ImGui.newLine()
        ImGui.separator()

        ImGui.end()
    }

    private fun update() {
        activeLevel.onLevelUpdate()
    }

    fun registerLevel(levelCreator: LevelCreator) {
        levelList.add(levelCreator)
        Logger.info {"Successfully added new level"}
    }
}