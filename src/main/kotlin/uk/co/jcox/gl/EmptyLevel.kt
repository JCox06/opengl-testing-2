package uk.co.jcox.game

import uk.co.jcox.gl.Level
import uk.co.jcox.gl.Renderer
import uk.co.jcox.gl.WindowManager

class EmptyLevel(renderer: Renderer) : Level(renderer) {


    override fun onLevelUpdate(windowManager: WindowManager, deltaTime: Float) {
    }

    override fun onLevelRender() {
    }

    override fun onLevelImGuiDebugRender() {
    }

    override fun onLevelDestroy() {
    }

    override fun getLevelName(): String {
        return "ENGINE/EMPTY_LEVEL"
    }
}