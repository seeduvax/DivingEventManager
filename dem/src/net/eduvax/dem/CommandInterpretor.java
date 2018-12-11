package net.eduvax.dem;

import java.util.Vector;

class CommandInterpretor {
	public interface ICommand extends Runnable {
		ICommand get(String str);
	}
	public class CT1 implements ICommand {
		public ICommand get(String s) {
			return "ct1".equals(s)?new CT1():null;
		}
		public void run() {
			System.out.println("Running command test 1");
		}
	}
	public class CT2 implements ICommand {
		public ICommand get(String s) {
			return "ct2".equals(s)?new CT2():null;
		}
		public void run() {
			System.out.println("Running command test 2");
		}
	}
	private boolean _completed=false;
	private Vector<ICommand> _cmd;
	public CommandInterpretor() {
		_cmd=new Vector<ICommand>();
		_cmd.add(new CT1());
		_cmd.add(new CT2());
	}
	public Runnable getCmd(String s) {
		String cmdStr=s.trim();
		Runnable res=null;
		int i=0;
		while (res==null && i<_cmd.size()) {
			res=_cmd.elementAt(i).get(cmdStr);
			i++;
		}
		if ("quit".equals(cmdStr)) {
			_completed=true;
		}
		if (res==null && !_completed) {
			System.out.println("Unknomw command: "+cmdStr);
		}
		return res;
	}
	boolean completed() {
		return _completed;
	}
}
