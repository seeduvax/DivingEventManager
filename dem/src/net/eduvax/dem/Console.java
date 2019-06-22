package net.eduvax.dem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Console {
	private PrintStream _out;
    private PrintStream _log;
	private BufferedReader _in;
    private CommandInterpretor _ci;
	private String _prompt="dem> ";
	public Console() {
		_out=System.out;
        String cmdLogName="dem-"+(new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date())+".log";
        try {
	        _log=new PrintStream(new FileOutputStream(cmdLogName));
        }
        catch (IOException ex) {
			System.err.println("Can't open command log file");
			System.err.println("Exception "+ex+" "+ex.getMessage());
			ex.printStackTrace();
        }
		_ci=new CommandInterpretor(this);
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
    public synchronized void runCommand(String strCmd) {
        Runnable cmd=_ci.getCmd(strCmd);
        if (cmd!=null) {
            int round=_ci.getRound();
            try {
                cmd.run();
                _log.println(strCmd);
                if (_ci.getRound()!=round) {
                    _log.println("# ----- R"+_ci.getRound()+" -----");
                }
            }
            catch (Exception ex) {
                System.out.println("Command failed: "+strCmd);
                ex.printStackTrace();
            }
        }
    }
    public void parse() throws IOException {
        parse(null);
    }
    public void parse(InputStream input) throws IOException {
        boolean prompt= input==null || input==System.in;
        if (input==null) {
            input=System.in;
        }
		_in=new BufferedReader(new InputStreamReader(input));
        if (prompt) {
			_out.print(_prompt);
        }
		String strCmd=_in.readLine();
        while (strCmd!=null&&!_ci.completed()) {
            runCommand(strCmd);
            if (prompt) {
                _out.print(_prompt);
            }
            if (!_ci.completed()) {
                strCmd=_in.readLine();
            }
        }
    }
}
