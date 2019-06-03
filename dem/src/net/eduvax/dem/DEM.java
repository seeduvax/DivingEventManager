package net.eduvax.dem;

import java.io.FileInputStream;


public class DEM {
	public static void main(String[] args) {
		try {
            Console console=new Console();
            for (int i=0;i<args.length;i++) {
                console.parse(new FileInputStream(args[i]));
            }
            console.parse();
		}
		catch (Exception ex) {
			System.err.println("Exception "+ex+" "+ex.getMessage());
			ex.printStackTrace();
		}
	}
}
