package simulation;

import ksl.utilities.io.StatisticReporter
import ksl.utilities.random.rvariable.NormalRV
import ksl.utilities.statistic.Histogram
import ksl.utilities.statistic.Statistic
import ksl.simulation.Model
import ksl.utilities.io.KSL
import ksl.utilities.io.MarkDown


fun main() {
    val model = Model("Pallet Model MCB")
    // add the model element to the main model
    val palletWorkCenter = PalletWorkCenter(model, name ="PWC")
    // println("Original value of property:")
    // println("num workers = ${palletWorkCenter.numWorkers}")
    // println()
    val controls =  model.controls()
    print("-------------------------------------------------------------------------------------------------")
    print("-------------------------------------------------------------------------------------------------")
    print("-------------------------------------------------------------------------------------------------")
    print("-------------------------------------------------------------------------------------------------")
    print("-------------------------------------------------------------------------------------------------")
    println("Control keys:")
    for (controlKey in controls.controlKeys()) {
        println(controlKey)
        println("------------------------------------------------------------------------------------------")
    }
    // find the control with the desired key
    val control = controls.control("PWC.numWorkers")!!
    // set the value of the control
    control.value = 3.0
    val controlss = controls.control("NumBusyWorkers.initialValue")!!
    controlss.value = 2.0
    val controller = controls.asMap()
    println(controller)
    println("Current value of property:")
    println("num workers = ${palletWorkCenter.numWorkers}")
}