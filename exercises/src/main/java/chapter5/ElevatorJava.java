package chapter5;

import java.util.Objects;

public class ElevatorJava {
    private final int floor;

    public ElevatorJava(int floor) {
        this.floor = floor;
    }

    public int getFloor() {
        return floor;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        ElevatorJava that = (ElevatorJava) object;
        return floor == that.floor;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(floor);
    }
}
