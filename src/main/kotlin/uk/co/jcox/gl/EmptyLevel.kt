package uk.co.jcox.game

import uk.co.jcox.gl.Level
import uk.co.jcox.gl.Renderer

class EmptyLevel(renderer: Renderer) : Level(renderer) {


    override fun onLevelUpdate() {
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