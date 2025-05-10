package chapter5;

public sealed class DirectionJava permits DirectionJava.UpJava, DirectionJava.DownJava {
    public static final class UpJava extends DirectionJava {}
    public static final class DownJava extends DirectionJava {}
}
