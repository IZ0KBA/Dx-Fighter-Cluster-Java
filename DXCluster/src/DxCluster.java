import java.io.*;
import java.net.*;

public class DxCluster {
	
	public static String myCall = "IZ0KBA-4";
	//public static String host = "dxfun.com"; public static int port = 8000;
	public static String host = "telnet.reversebeacon.net"; public static int port = 7001;
	
	private static Socket s = new Socket();
	private static PrintWriter s_out = null;
	private static BufferedReader s_in = null;
	
	public static Award award;
	public static LOTW lotw;
	public static Entities entities;
	public static Americani americani;
	public static DXCC dxcc;

	public static void main(String[] args) throws IOException {
		award = new Award("lotwreport.adi");
		lotw = new LOTW();
		entities = new Entities();
		americani = new Americani();
		dxcc = new DXCC(award.conferme);

		try {
			System.out.println("Tentativo di connesione a: " + host);
			s.connect(new InetSocketAddress(host, port));
			s_out = new PrintWriter(s.getOutputStream(), true);// writer for socket
			s_in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));// reader for socket
			s_out.println("IZ0KBA\n");
		} catch (final UnknownHostException e) {
			System.err.println("Impossibile connettersi all'Host " + host);
		} finally {
			System.out.println("Connessione stabilita!");
		}
		
		String response;
		while ((response = s_in.readLine()) != null) {
			for (String line : response.split("\n")) {
				if (line.startsWith("DX de ")) {
					Spot spot = new Spot(line);
					if (!spot.toString().contains("\u001B[37m"))
						System.out.println(spot.toString());	
				} else {
					System.out.println(line );
					if (line.contains("your call"))
						s_out.println("IZ0KBA\n");
				}
			}
		}
	}
	
}
