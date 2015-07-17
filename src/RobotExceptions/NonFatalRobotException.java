package RobotExceptions;

public class NonFatalRobotException extends RobotException {
	
	private static final long serialVersionUID = -5667677685065974389L;
	
	public NonFatalRobotException() {
		super();
	}
	
	public NonFatalRobotException(String message) {
		super(message);
	}
}
