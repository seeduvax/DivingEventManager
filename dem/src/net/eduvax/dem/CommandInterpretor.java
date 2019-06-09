package net.eduvax.dem;

import java.util.Collections;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import net.eduvax.dem.www.WebServer;

public class CommandInterpretor {
    private static WebServer _webServer=null;
    public class UIState {
        Session _session=null;
        DiveSheet _diveSheet=null;
        Enumeration<DiveSheet> _sheetEnum=null;

        void showCurrent() {
            if (!_session.isCompleted()) {
                System.out.println("Round: "+(_session.getRound()+1));
                DiveSheet sheet=_session.getCurrentSheet();
                System.out.println(" "+sheet.getDiver()+" "
                    +Session.str2digit(sheet.getScore()));
                Dive dive=_session.getCurrentDive();
                System.out.println(" "+dive.getName()+" ["+dive.getDD()+"]");
            }
            else {
                Vector<DiveSheet> v=new Vector<DiveSheet>(_session);
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
                    System.out.println(""+j+": "+ds.getDiver()+"\t: "
                        +Session.str2digit(ds.getScore()));
                }
            }
        }
    }
    private UIState _state=new UIState();

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
                res._argST=new StringTokenizer(s," \t");
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
    private boolean _completed=false;
	private Vector<ICommand> _cmd;
	public CommandInterpretor() {
        _state._session=new Session("");
		_cmd=new Vector<ICommand>();
        _cmd.add(new CNamedVectEntry<Session,DiveSheet>("cSheet",
            new Fact<Session,DiveSheet>() {
                public Session getCtnr() {
                    return _state._session;
                }
                private boolean isInteger(String str) {
                    boolean res=str.length()>0;
                    int i=0;
                    while (res && i<str.length()) {
                        char c=str.charAt(i);
                        res&= c>='0' && c<='9';
                        i++;
                    }
                    return res;
                }
                public DiveSheet newElem(Command cmd) {
                    String name=cmd.nextArg();
                    String ystr=cmd.nextArg();
                    while (ystr!=null && !isInteger(ystr)) {
                        name=name+" "+ystr;
                        ystr=cmd.nextArg();
                    }
                    int year=Integer.parseInt(ystr);
                    String club=cmd.nextArg();
                    Diver.Genre genre=cmd.nextArg().toUpperCase().charAt(0)>='H'?Diver.Genre.MALE:Diver.Genre.FEMAL;
                    Diver diver=new Diver(name,year,club,genre);
                    DiveSheet ds=new DiveSheet(diver);
                    _state._diveSheet=ds;
                    String h=cmd.nextArg();
                    if (h!=null) {
                        ds.setHeight(Integer.parseInt(h));
                    }
                    return ds;
                }
            }));
        _cmd.add(new Command("aDive") {
                public void run() {
                    String a=nextArg();
                    int h=_state._diveSheet.getHeight();
                    while (a!=null) {
                        String code=a;
                        a=nextArg();
                        double dd=0;
                        if (a!=null && a.charAt(1)=='.') {
                            dd=Double.parseDouble(a);
                            a=nextArg();
                        }
                        // TODO add something to optionnaly take height (for platform)
                        Dive dive=new Dive(code,dd,h);
                        _state._diveSheet.add(dive); 
                    }
                }
            });
        _cmd.add(new Command("start") {
                public void run() {
                    System.out.println("Starting current session: "+_state._session);
                    _state._session.start();
                    _state.showCurrent();
                }
            });
        _cmd.add(new Command("d") {
                public boolean match(String s) {
                    if (s.startsWith("d ")) {
                        return true;
                    }
                    try {
                        StringTokenizer st=new StringTokenizer(s," \t");
                        double d=Double.parseDouble(st.nextToken());
                        return true;
                    }
                    catch (Exception ex) {
                        return false;
                    }
                }
                public void run() {
                    Dive dive=_state._session.getCurrentDive();
                    dive.resetScore();
                    String arg=nextArg();
                    while (arg!=null) {
                        dive.addScore(Double.parseDouble(arg));
                        arg=nextArg();
                    }
                    System.out.println(" Score: "
                                +Session.str2digit(dive.getSum())+" ["
                                +Session.str2digit(dive.getTotal())
                                +"], total="+Session.str2digit(_state._session.getCurrentSheet().getScore()));
                }
            });
        _cmd.add(new Command("fd") {
                public void run() {
                    String code=nextArg();
                    String a=nextArg();
                    double dd=0;
                    if (a!=null) {
                        dd=Double.parseDouble(a);
                    }
                    _state._session.setCurrentDive( new Dive(code,dd,
                          _state._session.getCurrentSheet().getHeight()));
                    _state.showCurrent();
                }
            });
        _cmd.add(new Command("n") {
                public void run() {
                    _state._session.next();
                    System.out.println();
                    _state.showCurrent();
                }
            });
        _cmd.add(new Command("wstart") {
                public void run() {
                    if (_webServer==null) {
                        try {
                            _webServer=new WebServer(8080,_state._session);
                            _webServer.start();
                        }
                        catch (Exception ex) {
                            System.err.println("Can't start web server");
                            ex.printStackTrace();
                        }
                    }
                }
            });
        _cmd.add(new Command("wstop") {
                public void run() {
                    if (_webServer!=null) {
                        try {
                            _webServer.stop();
                            _webServer.join();
                        }
                        catch (Exception ex) {
                            System.err.println("Can't stop web server");
                            ex.printStackTrace();
                        }
                    }
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
	public boolean completed() {
		return _completed;
	}
    public int getRound() {
        return _state._session.getRound();
    } 
}
