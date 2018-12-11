package net.eduvax.dem;

import java.lang.StringBuffer;
import java.util.Comparator;

public class DiveSheet extends NamedVector<Dive> {

    public static class Comp implements Comparator<DiveSheet> {
        public int compare(DiveSheet ds1, DiveSheet ds2) {
            int res=0;
            if (ds1.size() < ds2.size()) {
                res=-1;
            }
            else if (ds1.size() > ds2.size()) {
                res=1;
            }
            else {
                if (ds1.getScore() < ds2.getScore()) {
                    res=-1;
                }
                else if (ds1.getScore() > ds2.getScore()) {
                    res=1;
                }
            }
            return res;
        }
    }

	private Diver _diver;

    public DiveSheet(Diver diver) {
        super(diver.getName());
        _diver=diver;
    }
    
/*
 *  TODO à revoir...
	private Vector<Rules> _rules;

	public boolean check() {
		boolean res=true;
		for (Rules rules : _rules) {
			res=res && rules.match(_diver,_dives);
		}
		return res;
	}
 */
	public double getScore() {
		double res=0;
		for (Dive d: this) {
			res+=d.getTotal();
		}
		return res;
	}

	public String toString() {
		StringBuffer str=new StringBuffer();
        str.append(getName()+":");
		for (Dive d: this) {
			str.append(d.toString()+"\n");
		}
		return str.toString();
	}

    public Diver getDiver() {
        return _diver;
    }
}
