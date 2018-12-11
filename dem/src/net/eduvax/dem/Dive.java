package net.eduvax.dem;

import java.lang.StringBuffer;
import java.util.Collections;
import java.util.Vector;

public class Dive {
	private String _code;
	private double _diff;
	private Vector<Double> _score;
	private double _total=-1;

	public Dive(String code,double diff) {
		_code=code;
		_diff=diff;
	}

	public void addScore(double score) {
		_score.add(score);
	}

	public double getTotal() {
		if (_total<0) {
			Vector<Double> sortScore=(Vector<Double>)_score.clone();
			Collections.sort(sortScore);
			int start=0;
			_total=0;
			if (_score.size()>3) {
				start=1;
				if (_score.size()>5) {
					start=2;
				}
			}
			for(int i=0;i<3;i++) {
				_total+=sortScore.elementAt(i+start);
			}
			_total*=_diff;
		}
		return _total;
	}

	public double[] getScore() {
		double[] res=new double[_score.size()];
		for (int i=0;i<_score.size();i++) {
			res[i]=_score.elementAt(i);
		}
		return res;
	}

	public String toString() {
		StringBuffer str=new StringBuffer();
		str.append(_code);
		for (int i=0;i<_score.size();i++) {
			str.append("\t"+_score.elementAt(i));
		}
		str.append("\t"+getTotal());
		return str.toString();
	}
}
