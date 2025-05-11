package chapter6

import chapter6.ElevatorCommand.CallElevator
import chapter6.ElevatorCommand.GoToFloor
import chapter6.ElevatorEvent.ButtonPressed
import chapter6.ElevatorEvent.ElevatorMoved
import chapter6.ElevatorState.DoorsOpenAtFloor
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class E02_ElevatorEventsTest {

    @Test
    fun `elevator opens the doors at the called floor`() {
        val events = listOf<ElevatorEvent>(ElevatorMoved(0, 3))
        val newEvents = processCommand(events, CallElevator(5))
        expectThat(foldEvents(newEvents)).isEqualTo(DoorsOpenAtFloor(5))
    }

    @Test
    fun `elevator starts traveling to that floor when someone presses a floor button`() {
        val events = listOf<ElevatorEvent>(ElevatorMoved(0, 5))
        val newEvents = processCommand(events, GoToFloor(10))
        expectThat(foldEvents(newEvents)).isEqualTo(DoorsOpenAtFloor(10))
    }

    @Test
    fun `elevator doesn't move if someone call it on the same floor`() {
        val events = listOf(ButtonPressed(10), ElevatorMoved(0, 10))
        val newEvents = processCommand(events, GoToFloor(10))
        expectThat(foldEvents(newEvents)).isEqualTo(DoorsOpenAtFloor(10))
    }

    private fun processCommand(events: List<ElevatorEvent>, command: ElevatorCommand): List<ElevatorEvent> =
        events + handleCommandsEvents(foldEvents(events), command)
}