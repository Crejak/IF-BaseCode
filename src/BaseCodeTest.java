
public class BaseCodeTest {

	public static void main(String[] args) {
		final ConsoleIO c = new ConsoleIO();
		c.addCommand("print", new Type[] { Type.STRING }, new Action() {
			@Override
			public void onAction(Arg[] args) {
				String msg = (String) args[0].value;
				System.out.println(msg);
			}
		});
		c.addCommand("exit", null, new Action() {
			@Override
			public void onAction(Arg[] args) {
				c.close();
			}
		});
		c.addCommand("add", new Type[] { Type.INT, Type.INT }, new Action() {
			@Override
			public void onAction(Arg[] args) {
				int a = (int)args[0].value;
				int b = (int)args[1].value;
				System.out.println(a + " + " + b + " = " + (a+b));
			}
		});
		c.addCommand("add", new Type[] {Type.FLOAT, Type.FLOAT}, new Action() {
			@Override
			public void onAction(Arg[] args) {
				float a = (float)args[0].value;
				float b = (float)args[1].value;
				System.out.println(a + " + " + b + " = " + (a+b));
			}
		});
		c.printCommands();
		c.run();
	}

}
