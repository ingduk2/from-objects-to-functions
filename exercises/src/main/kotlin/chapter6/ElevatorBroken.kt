package chapter6

class ElevatorBroken {
    sealed class ElevatorCommand {
        data class CallElevator(val floor: Int) : ElevatorCommand()
        data class GoToFloor(val floor: Int) : ElevatorCommand()
        object FixElevator : ElevatorCommand()
    }

    sealed class ElevatorState {
        data class DoorsOpenAtFloor(val floor: Int) : ElevatorState()
        data class TravelingAtFloor(val floor: Int) : ElevatorState()
        object OutOfOrder : ElevatorState()
    }

    sealed class ElevatorEvent {
        data class ButtonPressed(val floor: Int) : ElevatorEvent()
        data class ElevatorMoved(val fromFloor: Int, val toFloor: Int) : ElevatorEvent()
        object ElevatorBroken : ElevatorEvent()
        object ElevatorFixed : ElevatorEvent()
    }

    companion object {
        val initial: ElevatorState = ElevatorState.DoorsOpenAtFloor(0)

        fun foldEvents(events: List<ElevatorEvent>): ElevatorState {
            return events.fold(initial) { state, event ->
                when (event) {
                    is ElevatorEvent.ButtonPressed -> ElevatorState.TravelingAtFloor(event.floor)
                    is ElevatorEvent.ElevatorMoved -> ElevatorState.TravelingAtFloor(event.toFloor)
                    ElevatorEvent.ElevatorBroken -> ElevatorState.OutOfOrder
                    ElevatorEvent.ElevatorFixed -> ElevatorState.DoorsOpenAtFloor(0)
                }
            }
        }

        fun handleCommand(state: ElevatorState, command: ElevatorCommand): List<ElevatorEvent> {
            return when (command) {
                is ElevatorCommand.CallElevator -> {
                    when (state) {
                        is ElevatorState.DoorsOpenAtFloor -> emptyList()
                        is ElevatorState.TravelingAtFloor -> listOf(
                            ElevatorEvent.ButtonPressed(command.floor),
                            ElevatorEvent.ElevatorMoved(state.floor, command.floor)
                        )
                        ElevatorState.OutOfOrder -> listOf(ElevatorEvent.ElevatorBroken)
                    }
                }

                is ElevatorCommand.GoToFloor -> {
                    when (state) {
                        is ElevatorState.DoorsOpenAtFloor -> listOf(
                            ElevatorEvent.ButtonPressed(command.floor),
                            ElevatorEvent.ElevatorMoved(state.floor, command.floor)
                        )
                        is ElevatorState.TravelingAtFloor -> emptyList()
                        ElevatorState.OutOfOrder -> listOf(ElevatorEvent.ElevatorBroken)
                    }
                }

                ElevatorCommand.FixElevator -> {
                    if (state == ElevatorState.OutOfOrder) listOf(ElevatorEvent.ElevatorFixed) else emptyList()
                }
            }
        }
    }
}