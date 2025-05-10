package chapter5

import chapter5.DirectionJava.DownJava
import chapter5.DirectionJava.UpJava
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class E02_FoldTest {

    @Test
    fun `fold elevator events`() {
        val values = listOf(Up, Up, Down, Up, Down, Down, Up, Up, Up, Down)

        val tot = values.fold(Elevator(0)) { elevator, direction ->
            when (direction) {
                is Up -> Elevator(elevator.floor + 1)
                is Down -> Elevator(elevator.floor - 1)
            }
        }

        expectThat(tot).isEqualTo(Elevator(2))
    }

    @Test
    fun `java fold elevator events`() {
        val values = listOf(
            UpJava(), UpJava(), DownJava(), UpJava(), DownJava(),
            DownJava(), UpJava(), UpJava(), UpJava(), DownJava()
        )

        val tot = values.fold(ElevatorJava(0)) { elevator, direction ->
            when (direction) {
                is UpJava -> ElevatorJava(elevator.floor + 1)
                is DownJava -> ElevatorJava(elevator.floor - 1)
                else -> elevator
            }
        }

        expectThat(tot).isEqualTo(ElevatorJava(2))
    }
}