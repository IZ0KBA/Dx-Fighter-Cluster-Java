import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.HashMap;

public class LOTW {

	private HashMap<String, String> lotwUsers = new HashMap<String, String>();

	public LOTW() throws FileNotFoundException {
		File lotwUserFile = new File("lotw-user-activity.csv");
		Scanner sc1 = new Scanner(lotwUserFile);
		while (sc1.hasNextLine()) {
			String[] riga = sc1.nextLine().split(",");
			lotwUsers.put(riga[0], riga[1]);
		}
		sc1.close();
	}

	public String getLotw(String callsign) {
		callsign = callsign.toUpperCase();
		String data = "          ";
		if (lotwUsers.containsKey(callsign)) {
			data = lotwUsers.get(callsign).split(",")[0];
			try {
				data += " " + dateDiff(data);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public static String dateDiff(String data) throws ParseException {
		Date now = new Date();
		Date a = new SimpleDateFormat("yyyy-MM-dd").parse(data);
		long diff = now.getTime() - a.getTime();
		int age = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		if (age > 365) {
			return ">" + (age / 365) + "y";
		} else {
			return String.valueOf(age);
		}
	}
}
