import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Award {

	public static final String[] bande = { "160M", "80M", "60M", "40M", "30M", "20M", "17M", "15M", "12M", "10M",
			"6M" };
	List<QSL> conferme = new ArrayList<QSL>();
	public DXCC dxcc;
	public WAS was;

	public Award(String fileSorgente) throws IOException{		
		loadQSL(fileSorgente);
		dxcc = new DXCC(conferme);
		was = new WAS(conferme);
	}

	/**
	 * Carica le QSL
	 * 
	 * @param fileSorgente il file .adi dal quale caricare le QSL
	 * @throws IOException
	 */
	public void loadQSL(String fileSorgente) throws IOException {
		Timestamp t1 = new Timestamp(System.currentTimeMillis());
		File reportFile = new File(fileSorgente);
		FileInputStream fis = new FileInputStream(reportFile);
		byte[] data = new byte[(int) reportFile.length()];
		fis.read(data);
		fis.close();
		
		Date fileDate = new Date(reportFile.lastModified());
		Date now = new Date();
		long diff = now.getTime() - fileDate.getTime();
	    int age = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);


		String[] report = new String(data, "UTF-8").split("<eoh>");
		String header = report[0].split("\n")[1];
		System.out.format("Report di %d giorni, %s", age, header);

		report = report[1].split("<eor>");

		for (String record : report) {
			if (!record.contains("<DXCC:"))
				continue;
			QSL qsl = new QSL(record);
			conferme.add(qsl);
		}
		Timestamp t2 = new Timestamp(System.currentTimeMillis());
		float tt = t2.getTime() - t1.getTime();
		System.out.format("Ho elaborato il file: %s in %dms.\n", fileSorgente, (int)tt);
	}

	/**
	 * Scarica i file report
	 * 
	 * @param report seleziona tra report LOTW o lista utenti
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void downloader(String report) throws MalformedURLException, IOException {
		final String uri, destinazione;
		if (report.equals("user")) {
			uri = "https://lotw.arrl.org/lotw-user-activity.csv";
			destinazione = "lotw-user-activity.csv";
		} else if (report.equals("lotw")) {
			final String lotwUrl = "https://p1k.arrl.org/lotwuser/lotwreport.adi?";
			String credenziali = "login=iz0kba&password=HakunaMatata";
			final String query = "&qso_query=1&qso_qslsince=2017-11-08&qso_qsldetail=yes";
			uri = lotwUrl + credenziali + query;
			destinazione = "lotwreport.adi";
		} else
			return;

		try (InputStream in = URI.create(uri).toURL().openStream()) {
			System.out.println("Download in corso: " + destinazione);
			Files.copy(in, Paths.get(destinazione), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public void compareTo(Award controllo) {
		System.out.println(conferme.size() + " " + controllo.conferme.size());
	}

	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.println("Scaricare il report LOTW? (Y)");
		if (sc.nextLine().toUpperCase().equals("Y")) {
			downloader("user");
			downloader("lotw");
		}
		sc.close();

		Award myAward = new Award("lotwreport.adi");
		DXCC dxcc = myAward.dxcc;		
		WAS was = myAward.was;
		myAward.showTable();
		
		List<String> banda = was.showConfirmed("80M", "FT8");
		System.out.println(banda.size() + " stati in banda 80M: " + banda);

		// Award grrAward = new Award("lotwreport-grr.adi");
		// myAward.compareTo(grrAward);
	}
	
	public void showTable() {
		for (String banda : bande) 
			System.out.println(dxcc.getScore(banda) + ", WAS: " + was.showConfirmed(banda, "FT8").size());
	}
}

class WAS {

	private HashMap<String, HashSet<String>> wasFT8 = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> statiFT4 = new HashMap<String, HashSet<String>>();

	public WAS(List<QSL> conferme) {
		for (QSL qsl : conferme) {
			if (!qsl.stato.equals("DX")) {
				String stato = qsl.stato.split("//")[0].strip();
				if (qsl.ft8) {	
					if (!wasFT8.containsKey(stato)) wasFT8.put(stato, new HashSet<String>());
					wasFT8.get(stato).add(qsl.banda);
				} else if (qsl.ft4) {
					if (!statiFT4.containsKey(stato)) statiFT4.put(stato, new HashSet<String>());
					statiFT4.get(stato).add(qsl.banda);
				}
			}
		}
		System.out.println(size());
	}
	
	/*
	 * Verifica se uno stato è confermato in banda e modo
	 */
	public boolean isConfirmed(String stato, String modo, String banda) {
		switch (modo) {
		case ("FT8"):
			if (wasFT8.containsKey(stato)) {
				return wasFT8.get(stato).contains(banda);
			}
		case("FT4"):
			if (statiFT4.containsKey(stato)) {
				return statiFT4.get(stato).contains(banda);
			}
		}
		return false;
	}
	
	/*
	 * Restituisce la lista ordinata degli stati confermati per banda e modo
	 */
	public List<String> showConfirmed(String banda, String modo) {
		List<String> lista = new ArrayList<String>();
		switch (modo) {
		case ("FT8"):
			for (String s : wasFT8.keySet()) {
				if (wasFT8.get(s).contains(banda)) lista.add(s);
			}
			break;
		case ("FT4"):
			for (String s : wasFT8.keySet()) {
				if (wasFT8.get(s).contains(banda)) lista.add(s);
			}
			break;
		}
		Collections.sort(lista);
		return lista;
	}
	
	public List<String> showConfirmed(String modo) {
		List<String> lista = new ArrayList<String>();
		switch (modo) {
		case ("FT8"):
			lista = new ArrayList<String>(wasFT8.keySet());
			break;
		case ("FT4"):
			lista = new ArrayList<String>(statiFT4.keySet());
			break;
		}
		Collections.sort(lista);
		return lista;
	}

	/*
	 * Restituisce il conteggio totale degli stati confermati
	 */
	public String size() {
		return "Totale stati FT8: " + wasFT8.size() + ", stati in FT4: " + statiFT4.size();
	}
	
}

