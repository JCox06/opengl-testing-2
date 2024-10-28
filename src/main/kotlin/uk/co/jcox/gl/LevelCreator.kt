package uk.co.jcox.gl


fun interface LevelCreator {
    fun get(renderer: Renderer): Level
}