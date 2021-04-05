public class Prove {

    public static void main(String[] args) throws Exception {
    	Entities entities = new Entities();
    	LOTW lotw = new LOTW();
    	String callsign = "IZ0KBA";
    	System.out.println(entities.getEntities(callsign));
    	System.out.println(lotw.getLotw(callsign));
    }
}