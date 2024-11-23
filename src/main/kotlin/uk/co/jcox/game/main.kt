package uk.co.jcox.game

import jdk.incubator.vector.VectorOperators.Test
import uk.co.jcox.gl.*

fun main() {
    val engine = Engine()

    engine.init()
    engine.registerLevel {TestLevel(it)}
    engine.start()
}