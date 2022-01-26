package net.eduvax.dem;

import java.lang.StringBuffer;
import java.util.Collections;
import java.util.Vector;

public class Dive implements INamedObject {
    private static double[][] _vSS={
        /*1m et 5m*/ {0.9, 1.1, 1.2, 1.6, 2.0, 2.4, 2.7, 3.0, -99, -99},
        /*3m et 7m*/ {1.0, 1.3, 1.3, 1.5, 1.8, 2.2, 2.3, 2.8, -99, 3.5},
        /*10m */     {1.0, 1.3, 1.4, 1.5, 1.9, 2.1, 2.5, 2.7, -99, 3.5}
    }; 
    /** index from sommersault to in flight position */
    private static int[] _iSS={0,0,0,1,1,2,3,3,4,4};
    /** B: in flight position */
    private static double _vPos[][][]={
       /*        100 200 300 400 600 */
       /* A */ {{0.3,0.3,0.3,0.1,0.4},
                 {0.4,0.5,0.6,0.8,0.5},
                  {0.6,0.7,0.6,-99,-99},
                   {-99,-99,-99,-99,-99},
                    {-99,-99,-99,-99,-99}},
       /* B */ {{0.2,0.2,0.2,-0.2,0.3},
                 {0.1,0.3,0.3,0.3,0.3},
                  {0.2,0.3,0.2,0.5,0},
                   {0.3,0.3,0.3,0.6,0.4},
                    {0.4,-99,-99,0.7,-99}},
       /* C */ {{0.1,0.1,0.1,-0.3,0.1},
                 {0,0,0,0.1,0},
                  {0,0.1,0,0.2,0.1},
                   {0,0,0,0.3,0.1},
                    {0,-99,-99,0.4,-99}},
       /* D */ {{0.1,0.1,0.1,-0.1,0},
                 {0,-0.1,-0.1,0.2,0},
                  {0,-0.1,-0.2,0.4,0},
                   {0,0,0,-99,-99},
                    {-99,-99,-99,-99,-99}},
       /* E */ {{0.2,0.1,0.1,0.4,-99},
                 {0.2,0.2,0.2,0.5,-99},
                  {0.3,0.3,0.3,0.7,-99},
                   {0.4,-99,-99,-99,-99},
                    {-99,-99,-99,-99,-99}}};
    /** C:  */
    private static double _vTw[][][]={
       /* 51XX */ {{0.4,0.4,0.4,0.4},
                    {0.6,0.8,1.0,1.2,1.4,1.6,1.8,2.0}},
       /* 52XX */ {{0.2,0.4,0,0},
                    {0.4,0.8,0.8,1.2,1.4,1.6,1.8,2.0}},
       /* 53XX */ {{0.2,0.4,0,0},
                    {0.4,0.8,0.8,1.2,1.4,1.6,1.8,2.0}},
       /* 54XX */ {{0.2,0.4,0.2,0.4},
                    {0.4,0.8,0.8,1.2,1.4,1.6,1.8,2.0}},
       /* 61XX */ {{0.4,0.5,0.5,0.4},
                    {1.2,1.3,1.5,1.7,-99,-99,-99,-99}},
       /* 62XX */ {{0.4,0.5,0.5,0.5},
                    {1.2,1.3,1.3,1.7,-99,-99,-99,-99}},
       /* 63XX */ {{0.4,0.5,0.5,0.5},
                    {1.2,1.3,1.3,1.7,-99,-99,-99,-99}},
       /* 64XX */ {{-99,-99,-99,-99},
                    {-99,-99,-99,-99,-99,-99,-99,-99}}
       };
    /** D: Start position */
    private static double _vSP[][]={
        /* 1m 5m */ {0, 0.2, 0.3, 0.6, 0.5},
        /* 3m 7m */ {0, 0.2, 0.3, 0.3, 0.3},
        /* 10m   */ {0, 0.2, 0.3, 0.3, 0.2}};
    private static double _vSP600[]=
        /* 6XX   */ {0.2,0.4,0.2,0.4,0.3,0.5};
    /** D: index for start pos from armstand */
    private static int _i6XX[][]={
       /* 61X */    {0,0,0,0,0,1,1,1,1,1},
       /* 62X */    {0,0,1,1,1,1,1,1,1,1},
       /* 63X */    {0,0,1,1,1,1,1,1,1,1},
       /* 64X */    {0,0,0,0,0,0,0,0,0,0}};
    /** D: start pos from armstand */
    private static double _v6XX[][]= {
       /* 61X */     {0.2, 0.4},
       /* 62X */     {0.2, 0.4},
       /* 63X */     {0.3, 0.5},
       /* 64X */     {-99,-99}};
    /** E: blind entry */
    private static double _vBE[][]={
        /* 100 */ {0,0,0.1,0,0.2,0,0.2,0,0.2,0},
        /* 200 */ {0,0.1,0,0.2,0,0.3,0,0.4,0,0.5},
        /* 300 */ {0,0.1,0,0.2,0,0.3,0,0.4,0,0.5},
        /* 400 */ {0,0,0.1,0,0.2,0,0.2,0,0.2,0},
        /* 610 */ {0,0.1,0,0.2,0,0.3,0,0.4,0,0.5},
        /* 620 */ {0,0,0.1,0,0.2,0,0.2,0,0.2,0},
        /* 630 */ {0,0,0.1,0,0.2,0,0.2,0,0.2,0},
        /* 640 */ {0,0.1,0,0.2,0,0.3,0,0.4,0,0.5}
        };

