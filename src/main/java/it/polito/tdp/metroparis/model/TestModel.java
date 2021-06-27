package it.polito.tdp.metroparis.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {
		
		Model m= new Model();
		m.creaGrafo();
		
		//Mi serve l'oggetto fermata e non il nome da dare come input (partenza) nel metodo fermateRaggiungibili:
		//aggiungo un metodo sul model che dato il nome mi rimanda alla fermata corrispondente
		
		
		Fermata partenza = m.trovaFermata("La Fourche"); 
		if(partenza==null) {
			System.out.println("Fermata non trovata");
		}
		else {
//		List<Fermata> raggiungibili= m.fermateRaggiungibili(partenza);
//		System.out.println("amp: " + raggiungibili);
//		List<Fermata> raggiungibili1= m.fermateRaggiungibili(partenza);
//		System.out.println("deep: "+ raggiungibili1);
//		List<Fermata> raggiungibili2= m.fermateRaggiungibili2(partenza);
//		System.out.println( raggiungibili2);
		//stessa lista di nomi ma con ordine diverso!!
		}
		
		Fermata arrivo= m.trovaFermata("Temple");
		List<Fermata> percorso= m.trovaCammino(partenza, arrivo);
		System.out.println( percorso);
	}
 
	
}