class DXCC {	
	private HashMap<String, HashSet<String>> dxcc = new HashMap<String, HashSet<String>>();
	
	public DXCC(List<QSL> conferme) {		
		for (QSL qsl : conferme) {
			if (qsl.ft8 || qsl.ft4) {
				if (!dxcc.containsKey(qsl.dxccn)) dxcc.put(qsl.dxccn, new HashSet<String>());
				dxcc.get(qsl.dxccn).add(qsl.banda);
			}
		}
		System.out.println("Totale entita' confermate in FT8 e FT4: " + dxcc.size());
	}
	
	/*
	 * Restituisce una stringa con il totale delle entità per banda.
	 */
	public String getScore(String banda) {
		int count = 0;
		for (HashSet dxccn : dxcc.values()) {
			if (dxccn.contains(banda)) count++;
		}
		return "Entita' confermate in " + banda + ": " + count;
	}
	
	/**
	 * restituisce il colre in base alla priorità DXCC
	 * @param dxccn
	 * @param banda
	 * @return
	 */
	public String getColor(String dxccn, String banda, String modo) {
		if (dxccn.equals("-1")) return "\u001B[34m";
		if (modo.equals("DATA")) {
			if (dxcc.containsKey(dxccn)) {
				if (dxcc.get(dxccn).contains(banda)) {
					return "\u001B[37m";
				}
				return "\u001B[33m";
			}
			return  "\u001B[31m";
		}
		return "\u001B[34m";
	}
}

class Americani {
	
	private HashMap<String, String> americani = new HashMap<String, String>(); 
	
	public Americani(String file) throws FileNotFoundException {
		File fileArchivio = new File(file);
		Scanner fileReader = new Scanner(fileArchivio);
		while (fileReader.hasNextLine()) {
			String[] americano = fileReader.nextLine().split(",");
			americani.put(americano[0], americano[1]);
		}
		fileReader.close();
	}
	
	public Americani() throws FileNotFoundException {
		this("americani.csv");
	}
	
	public String getState(String callsign) {
		if (americani.containsKey(callsign))
			return americani.get(callsign);
		return "  ";
	}
}