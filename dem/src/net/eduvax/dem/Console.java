package net.eduvax.dem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;

public class Console {
	private PrintStream _out;
	private BufferedReader _in;
	private String _prompt="dem> ";
	public Console() {
		_out=System.out;
		_in=new BufferedReader(new InputStreamReader(System.in));
	}
	public void setPrompt(String prompt) {
		_prompt=prompt;
	}
	public String readLine() {
		try {
			_out.print(_prompt);
			return _in.readLine();
		}
		catch (IOException ex) {
			return "";
		}
	}
}
