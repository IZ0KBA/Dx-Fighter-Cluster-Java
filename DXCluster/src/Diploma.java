
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Diploma {

	private HashMap<String, HashSet<String>> cw = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> phone = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> ft8 = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> ft4 = new HashMap<String, HashSet<String>>();
	
	/**
	 * Load the QSL list
	 * @param filesorgente LOTW report File
	 * @return 
	 * @throws IOException 
	 */
	public static List<QSL> loadQsl(String fileSorgente) throws IOException {
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
	    List<QSL> conferme = new ArrayList<QSL>();

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
		return conferme;
	}

}