    private static double specialDD(String code) {
        if ("100A".equals(code)) return 1.1;
        if ("100B".equals(code)) return 1.3;
        if ("100C".equals(code)) return 1.2;
        if ("200A".equals(code)) return 1.2;
        if ("200B".equals(code)) return 1.4;
        if ("200C".equals(code)) return 1.3;
        if ("010A".equals(code)) return 1.1;
        if ("010B".equals(code)) return 1.1;
        if ("010C".equals(code)) return 1.1;
        if ("020A".equals(code)) return 1.2;
        if ("020B".equals(code)) return 1.2;
        if ("020C".equals(code)) return 1.2;
        return 0;
    }

	private String _code;
	private double _diff;
	private Vector<Double> _score;
	private double _total=-1;
    private double _sum=0;

	public Dive(String code,double diff,int h) {
		_code=code;
		_diff=diff;
        int hIdx=0;
        if (h==3 || ( h>=7 && h<=8)) {
            hIdx=1;
        }
        else if (h==10) {
            hIdx=2;
        }
        if (_diff==0) {
            _diff=getDD(hIdx);
        }
        else {
            double dd=getDD(hIdx);
            if (_diff!=dd) {
                System.out.println("Check DD for "+code
                    +" "+_diff+" / "+dd+"(C)");
            }
        }
        _score=new Vector<Double>();
	}
	public int getSens() {
        int res=0;
        if (_code.length()>4 || _code.charAt(0)=='6') {
            res=_code.charAt(1)-'1';
        }
        else {
            res=_code.charAt(0)-'1';
        }
        return res;
    }
    public int getSomersault() {
        int res=_code.charAt(2)-'0';
        return res;
    }
    public int getTwist() {
        int res=0;
        if (_code.length()>4) {
            res=_code.charAt(3)-'0';
        }
        return res;
    }
    public boolean isArmstand() {
        return _code.charAt(0)=='6';
    }
    public int getLayout() {
        int res=_code.charAt(_code.length()-1)-'A';
        return res;
    }
    public boolean isFlying() {
        return (_code.charAt(0)<'5' && _code.charAt(1)=='1');
    }
    public double getDD(int high) {
        double sdd=specialDD(_code);
        if (sdd!=0) {
            return sdd;
        }
        int layout=getLayout();
        int ssault=getSomersault();
        int sens=getSens();
        int tw=getTwist();
        if (
               layout<0
            || layout>4
            || ssault<0
            || ssault>9
            || sens<0
            || sens>3
            || tw<0
            || tw>9
                ) {
            return 0.0;
        }
        int iSS=_iSS[ssault];
        double pA=_vSS[high][ssault];
        double pB=+_vPos[layout][iSS][isArmstand()?4:sens] 
                    +(isFlying()?_vPos[4][iSS][layout]:0);
        double pC=(tw>0?(tw==1?                         
                        _vTw[isArmstand()?sens+4:sens][0][iSS]
                        :_vTw[isArmstand()?sens+4:sens][1][tw-2]):0);
        double pD=0;
        if (isArmstand() && tw==0) {
            int ind=2*sens;
            if ((sens==0 && ssault>4)
                ||(sens==1 && ssault>1)
                ||(sens==2 && ssault>1)) {
                ind++;
            }
            pD=_vSP600[ind];
        }
        else if (!isArmstand()) {
            pD=_vSP[high][(sens==3 && ssault>2)?4:sens];
        }
        double pE=tw==0?_vBE[isArmstand()?sens+4:sens][ssault]:0;
/*
System.out.println();
System.out.println("Sens:"+getSens()+" Somersault:"+ssault);
System.out.println("  - A:"+pA);
System.out.println("  - B:"+pB);
System.out.println("  - C:"+pC);
System.out.println("  - D:"+pD);
System.out.println("  - E:"+pE);
*/
        return  Math.rint(100*(pA+pB+pC+pD+pE))/100; 
    }

    public void resetScore() {
        _score=new Vector<Double>();
        _total=-1;
    }

	public void addScore(double score) {
		_score.add(score);
	}

	public double getTotal() {
		if (_total<0 && _score.size()>0) {
			Vector<Double> sortScore=(Vector<Double>)_score.clone();
			Collections.sort(sortScore);
			int start=0;
			_sum=0;
			if (_score.size()>3) {
				start=1;
				if (_score.size()>5) {
					start=2;
				}
			}
			for(int i=0;i<3;i++) {
				_sum+=sortScore.elementAt(i+start);
			}
			_total=_sum*_diff;
		}
		return _total<0?0:_total;
	}

    public double getSum() {
        if (_total<0) {
            getTotal();
        }
        return _sum;
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
		str.append(_code+":"+_diff+":");
		for (int i=0;i<_score.size();i++) {
			str.append("\t"+_score.elementAt(i));
		}
		str.append("\t"+getSum());
		str.append("\t["+getTotal()+"]");
		return str.toString();
	}
    
    public String getName() {
        return _code;
    }
    public double getDD() {
        return _diff;
    }
}
