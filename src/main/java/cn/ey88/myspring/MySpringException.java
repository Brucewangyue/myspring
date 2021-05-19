package cn.ey88.myspring;

public class MySpringException extends RuntimeException{
    public MySpringException() {
        super();
    }

    public MySpringException(String message) {
        super(message);
    }

    public MySpringException(String message, Throwable cause) {
        super(message, cause);
    }
}
