package uk.co.jcox.game

import imgui.ImGui
import uk.co.jcox.gl.Level
import uk.co.jcox.gl.Renderer

class TestLevel(renderer: Renderer) : Level(renderer) {


    override fun onLevelUpdate() {
    }

    override fun onLevelRender() {
    }

    override fun onLevelImGuiDebugRender() {

    }

    override fun onLevelDestroy() {
    }

    override fun getLevelName(): String {
        return "GAME/TESTING_LEVEL"
    }
}