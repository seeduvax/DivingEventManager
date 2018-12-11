package net.eduvax.dem;

public class Contest extends NamedVector <DiveSheet> {
	private Diver.Genre _genre;
	private Rules _rules;
// TODO à voir si ça vaut le coup...
//	private Contest _overClass=null;

    public Contest(String name) {
        super(name);
    }
}
