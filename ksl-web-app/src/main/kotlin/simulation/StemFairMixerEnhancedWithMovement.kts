/*
 *     The KSL provides a discrete-event simulation library for the Kotlin programming language.
 *     Copyright (C) 2022  Manuel D. Rossetti, rossetti@uark.edu
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package simulation

import ksl.modeling.elements.EventGenerator
import ksl.modeling.entity.ProcessModel
import ksl.modeling.entity.ResourceCIfc
import ksl.modeling.entity.ResourceWithQ
import ksl.modeling.nhpp.NHPPTimeBtwEventRV
import ksl.modeling.nhpp.PiecewiseConstantRateFunction
import ksl.modeling.spatial.DistancesModel
import ksl.modeling.variable.*
import ksl.simulation.KSLEvent
import ksl.simulation.ModelElement
import ksl.utilities.divideConstant
import ksl.utilities.random.rvariable.*
import ksl.simulation.Model
import ksl.utilities.io.KSL
import ksl.utilities.io.MarkDown
import java.io.StringWriter
import java.io.PrintWriter

fun getControls(): List<String> {
    val m = Model()
    StemFairMixerEnhancedWithMovement(m, "Stem Fair Base Case")
    val controls = m.controls()
    val keyLoop = controls.controlKeys()
    val keys = mutableListOf<String>()
    val i = 0
     for(key in keyLoop) {
        keys.add(key.toString())
    }
    return keys
}

fun runSimulation(): String{
    val m = Model()
    StemFairMixerEnhancedWithMovement(m, "Stem Fair Base Case")
    m.numberOfReplications = 400
    m.simulate()
    //m.print()
    val stringWriter = StringWriter()
    val printWriter = PrintWriter(stringWriter)

    m.simulationReporter.writeHalfWidthSummaryReportAsMarkDown(printWriter, df = MarkDown.D3FORMAT)

    val markdownOutput = stringWriter.toString()
    return markdownOutput
}

class StemFairMixerEnhancedWithMovement(parent: ModelElement, name: String? = null) : ProcessModel(parent, name) {

    var lengthOfMixer = 360.0
        set(value) {
            require(value > 0.0) { "The length of the mixer must be > 0.0" }
            field = value
        }
    var warningTime = 15.0
        set(value) {
            require(value > 0.0) { "The warning limit must be > 0.0" }
            field = value
        }

    val doorClosingTime
        get() = lengthOfMixer - warningTime

    var isClosed: Boolean = false
        private set

    private val myNameTagTimeRV = RandomVariable(this, UniformRV((15.0 / 60.0), (45.0 / 60.0), 2))
    private val myDecideToMix = RandomVariable(this, BernoulliRV(0.4, 3))
    private val myDecideToLeave = RandomVariable(this, BernoulliRV(0.1, 4))
    private val myInteractionTimeRV = RandomVariable(this, TriangularRV(10.0, 15.0, 30.0, 5))
    private val myDecideRecruiter = RandomVariable(this, BernoulliRV(0.5, 6))
    private val myTalkWithJHBunt = RandomVariable(this, ExponentialRV(6.0, 7))
    private val myTalkWithMalMart = RandomVariable(this, ExponentialRV(3.0, 8))

    private val myWalkingSpeedRV = RandomVariable(this, TriangularRV(88.0, 176.0, 264.0, 9))
    private val dm = DistancesModel()
    private val entrance = dm.Location("Entrance")
    private val nameTags = dm.Location("NameTags")
    private val conversationArea = dm.Location("ConversationArea")
    private val recruiting = dm.Location("Recruiting")
    private val exit = dm.Location("Exit")
    init {
        // distance is in feet
        dm.addDistance(entrance, nameTags, 20.0, symmetric = true)
        dm.addDistance(nameTags, conversationArea, 30.0, symmetric = true)
        dm.addDistance(nameTags, recruiting, 80.0, symmetric = true)
        dm.addDistance(nameTags, exit, 140.0, symmetric = true)
        dm.addDistance(conversationArea, recruiting, 50.0, symmetric = true)
        dm.addDistance(conversationArea, exit, 110.0, symmetric = true)
        dm.addDistance(recruiting, exit, 60.0, symmetric = true)
        dm.defaultVelocity = myWalkingSpeedRV
        spatialModel = dm
    }

    private val myOverallSystemTime = Response(this, "OverallSystemTime")
    private val myRecruitingOnlySystemTime = Response(this, "RecruitingOnlySystemTime")

    init {
        myRecruitingOnlySystemTime.attachIndicator({ x -> x <= 30.0 }, "P(Recruiting Only < 30 minutes)")
    }

    private val myMixingStudentSystemTime = Response(this, "MixingStudentSystemTime")
    private val myMixingAndLeavingSystemTime = Response(this, "MixingStudentThatLeavesSystemTime")
    private val myNumInSystem = TWResponse(this, "NumInSystem")
    private val myNumInConversationArea = TWResponse(this, "NumInConversationArea")

    private val myNumAtJHBuntAtClosing = Response(this, "NumAtJHBuntAtClosing")
    private val myNumAtMalWartAtClosing = Response(this, "NumAtMalWartAtClosing")
    private val myNumAtConversationAtClosing = Response(this, "NumAtConversationAtClosing")
    private val myNumInSystemAtClosing = Response(this, "NumInSystemAtClosing")

    private val myJHBuntRecruiters: ResourceWithQ = ResourceWithQ(this, capacity = 3, name = "JHBuntR")
    val JHBuntRecruiters: ResourceCIfc
        get() = myJHBuntRecruiters
    private val myMalWartRecruiters: ResourceWithQ = ResourceWithQ(this, capacity = 2, name = "MalWartR")
    val MalWartRecruiters: ResourceCIfc
        get() = myMalWartRecruiters

    private val myTotalAtRecruiters: AggregateTWResponse = AggregateTWResponse(this, "StudentsAtRecruiters")
    init {
        myTotalAtRecruiters.observe(myJHBuntRecruiters.numBusyUnits)
        myTotalAtRecruiters.observe(myJHBuntRecruiters.waitingQ.numInQ)
        myTotalAtRecruiters.observe(myMalWartRecruiters.numBusyUnits)
        myTotalAtRecruiters.observe(myMalWartRecruiters.waitingQ.numInQ)
    }

    private val myTBArrivals: NHPPTimeBtwEventRV
//    private val myTBArrivals: RVariableIfc

    init {
        // set up the generator
        val durations = doubleArrayOf(
            30.0, 30.0, 30.0, 30.0, 30.0, 30.0,
            30.0, 30.0, 30.0, 30.0, 30.0, 30.0
        )
        val hourlyRates = doubleArrayOf(
            5.0, 10.0, 15.0, 25.0, 40.0, 50.0,
            55.0, 55.0, 60.0, 30.0, 5.0, 5.0
        )
        val ratesPerMinute = hourlyRates.divideConstant(60.0)
        val f = PiecewiseConstantRateFunction(durations, ratesPerMinute)
        myTBArrivals = NHPPTimeBtwEventRV(this, f, streamNum = 1)
//        myTBArrivals = ExponentialRV(2.0, 1)
    }

    private val generator = EventGenerator(this, this::createStudents, myTBArrivals, myTBArrivals)

    private val hourlyResponseSchedule = ResponseSchedule(this, 0.0, name = "Hourly")
    private val peakResponseInterval: ResponseInterval = ResponseInterval(this, 120.0, "PeakPeriod:[150.0,270.0]")

    init {
        hourlyResponseSchedule.scheduleRepeatFlag = false
        hourlyResponseSchedule.addIntervals(0.0, 6, 60.0)
        hourlyResponseSchedule.addResponseToAllIntervals(myJHBuntRecruiters.numBusyUnits)
        hourlyResponseSchedule.addResponseToAllIntervals(myMalWartRecruiters.numBusyUnits)
        hourlyResponseSchedule.addResponseToAllIntervals(myJHBuntRecruiters.waitingQ.timeInQ)
        hourlyResponseSchedule.addResponseToAllIntervals(myMalWartRecruiters.waitingQ.timeInQ)
        hourlyResponseSchedule.addResponseToAllIntervals(myJHBuntRecruiters.timeAvgInstantaneousUtil)
        hourlyResponseSchedule.addResponseToAllIntervals(myMalWartRecruiters.timeAvgInstantaneousUtil)
        peakResponseInterval.startTime = 150.0
        peakResponseInterval.addResponseToInterval(myTotalAtRecruiters)
        peakResponseInterval.addResponseToInterval(myJHBuntRecruiters.timeAvgInstantaneousUtil)
        peakResponseInterval.addResponseToInterval(myMalWartRecruiters.timeAvgInstantaneousUtil)
    }

    private val myEndTime = Response(this, "Mixer Ending Time")
    init{
        myEndTime.attachIndicator({ x -> x > lengthOfMixer }, "Prob(EndTime>$lengthOfMixer)")
    }

    override fun initialize() {
        isClosed = false
        schedule(this::closeMixer, doorClosingTime)
    }

    override fun replicationEnded() {
        myEndTime.value = time
    }

    private fun createStudents(eventGenerator: EventGenerator) {
        val student = Student()
        if (student.isMixer) {
            activate(student.mixingStudentProcess)
        } else {
            activate(student.recruitingOnlyStudentProcess)
        }
    }

    private fun closeMixer(event: KSLEvent<Nothing>) {
        isClosed = true
        // turn off the generator
        generator.turnOffGenerator()
        // collect statistics
        myNumAtJHBuntAtClosing.value = myJHBuntRecruiters.waitingQ.numInQ.value + myJHBuntRecruiters.numBusy
        myNumAtMalWartAtClosing.value = myMalWartRecruiters.waitingQ.numInQ.value + myMalWartRecruiters.numBusy
        myNumAtConversationAtClosing.value = myNumInConversationArea.value
        myNumInSystemAtClosing.value = myNumInSystem.value
    }

    private fun decideRecruiter(): ResourceWithQ {
        // check the equal case first to show no preference
        val j = myJHBuntRecruiters.waitingQ.size + myJHBuntRecruiters.numBusy
        val m = myMalWartRecruiters.waitingQ.size + myMalWartRecruiters.numBusy
        if (j == m ){
            if (myDecideRecruiter.value.toBoolean()) {
                return myJHBuntRecruiters
            } else {
                return myMalWartRecruiters
            }
        } else if (j < m) {
            return myJHBuntRecruiters
        } else  {
            // MalWart must be smaller
            return myMalWartRecruiters
        }
    }

    private inner class Student : Entity() {
        val isMixer = myDecideToMix.value.toBoolean()
        val isLeaver = myDecideToLeave.value.toBoolean()

        val mixingStudentProcess = process {
            entity.currentLocation = entrance
            myNumInSystem.increment()
            moveTo(nameTags)
            // at name tag station
            if (isClosed) {
                // mixture closed during walking
                moveTo(exit)
                departMixer(this@Student)
            } else {
                // get name tags
                delay(myNameTagTimeRV)
                if (isClosed) {
                    // mixture closed during name tag
                    moveTo(exit)
                    departMixer(this@Student)
                } else {
                    // goto the conversation area
                    moveTo(conversationArea)
                    if (isClosed) {
                        // closed during walking, must leave
                        moveTo(exit)
                        departMixer(this@Student)
                    } else {
                        // start the conversation
                        myNumInConversationArea.increment()
                        delay(myInteractionTimeRV)
                        myNumInConversationArea.decrement()
                        if (isClosed) {
                            // closed during conversation, must leave
                            moveTo(exit)
                            departMixer(this@Student)
                        } else {
                            // decide to leave or go to recruiting
                            if (isLeaver) {
                                moveTo(exit)
                                departMixer(this@Student)
                            } else {
                                moveTo(recruiting)
                                if (!isClosed) {
                                    // proceed with recruiting visit
                                    val firstRecruiter = decideRecruiter()
                                    if (firstRecruiter == myJHBuntRecruiters) {
                                        use(myJHBuntRecruiters, delayDuration = myTalkWithJHBunt)
                                        use(myMalWartRecruiters, delayDuration = myTalkWithMalMart)
                                    } else {
                                        use(myMalWartRecruiters, delayDuration = myTalkWithMalMart)
                                        use(myJHBuntRecruiters, delayDuration = myTalkWithJHBunt)
                                    }
                                }
                                // either closed or they visited recruiting
                                moveTo(exit)
                                departMixer(this@Student)
                            }
                        }
                    }
                }
            }
        }

        val recruitingOnlyStudentProcess = process {
            entity.currentLocation = entrance
            myNumInSystem.increment()
            moveTo(nameTags, velocity = myWalkingSpeedRV)
            // at name tag station
            if (isClosed) {
                // mixture closed during walking
                moveTo(exit, velocity = myWalkingSpeedRV)
                departMixer(this@Student)
            } else {
                delay(myNameTagTimeRV)
                if (isClosed) {
                    // mixture closed during name tag
                    moveTo(exit, velocity = myWalkingSpeedRV)
                    departMixer(this@Student)
                } else {
                    // proceed to recruiting
                    moveTo(recruiting, velocity = myWalkingSpeedRV)
                    if (!isClosed) {
                        // proceed with recruiting visit
                        val firstRecruiter = decideRecruiter()
                        if (firstRecruiter == myJHBuntRecruiters) {
                            use(myJHBuntRecruiters, delayDuration = myTalkWithJHBunt)
                            use(myMalWartRecruiters, delayDuration = myTalkWithMalMart)
                        } else {
                            use(myMalWartRecruiters, delayDuration = myTalkWithMalMart)
                            use(myJHBuntRecruiters, delayDuration = myTalkWithJHBunt)
                        }
                    }
                    // either closed or they visited recruiting
                    moveTo(exit, velocity = myWalkingSpeedRV)
                    departMixer(this@Student)
                }
            }
        }

        private fun departMixer(departingStudent: Student) {
            myNumInSystem.decrement()
            val st = time - departingStudent.createTime
            myOverallSystemTime.value = st
            if (isMixer) {
                myMixingStudentSystemTime.value = st
                if (isLeaver) {
                    myMixingAndLeavingSystemTime.value = st
                }
            } else {
                myRecruitingOnlySystemTime.value = st
            }

        }
    }
}