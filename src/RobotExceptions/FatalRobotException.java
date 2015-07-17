package RobotExceptions;

public class FatalRobotException extends Exception {

	private static final long serialVersionUID = -8987458656685913882L;
	
	public FatalRobotException() {
		super();
	}
	
	public FatalRobotException(String message) {
		super(message);
	}
}
