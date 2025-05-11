package chapter6

import chapter6.ElevatorCommand.CallElevator
import chapter6.ElevatorCommand.GoToFloor
import chapter6.ElevatorEvent.ButtonPressed
import chapter6.ElevatorEvent.ElevatorMoved
import chapter6.ElevatorState.DoorsOpenAtFloor
import chapter6.ElevatorState.TravelingAtFloor

sealed class ElevatorEvent {
    data class ButtonPressed(val floor: Int) : ElevatorEvent()
    data class ElevatorMoved(val fromFloor: Int, val toFloor: Int) : ElevatorEvent()
}

fun foldEvents(events: List<ElevatorEvent>): ElevatorState =
    events.fold(DoorsOpenAtFloor(0) as ElevatorState) { state, event ->
        when (event) {
            is ButtonPressed ->
                if (state != DoorsOpenAtFloor(event.floor))
                    TravelingAtFloor(event.floor)
                else
                    state

            is ElevatorMoved -> DoorsOpenAtFloor(event.toFloor)
        }
    }

fun handleCommandsEvents(state: ElevatorState, command: ElevatorCommand): List<ElevatorEvent> {
    return when (command) {
        is CallElevator -> {
            when (state) {
                is DoorsOpenAtFloor -> listOf(ButtonPressed(command.floor), ElevatorMoved(state.floor, command.floor))
                is TravelingAtFloor -> listOf(ElevatorMoved(state.floor, command.floor))
            }
        }
        is GoToFloor -> {
            when (state) {
                is DoorsOpenAtFloor -> listOf(ButtonPressed(command.floor), ElevatorMoved(state.floor, command.floor))
                is TravelingAtFloor -> emptyList()
            }
        }
    }
}