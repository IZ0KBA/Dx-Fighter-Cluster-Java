
public class QSL {
	public String dxccn, banda;
	public String stato = "DX";
	public boolean ft8 = false, ft4 = false;
	public Modo modo;
	
	public QSL(String record) {
		for (String line : record.split("\n")) {			
			if (line.contains("<BAND:")) {
				banda = getData(line);
			} else if (line.contains("<DXCC:"))	{
				dxccn = padLeft(getData(line));
			} else if (line.contains("<APP_LoTW_MODEGROUP:")) {
				String m = getData(line);
				modo = Modo.valueOf(m);				
			} else if (line.contains("<STATE:2>")) {
				stato = getData(line);
			} else if (line.contains("<MODE:3>FT8")) {
				ft8 = true;
			} else if (line.contains("<SUBMODE:3>FT4")) {
				ft4 = true;
			}
		}
		if (!dxccn.equals("291") & !dxccn.equals("110") & !dxccn.equals("006")) { //110 HAWAII - 6 ALASKA
			stato = "DX";
		} else if (stato.equals("DC // District of Columbia")) {
			stato = "VA // Virginia";
		}		
	}
	
	private static String getData(String line) {
		return line.substring(line.indexOf(">") + 1).strip();
	}
	
	private static String padLeft(String txt) {
		while (txt.length() < 3) txt = "0" + txt;
		return txt;
	}
}