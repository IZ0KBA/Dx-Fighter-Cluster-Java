import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Spot {

	private static int lunghezzaSpot = 50;
	public static int contatore;
	public static HashSet<String> listaQrz = new HashSet<String>();
	public static HashSet<String> listaEntita = new HashSet<String>();
	public static HashSet<String> listaNewOne = new HashSet<String>();
	public String text, qrz, entita, banda, modo, dxccn;
	public double qrg;
	public String lotw = "<      <", lotwData = "          ";

	public Spot(String line) {
		contatore++;
		qrz = line.substring(26, 40).strip();
		qrg = Double.parseDouble(line.substring(line.indexOf(":") + 1, line.indexOf(".") + 2).trim());
		text = line.replace("\07", "");
		Entita e = DxCluster.entities.getEntities(qrz);
		entita = e.name;
		dxccn = e.dxccn;
		String[] bandaModo = getBand(qrg);
		banda = bandaModo[0];
		modo = bandaModo[1];
		if (lunghezzaSpot < text.length())
			lunghezzaSpot = text.length();
		lotwData = DxCluster.lotw.getLotw(qrz);
		if (lotwData.contains("-")) {
			lotw = "< LOTW <";
		}
		if (dxccn.equals("291")) {
			entita = entita.substring(0, entita.length() - 2) + DxCluster.americani.getState(qrz);
		} else if (dxccn.equals("-1")) {
			scrittore("nominativi sconosciuti", qrg + " " + line);
		}
		listaEntita.add(entita);
		listaQrz.add(qrz);
	}
	
	private void allarm(String callsign) {
		 ArrayList<String> allarms = new ArrayList<String>(Arrays.asList("IZ0KBA", "IZ0GRR", "IK0DWN","R1BIG"));
		 if (allarms.contains(callsign)) scrittore("allarmi", callsign);
	}

	/**
	 * Write line to file text
	 * 
	 * @param fileName
	 * @param text
	 * @throws IOException
	 */
	public static void scrittore(String fileName, String text) {
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".txt", true));
			writer.append(text + "\n");
			writer.close();
		} catch (IOException e1) {

		}
	}

	public static String[] getBand(double qrg) {
		String banda, modo = "X";
		if (qrg < 2000) {
			banda = "160M";
			if (qrg <= 1838) {
				modo = "CW";
			} else if (qrg <= 1843) {
				modo = "DATA";
			} else if (qrg <= 1843) {
				modo = "PHONE";
			}
		} else if (qrg < 4000) {
			banda = "80M";
			if (qrg < 3570) {
				modo = "CW";
			} else if (qrg <= 3600) {
				modo = "DATA";
			} else {
				modo = "PHONE";
			}
		} else if (qrg < 6000) {
			banda = "60M";
			if (qrg <= 5354) {
				modo = "CW";
			} else if (qrg <= 5360) {
				modo = "DATA";
			} else {
				modo = "PHONE";
			}
		} else if (qrg < 7500) {
			banda = "40M";
			if (qrg < 7035) {
				modo = "CW";
			} else if (qrg < 7050 || 7074 <= qrg && qrg < 7076) {
				modo = "DATA";
			} else {
				modo = "PHONE";
			}
		} else if (qrg < 10500) {
			banda = "30M";
			if (10100 <= qrg && qrg < 10134) {
				modo = "CW";
			} else if (10134 <= qrg) {
				modo = "DATA";
			}
		} else if (qrg < 14500) {
			banda = "20M";
			if (14000 <= qrg && qrg < 14070) {
				modo = "CW";
			} else if (14070 <= qrg && qrg < 14090) {
				modo = "DATA";
			} else if (14090 <= qrg) {
				modo = "PHONE";
			}
		} else if (qrg < 18200) {
			banda = "17M";
			if (qrg <= 18095) {
				modo = "CW";
			} else if (qrg <= 18109) {
				modo = "DATA";
			} else {
				modo = "PHONE";
			}
		} else if (qrg < 21500) {
			banda = "15M";
			if (qrg <= 21070) {
				modo = "CW";
			} else if (qrg <= 21110) {
				modo = "DATA";
			} else {
				modo = "PHONE";
			}
		} else if (qrg < 25000) {
			banda = "12M";
			if (qrg < 24915) {
				modo = "CW";
			} else if (qrg <= 24929) {
				modo = "DATA";
			} else {
				modo = "PHONE";
			}
		} else if (qrg < 30000) {
			banda = "10M";
			if (qrg <= 1838) {
				modo = "CW";
			} else if (qrg <= 28099) {
				modo = "DATA";
			} else if (qrg <= 28190) {
				modo = "PHONE";
			}
		} else if (qrg < 54000) {
			banda = "6M";
			if (qrg <= 50100) {
				modo = "CW";
			} else if (qrg <= 50316) {
				modo = "DATA";
			} else {
				modo = "PHONE";
			}
		} else {
			banda = "V-UHF";
		}
		String[] out = { banda, modo };
		return out;
	}

	public static String padRight(String s, int n) {
		return String.format("%-" + n + "s", s);
	}

	public String toString() {
		String c = String.join("/", String.valueOf(listaNewOne.size()), String.valueOf(listaEntita.size()),
				String.valueOf(listaQrz.size()), String.valueOf(contatore));
		String out = String.join(" ", padRight(text, lunghezzaSpot), lotw, entita, lotwData, c);
		return DxCluster.dxcc.getColor(dxccn, banda, modo) + out + "\u001B[0m";
	}
}

enum Modo {
	PHONE("PHONE"), CW("CW"), DATA("DATA"), IMAGE("DATA");

	private String modo;

	Modo(String modo) {
		this.modo = modo;
	}

	public String toString() {
		return modo;
	}

}
