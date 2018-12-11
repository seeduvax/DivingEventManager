package net.eduvax.dem;

import java.util.Collections;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

class CommandInterpretor {
    class UIState {
	    Event _event=null;
        Session _session=null;
        Contest _contest=null;
        DiveSheet _diveSheet=null;
        Dive _dive=null;
        boolean _completed=false;
        Enumeration<DiveSheet> _sheetEnum=null;
        int _round=0;
        
        void start() {
            _sheetEnum=_contest.elements();
            _diveSheet=_sheetEnum.nextElement();
            _round=0;
            _dive=_diveSheet.elementAt(_round);
            _completed=false;
        }

        void next() {
            boolean loop=false;
            _dive=null;
            while (_dive==null&&!_completed) {
                if (!_sheetEnum.hasMoreElements()) {
                    _round++;
                    if (!loop) {
                        _sheetEnum=_contest.elements();
                        loop=true;
                    }
                    else {
                        _completed=true;
                    }
                }
                if (!_completed) {
                    _diveSheet=_sheetEnum.nextElement();
                    if (_diveSheet.size()>_round) {
                        _dive=_diveSheet.elementAt(_round);
                    }
                }
            }
            if (_completed) {
                System.out.println("Completed !!!");
            }
        }

        void showCurrent() {
            if (!_completed) {
                System.out.println("Round: "+(_round+1));
                System.out.println(" "+_diveSheet.getDiver()+" "+str2digit(_diveSheet.getScore()));
                System.out.println(" "+_dive.getName()+" ["+_dive.getDD()+"]");
            }
            else {
                Vector<DiveSheet> v=new Vector<DiveSheet>(_contest);
                Collections.sort(v,new DiveSheet.Comp());
                int j=0;
                System.out.println("---------------------------------");
                for (int i=v.size()-1;i>=0;i--) {
                    DiveSheet ds=v.elementAt(i);
                    if (j>0 && ds.getDiver().getGenre() != 
                            v.elementAt(i+1).getDiver().getGenre() ) {
                        j=1;
                        System.out.println("---------------------------------");
                    }
                    else {
                        j++;
                    }
                    System.out.println(""+j+": "+ds.getDiver()+"\t: "+str2digit(ds.getScore()));
                }
            }
        }
        public String str2digit(double d) {
            return String.format("%1$.2f",Math.rint(d*100)/100);
        }
    }
    private UIState _state=new UIState();
    public int getRound() {
        return _state._round;
    }

	public interface ICommand extends Runnable {
		ICommand get(String str);
        boolean match(String str);
	}

