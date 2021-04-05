import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;

public class Collezioni {

	public static void main(String[] args) {
		HashMap<String, Oggetto> collezione1 = new HashMap<String, Oggetto>();
		Hashtable<String, Oggetto> collezione2 = new Hashtable<String, Oggetto>();
		
		String[] nomi = {"Nina", "Teddy", "Nina", "Nina", "Teddy", "Nina", "Isotta"};
		
		/*collezione1.put("Zoe", 2016);
		collezione2.put("Zoe", 2016);
		collezione1.put("Nina", 2011);
		collezione2.put("Nina", 2011);
		collezione1.put("Teddy",2007);
		collezione2.put("Teddy", 2007);
		collezione1.put("Isotta",2020);
		collezione2.put("Isotta", 2020);*/
		
		
		for (String nome : nomi) {
			System.out.println("Controllo " + nome + "... ");
			if (collezione1.containsKey(nome)) {
				collezione1.get(nome).add();
				System.out.println("OK " + collezione1.get(nome).count);
			} else {
				System.out.println("not in collection1");
				collezione1.put(nome, new Oggetto(nome));
			}
			if (collezione2.containsKey(nome)) {
				collezione2.get(nome).add();
				System.out.println("OK " + collezione2.get(nome).count);
			} else {
				System.out.println("not in collection2");
				collezione2.put(nome, new Oggetto(nome));
			}
			System.out.println();
		}
		
		//Collections.sort(collezione2);
		System.out.println("Collezione 1: " + collezione1);
		System.out.println("Collezione 2: " + collezione2);
		
		for (Entry<String, Oggetto> me : collezione1.entrySet()) {
			System.out.println(me.getKey() + " " + me.getValue().count);
		}
		
		int[] arr = {1,2,3,4,5,6,0};
		for (int i : arr) {
			System.out.println(i);
		}
		
	}

}

class Oggetto {
	public String nome;
	public int count = 0;
	
	public Oggetto(String nome) {
		this.nome = nome;
		add();
	}
	
	public void add() {
		count ++;
	}
}