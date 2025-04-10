package simulation

import ksl.simulation.Model
import ksl.utilities.io.KSL
import ksl.utilities.io.MarkDown

fun main() {
    val m = Model()
    StemFairMixerEnhancedWithMovement(m, "Stem Fair Base Case")
    m.numberOfReplications = 400
    m.simulate()
    m.print()
    val r = m.simulationReporter
    val controls = m.controls()
    val map = controls.asMap()
    println("This is the map: " + map)
    println("This is the key: ")
    val keyLoop = controls.controlKeys()
    for(key in keyLoop) {
        println(key)
    }
    r.writeHalfWidthSummaryReportAsMarkDown(KSL.out, df = MarkDown.D3FORMAT)
}





