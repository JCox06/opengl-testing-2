package uk.co.jcox.game

import jdk.incubator.vector.VectorOperators.Test
import uk.co.jcox.gl.Engine
import uk.co.jcox.gl.Level
import uk.co.jcox.gl.LevelCreator
import uk.co.jcox.gl.Renderer

fun main() {
    val engine = Engine()

    engine.init()
    engine.registerLevel {TestLevel(it)}
    engine.start()
}