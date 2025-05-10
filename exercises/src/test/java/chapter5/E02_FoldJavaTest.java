package chapter5;

import chapter5.DirectionJava.DownJava;
import chapter5.DirectionJava.UpJava;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class E02_FoldJavaTest {

    @Test
    void javaFoldElevatorEvents() {
        List<DirectionJava> values = List.of(
                new UpJava(), new UpJava(), new DownJava(), new UpJava(), new DownJava(),
                new DownJava(), new UpJava(), new UpJava(), new UpJava(), new DownJava()
        );

        ElevatorJava result = values.stream().reduce(
                new ElevatorJava(0), // identity
                (elevator, direction) -> {
                    if (direction instanceof UpJava) {
                        return new ElevatorJava(elevator.getFloor() + 1);
                    } else if (direction instanceof DownJava) {
                        return new ElevatorJava(elevator.getFloor() - 1);
                    } else {
                        return elevator;
                    }
                },
                (e1, e2) -> e2 // combiner, 병렬 스트림 아니면 의미 없음
        );

        assertEquals(new ElevatorJava(2), result);
    }

    @Test
    void javaFoldElevatorEventsPatternMatching() {
        List<DirectionJava> values = List.of(
                new UpJava(), new UpJava(), new DownJava(), new UpJava(), new DownJava(),
                new DownJava(), new UpJava(), new UpJava(), new UpJava(), new DownJava()
        );

        ElevatorJava result = values.stream().reduce(
                new ElevatorJava(0), // identity
                (elevator, direction) ->
                        switch (direction) {
                            case UpJava up -> new ElevatorJava(elevator.getFloor() + 1);
                            case DownJava down -> new ElevatorJava(elevator.getFloor() - 1);
                            default -> elevator;
                        },
                (e1, e2) -> e2 // combiner, 병렬 스트림 아니면 의미 없음
        );

        assertEquals(new ElevatorJava(2), result);
    }
}
