package net.eduvax.dem;

import java.util.Date;

public class Event extends NamedVector<Session> {
	private Date _date;
	public Event(String name) {
		super(name);
        _date=new Date();
	}
    public Event(String name,Date date) {
        super(name);
        _date=date;
    }
    public Date getDate() {
        return _date;
    }
}