    public abstract class Command implements ICommand, INamedObject {
        private StringTokenizer _argST;
        private String _name;
        protected Command(String name) {
            _name=name;
        }
        public String getName() {
            return _name;
        }
        public boolean match(String s) {
            return s.startsWith(_name);
        }
        protected Command get() {
/*
 * A clone should be preferable...
            try {
                return (Command)clone();
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
*/
return this;
        }        
        protected String nextArg() {
            if (_argST.hasMoreTokens()) {
                return _argST.nextToken();
            }
            return null;
        }
        public ICommand get(String s) {
            Command res=null;
            if (match(s)) {
                res=get();
                res._argST=new StringTokenizer(s);
                if (s.startsWith(_name)) {
                    // for d command that is a special case
                    // first word may be first arg.
                    // TODO consider to make specific handling in child
                    // class instead of here...
                    res._argST.nextToken();
                }
            } 
            else if (_name.equals(s)) {
                res=get();
                res._argST=new StringTokenizer("");
            }
            return res;
        }
    }
    public interface Fact<T,U> {
        T getCtnr();
        U newElem(Command c);            
    }
    public class CNamedVectEntry<T extends NamedVector, U> extends Command {
        private Fact<T,U> _fact=null;
        public CNamedVectEntry(String name,Fact<T,U> fact) {
            super(name);
            _fact=fact;
        }
        protected Command get() {
            return new CNamedVectEntry<T,U>(getName(),_fact);
        }
        protected T getCtnr() {
            return _fact.getCtnr();
        }
        protected U newElem() {
            return _fact.newElem(this);
        }
        public void run() {
            T container=getCtnr();
            if (container!=null) {
                U elem=newElem();
                if (elem!=null) {
                    container.add(elem);
                    System.out.println("Done "+getName());
                }
                else {
                    System.out.println("Element creation error. check args.");
                }
            }
            else {
                System.out.println("No container to add element, check current selection.");
            }
        }
    }
	public class CEv extends Command {
		private String _evName;
        public CEv() {
            super("cEvent");
        }
		public ICommand get(String s) {
			CEv cev=null;
			if (s.startsWith("cEvent ")) {
				cev=new CEv();
				cev._evName=s.substring(7);
			}
			return cev;
		}
		public void run() {
			_state._event=new Event(_evName);
			System.out.println("Event "+_evName+" ready.");
		}
	}
	private boolean _completed=false;
	private Vector<ICommand> _cmd;
	public CommandInterpretor() {
		_cmd=new Vector<ICommand>();
		_cmd.add(new CEv());
        _cmd.add(new CNamedVectEntry<Event,Session>("cSession",
            new Fact<Event,Session>() {
                public Event getCtnr() {
                    return _state._event;
                }
                public Session newElem(Command c) {
                    Session s=new Session(c.nextArg());
                    _state._session=s;
                    return s;
                }
            }));
        _cmd.add(new CNamedVectEntry<Session,Contest>("cContest",
            new Fact<Session,Contest>() {
                public Session getCtnr() {
                    return _state._session;
                }
                public Contest newElem(Command cmd) {
                    Contest c=new Contest(cmd.nextArg());
                    _state._contest=c;
                    return c;
                }
            }));
        _cmd.add(new CNamedVectEntry<Contest,DiveSheet>("cSheet",
            new Fact<Contest,DiveSheet>() {
                public Contest getCtnr() {
                    return _state._contest;
                }
                public DiveSheet newElem(Command cmd) {
                    String name=cmd.nextArg();
                    int year=Integer.parseInt(cmd.nextArg());
                    String club=cmd.nextArg();
                    Diver.Genre genre=cmd.nextArg().toUpperCase().charAt(0)>='H'?Diver.Genre.MALE:Diver.Genre.FEMAL;
                    Diver diver=new Diver(name,year,club,genre);
                    DiveSheet ds=new DiveSheet(diver);
                    _state._diveSheet=ds;
                    return ds;
                }
            }));
        _cmd.add(new Command("aDive") {
                public void run() {
                    String code=nextArg();
                    double dd=Double.parseDouble(nextArg());
                    String s=nextArg();
                    int height=-1;
                    if (s!=null) {
                        height=Integer.parseInt(s);
                    }
                    Dive dive=new Dive(code,dd);
                    _state._diveSheet.add(dive); 
                }
            });
        _cmd.add(new Command("start") {
                public void run() {
                    System.out.println("Starting current session: "+_state._session);
                    _state.start();
                    _state.showCurrent();
                }
            });
        _cmd.add(new Command("d") {
                public boolean match(String s) {
                    if (s.startsWith("d ")) {
                        return true;
                    }
                    try {
                        StringTokenizer st=new StringTokenizer(s," ");
                        double d=Double.parseDouble(st.nextToken());
                        return true;
                    }
                    catch (Exception ex) {
                        return false;
                    }
                }
                public void run() {
                    _state._dive.resetScore();
                    String arg=nextArg();
                    while (arg!=null) {
                        _state._dive.addScore(Double.parseDouble(arg));
                        arg=nextArg();
                    }
                    System.out.println(" Score: "
                                +_state.str2digit(_state._dive.getSum())+" ["
                                +_state.str2digit(_state._dive.getTotal())
                                +"], total="+_state.str2digit(_state._diveSheet.getScore()));
                }
            });
        _cmd.add(new Command("fd") {
                public void run() {
                    String code=nextArg();
                    double dd=Double.parseDouble(nextArg());
                    Dive dive=new Dive(code,dd);
                    _state._diveSheet.set(_state._round,dive);
                    _state._dive=dive;
                    _state.showCurrent();
                }
            });
        _cmd.add(new Command("n") {
                public void run() {
                    _state.next();
                    System.out.println();
                    _state.showCurrent();
                }
            });
	}
	public Runnable getCmd(String s) {
		String cmdStr=s.trim();
		Runnable res=null;
        if (!cmdStr.startsWith("#")) {
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
        }
		return res;
	}
	boolean completed() {
		return _completed;
	}
}
