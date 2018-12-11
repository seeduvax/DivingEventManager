package net.eduvax.dem;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.util.Date;
import java.text.SimpleDateFormat;

public class DEM {
	public static void main(String[] args) {
		try {
            String cmdLogName="dem-"+(new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date())+".log";
			PrintStream cmdLog=new PrintStream(new FileOutputStream(cmdLogName));
			CommandInterpretor ci=new CommandInterpretor();
            for (int i=0;i<args.length;i++) {
                BufferedReader reader=new BufferedReader(new InputStreamReader(
                        new FileInputStream(args[i])));
                String strCmd=reader.readLine();
                while (strCmd!=null&&!ci.completed()) {
                    Runnable cmd=ci.getCmd(strCmd);
                    if (cmd!=null) {
                        int round=ci.getRound();
                        try {
                            cmd.run();
                            cmdLog.println(strCmd);
                            if (ci.getRound()!=round) {
                                cmdLog.println("# ----- R"+ci.getRound()+" -----");
                            }
                        }
                        catch (Exception ex) {
                            System.out.println("Command failed: strCmd");
                            ex.printStackTrace();
                        }
                    }
                    if (!ci.completed()) {
                        strCmd=reader.readLine();
                    }
                }
            }
			Console console=new Console();
			String strCmd=console.readLine();
            while (strCmd!=null&&!ci.completed()) {
				Runnable cmd=ci.getCmd(strCmd);
				if (cmd!=null) {
                    try {
                        cmd.run();
                        cmdLog.println(strCmd);	
                    }
                    catch (Exception ex) {
                        System.out.println("Command failed: strCmd");
                        ex.printStackTrace();
                    }
				}
                if (!ci.completed()) {
                    strCmd=console.readLine();
                }
			}
		}
		catch (Exception ex) {
			System.err.println("Exception "+ex+" "+ex.getMessage());
			ex.printStackTrace();
		}
	}
}
