package net.eduvax.dem;

import java.lang.StringBuffer;
import java.util.Vector;

public class DiveSheet {
	private Diver _diver;
	private Vector<Dive> _dives;
	private Vector<Rules> _rules;

	public boolean check() {
		boolean res=true;
		for (Rules rules : _rules) {
			res=res && rules.match(_diver,_dives);
		}
		return res;
	}

	public double getScore() {
		double res=0;
		for (int i=0;i<_dives.size();i++) {
			res+=_dives.elementAt(i).getTotal();
		}
		return res;
	}

	public String toString() {
		StringBuffer str=new StringBuffer();
		for (int i=0;i<_dives.size();i++) {
			str.append(_dives.toString()+"\n");
		}
		return str.toString();
	}
}
