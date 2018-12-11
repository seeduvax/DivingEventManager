package net.eduvax.dem;

import java.util.Vector;

public class Session extends NamedVector<Contest> implements INamedObject {
    public Session(String name) {
        super(name);
    }
}
