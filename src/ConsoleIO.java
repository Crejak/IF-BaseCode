import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ConsoleIO {
	Set<Command> commands = new HashSet<Command>();
	boolean running = true;
	
	public ConsoleIO() {
	}
	
	public void addCommand(String cmd, Type[] args, Action action) {
		commands.add(new Command(cmd, args, action));
	}
	
	public void run() {
		Scanner scan = new Scanner(System.in);
		this.running = true;
		while (this.running) {
			System.out.print("$ ");
			interpretCommand(scan.nextLine());
		}
		scan.close();
	}
	
	public void close() {
		this.running = false;
	}
	
	public void interpretCommand(String input) {
		interpretCommand(input.split(" "));
	}
	
	public void interpretCommand(String[] lits) {
		if (lits.length > 0) {
			String name = lits[0];
			int argsCount = lits.length - 1;
			String[] litArgs = new String[lits.length - 1];
			for (int i = 1; i < lits.length; ++i) {
				litArgs[i-1] = lits[i];
			}
			boolean foundName = false;
			boolean foundArgs = false;
			for (Command cmd : commands) {
				if (cmd.getName().equals(name)) {
					foundName = true;
					if (cmd.argsNumber() == argsCount) {
						try {
							Arg[] matchArgs = toArgs(litArgs, cmd.getArgs());
							foundArgs = true;
							cmd.call(matchArgs);
						} catch (ArgTypeException e) {
							continue;
						}
					}
				}
			}
			if (!foundName) {
				System.out.println("\"" + name +"\" n'est pas une commande enregistrée.");
			} else if (!foundArgs) {
				System.out.println("Aucune commande \"" + name + "\" ne prend ces arguments.");
			}
		}
	}
	
	private Arg[] toArgs(String[] litArgs, Type[] types) throws ArgTypeException {
		Arg[] args = new Arg[litArgs.length];
		for (int i = 0; i < litArgs.length; ++i) {
			if (types[i] == Type.STRING) {
				args[i] = new Arg(Type.STRING, litArgs[i]);
			} else if (types[i] == Type.INT) {
				try {
					int val = Integer.parseInt(litArgs[i]);
					args[i] = new Arg(Type.INT, val);
				} catch (NumberFormatException e) {
					throw new ArgTypeException(Type.INT, Type.STRING, i);
				}
			} else if (types[i] == Type.FLOAT) {
				try {
					float val = Float.parseFloat(litArgs[i]);
					args[i] = new Arg(Type.FLOAT, val);
				} catch (NumberFormatException e) {
					throw new ArgTypeException(Type.FLOAT, Type.STRING, i);
				}
			}
		}
		return args;
	}
	
	public void printCommands() {
		for (Command cmd : this.commands) {
			System.out.print("[\"" + cmd.getName() + "\"");
			if (cmd.argsNumber() != 0) {
				for (Type type : cmd.getArgs()) {
					System.out.print("; " + type);
				}
			}
			System.out.println("]");
		}
	}
}

class Command {
	String cmd;
	Type[] args;
	Action action;
	
	public Command(String cmd, Type[] args, Action action) {
		this.cmd = cmd;
		this.args = args;
		this.action = action;
	}
	
	public void call(Arg[] args) throws ArgTypeException {
		for (int i = 0; i < args.length; ++i) {
			if (args[i].type != this.args[i]) {
				throw new ArgTypeException(this.args[i], args[i].type, i);
			}
		}
		// C'est bon, tous les arguments ont le bon type
		action.onAction(args);
	}
	
	public String getName() {
		return this.cmd;
	}
	
	public Type[] getArgs() {
		return this.args;
	}
	
	public int argsNumber() {
		if (args == null) {
			return 0;
		}
		return this.args.length;
	}
	
	public String toString() {
		return "[\"" + cmd + "\"" + args + "]";
	}
}

@SuppressWarnings("serial")
class ArgTypeException extends Exception {
	public Type expectType;
	public Type givenType;
	public int argIndex;
	
	public ArgTypeException(Type expectType, Type givenType, int argIndex) {
		this.expectType = expectType;
		this.givenType = givenType;
		this.argIndex = argIndex;
	}
}

class Arg {
	public Type type;
	public Object value;
	
	public Arg(Type type, Object value) {
		this.type = type;
		this.value = value;
	}
}

interface Action {
	public void onAction(Arg[] args);
}