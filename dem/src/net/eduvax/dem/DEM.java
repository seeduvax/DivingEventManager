package net.eduvax.dem;

public class DEM {
	public static void main(String[] args) {
		Console console=new Console();
		CommandInterpretor ci=new CommandInterpretor();
		do {
			Runnable cmd=ci.getCmd(console.readLine());
			if (cmd!=null) {
				cmd.run();
			}
		}
		while (!ci.completed());
	}
}
