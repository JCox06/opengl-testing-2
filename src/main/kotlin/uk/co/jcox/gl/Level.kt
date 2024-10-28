package uk.co.jcox.gl

abstract class Level (
    protected val renderer: Renderer,
) {

     open fun onLevelUpdate() {

     }
     open fun onLevelRender() {

     }
     open fun onLevelImGuiDebugRender() {

     }
     open fun onLevelDestroy() {

     }

     abstract fun getLevelName(): String
}