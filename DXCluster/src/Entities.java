import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

class Entita {
	
	public String name;
	public String dxccn;
	public String continent;
	
	public Entita(String info) {
		String[] i = info.split(";");
		name = i[0];
		dxccn = i[4];
		continent = i[1];
	}
	
	public String toString() {
		return String.join(" ", name, continent);
	}	
}

public class Entities {
	private HashMap<String, String> entities = new HashMap<String, String>();
	
	public Entities() throws FileNotFoundException {
		File fileArchivio = new File("entities.ini");
		Scanner fileReader = new Scanner(fileArchivio);
		while (fileReader.hasNextLine()) {
			String line = fileReader.nextLine();
			if (line.contains("=")) {
				String[] e = line.split("=");
				entities.put(e[0], e[1]);
			}			
		}
		fileReader.close();	
	}
	
	/**
	 * Restituisce un oggetto entità
	 * @param callsign il nominativo da cui estrarre informazioni
	 * @return entità
	 */
	public Entita getEntities(String callsign) {
		callsign = callsign.toUpperCase();
		Entita e;
		if (callsign.length() >= 4 && entities.containsKey(callsign.substring(0, 3))) {
			e = new Entita(entities.get(callsign.substring(0, 3)));
		} else if (entities.containsKey(callsign.substring(0, 2))) {
			e = new Entita(entities.get(callsign.substring(0, 2)));
		} else if (entities.containsKey(callsign.substring(0, 1))) {
			e = new Entita(entities.get(callsign.substring(0, 1)));
		} else if (entities.containsKey(callsign.substring(0, 0))) {
			e = new Entita(entities.get(callsign.substring(0, 0)));
		} else {
			e = new Entita("Sconosciuto         ; ''; 0; 0;-1");
		}
		return e;
	}
}