package turrets.game.debug;

public class QuitCommands extends Command {
	
	public enum CommandType {
		QUIT,
		EXIT,
		CLOSE
	}
	public QuitCommands(CommandType quit) {
		switch (quit) {
		case QUIT:
			commandName = "quit";
			break;
		case EXIT:
			commandName = "exit";
			break;
		case CLOSE:
			commandName = "close";
			break;
		}
		argumentCount = 1;
		help = "Closes the debug console";
	}
	@Override
	String execute(Object[] arguments) {
		//Window.getEngine().getRenderer().disableScreen("Debug Console");
		return "Quitting";
	}
	
}
