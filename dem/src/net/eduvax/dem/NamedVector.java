package net.eduvax.dem;

import java.util.Vector;


public class NamedVector<T extends INamedObject> extends Vector<T> implements INamedObject {
    private String _name;
    public NamedVector(String name) {
        _name=name;
    }
    public String getName() {
        return _name;
    }
    public T getByName(String name) {
        for (T elem: this) {
            if (name.equals(elem.getName())) {
                return elem;
            }
        }
        return null;
    }
}
