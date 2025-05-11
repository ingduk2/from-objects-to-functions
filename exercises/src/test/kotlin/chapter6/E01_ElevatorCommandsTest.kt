package chapter6

import chapter6.ElevatorCommand.CallElevator
import chapter6.ElevatorCommand.GoToFloor
import chapter6.ElevatorState.DoorsOpenAtFloor
import chapter6.ElevatorState.TravelingAtFloor
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class E01_ElevatorCommandsTest {

    @Test
    fun `elevator opens the doors at the called floor`() {
        val state = TravelingAtFloor(3)
        val finalState = handleCommand(state, CallElevator(5))
        expectThat(finalState).isEqualTo(DoorsOpenAtFloor(5))
    }

    @Test
    fun `elevator starts traveling to that floor when someone presses a floor button`() {
        val state = DoorsOpenAtFloor(5)
        val finalState = handleCommand(state, GoToFloor(10))
        expectThat(finalState).isEqualTo(TravelingAtFloor(10))
    }

    @Test
    fun `elevator continues traveling to that floor when someone presses a floor button`() {
        val state = TravelingAtFloor(10)
        val finalState = handleCommand(state, GoToFloor(3))
        expectThat(finalState).isEqualTo(TravelingAtFloor(10))
    }

    @Test
    fun `elevator does nothing when doors are already open at a floor and receives a call`() {
        val state = DoorsOpenAtFloor(5)
        val finalState = handleCommand(state, CallElevator(2))
        expectThat(finalState).isEqualTo(DoorsOpenAtFloor(5))
    }
}