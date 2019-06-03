package net.eduvax.dem;

import java.util.Enumeration;


public class Session extends NamedVector <DiveSheet> {
    public Session(String name) {
        super(name);
    }
    private int _round=0;
    private DiveSheet _cSheet=null;
    private Enumeration<DiveSheet> _sheetEnum=null;
    private boolean _completed=false;
    private Dive _cDive;

    public void start() {
        _round=0;
        _sheetEnum=elements();
        _cSheet=_sheetEnum.nextElement();
        _cDive=_cSheet.elementAt(_round);
        _completed=false;
    }

    public void next() {
        boolean loop=false;
        _cDive=null;
        while (_cDive==null&&!_completed) {
            if (!_sheetEnum.hasMoreElements()) {
                _round++;
                if (!loop) {
                    _sheetEnum=elements();
                    loop=true;
                }
                else {
                    _completed=true;
                }
            }
            if (!_completed) {
                _cSheet=_sheetEnum.nextElement();
                if (_cSheet.size()>_round) {
                    _cDive=_cSheet.elementAt(_round);
                }
            }
        }
        if (_completed) {
            System.out.println("Completed !!!");
        }
    }
    public int getRound() {
        return _round;
    }
    public Dive getCurrentDive() {
        return _cDive;
    }
    public DiveSheet getCurrentSheet() {
        return _cSheet;
    }
    public boolean isCompleted() {
        return _completed;
    }
}
