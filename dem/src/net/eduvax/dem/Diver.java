
package net.eduvax.dem;


public class Diver implements INamedObject {
	private String _name;
	private String _club;
	private int _birthYear;
	private String _licenceId;
	private String _nation;
    
	public enum Genre {
		FEMAL,
		MALE
	};
	private Genre _genre;
    public Diver(String name, int year, String club,Genre genre) {
        _name=name;
        _birthYear=year;
        _genre=genre;
        _club=club;
        _nation="fr";
    }
    public String getName() {
        return _name;
    }

    public String toString() {
        return _name+" "+_birthYear+" "+_club;
    }
    public Genre getGenre() {
        return _genre;
    }
}
