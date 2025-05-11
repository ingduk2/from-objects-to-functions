package chapter6

import chapter6.ElevatorCommand.*
import chapter6.ElevatorState.DoorsOpenAtFloor
import chapter6.ElevatorState.TravelingAtFloor

sealed class ElevatorCommand {
    data class CallElevator(val floor: Int) : ElevatorCommand()
    data class GoToFloor(val floor: Int) : ElevatorCommand()
}

sealed class ElevatorState {
    data class DoorsOpenAtFloor(val floor: Int) : ElevatorState()
    data class TravelingAtFloor(val floor: Int) : ElevatorState()
}

fun handleCommand(state: ElevatorState, command: ElevatorCommand): ElevatorState {
    return when (command) {
        is CallElevator -> {
            when (state) {
                is DoorsOpenAtFloor-> state
                is TravelingAtFloor -> DoorsOpenAtFloor(command.floor)
            }
        }
        is GoToFloor -> {
            when (state) {
                is DoorsOpenAtFloor -> TravelingAtFloor(command.floor)
                is TravelingAtFloor -> state
            }
        }
    }
}