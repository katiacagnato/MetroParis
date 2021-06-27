package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	Graph<Fermata, DefaultEdge> grafo= new SimpleGraph<>(DefaultEdge.class);
	Map<Fermata, Fermata> predecessore ; 
	//come chiave ha l'elemento scoperto e come valore l'elemento da cui e stato scoperto
	
	public void creaGrafo() {
		
		MetroDAO dao = new MetroDAO();
		List <Fermata> fermate= dao.getAllFermate();
		
		/**for(Fermata f: fermate) {
			this.grafo.addVertex(f);
			}
			
			
			 * 	   Nel 99% dei casi dovro fare cosi per comporre un grafo:
			 * 		itero le ompoenti di una collection e le aggiungo al grafo.
			 * Posso anche scrivere:
			 */  Graphs.addAllVertices(this.grafo, fermate);
			
		
		//Aggiungiamo gli archi
		
	/** I DUE CICLI PERO HANNO UNA COMPLESSITà MOLTO ALTA SE HO TANTI VERTICI
	 *  (SE HO PIU DI 50 VERTICI)
	 
		 for(Fermata f1: this.grafo.vertexSet()) {
			 for(Fermata f2: this.grafo.vertexSet()) {
				 //questi due cicli for mi danno tutte i possibili collegamenti tra stazioni 
				 if(!f1.equals(f2) && dao.fermateCollegate(f1, f2)){ //mi chiedo se le due fermate sono uguali e se sono collegate (query sul db)
					 this.grafo.addEdge(f1, f2);
				 }
			 }
		 }
	*/
	List<Connessione> connessioni = dao.getAllConnessioni(fermate);
	for(Connessione c: connessioni) {
		this.grafo.addEdge(c.getStazP(), c.getStazA());
	}
		
		System.out.println(this.grafo);
	}
	
	
/*	Fermata f;
	Set<DefaultEdge> archi = this.grafo.edgesOf(f);
	for(DefaultEdge e: archi) {
		Fermata f1= this.grafo.getEdgeSource(e);
		Fermata f2= this.grafo.getEdgeSource(e);
			if(f1.equals(f)) {
					mi serve f2
				}
			else {
					mi serve f1
				}
	}
	f1= Graphs.getOppositeVertex(this.grafo, e, f);	

	List<Fermata> fermateAdiacenti= Graphs.successorListOf(this.grafo, f);
*/	
/**
 * Metodo che mi da tutti i vertici raggiungibili a partire da un vertice
 */
/*	public List<Fermata> fermateRaggiungibili(Fermata partenza){
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(this.grafo, partenza);  
//uso una delle tre versioni (vedi la documentazione) : quella in cui il primo paramentro e il grafo e il secondo il vertice di partenza
		List<Fermata> result = new ArrayList<>();
		
		while(bfv.hasNext()) { //finche il metodo next e vero ho dei vertici da aggiungere
			Fermata f= bfv.next();
			result.add(f);
		}
		
	return result;
	}
	public List<Fermata> fermateRaggiungibili1(Fermata partenza){
		DepthFirstIterator<Fermata, DefaultEdge> dfv = new DepthFirstIterator<>(this.grafo, partenza);  
//uso una delle tre versioni (vedi la documentazione) : quella in cui il primo paramentro e il grafo e il secondo il vertice di partenza
		List<Fermata> result = new ArrayList<>();
		
		while(dfv.hasNext()) { //finche il metodo next e vero ho dei vertici da aggiungere
			Fermata f= dfv.next();
			result.add(f);
		}
		
	return result;
	}
*/	
	public Fermata trovaFermata (String nome) {
		for(Fermata f: this.grafo.vertexSet()) {
			if(f.getNome().equals(nome))
			{	return f;}
		}
	return null;
	}
	
	/**
	 * Con l'iteratore troviamo la lista di fermate raagiugibili, ma
	 * come posso invece trovare il percorso da una fermata ad un'altra?
	 * Man mano che l'iteratore lavora genera degli eventi; se sono interessato a
	 * qualcuno di questi eventi li poso intercettare definendo l'oggetto di
	 * tipo TraversalListener<V,E>
	 */
	
	public List<Fermata> fermateRaggiungibili2(Fermata partenza){
		BreadthFirstIterator<Fermata, DefaultEdge> bfv = new BreadthFirstIterator<>(this.grafo, partenza); 
		this.predecessore=new HashMap<>();
		this.predecessore.put(partenza, null);
		
		bfv.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>() {

			/**
			 * nei vari metodi chiamati in automatico l'algoritmo passa un paramentro per
			 * descrivere cos'e successo: passa un evento che descrive cos'e successo
			 */
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> e) {
				DefaultEdge arco= e.getEdge();
				Fermata a= grafo.getEdgeSource(arco); //NB!! source e target vanno prese dall'edge(arco) e non dall'oggetto evento
				Fermata b= grafo.getEdgeTarget(arco);
			//ho i due estremi dell'arco: uno e il vertice nuovo e uno il precedente (i nomi source e target sono indicativi: dipende da coeme e costruito il grafico)
		//ho solo due casi: ho scoperto a arrivando da b(se b lo conosevo gia) o viceversa
				if(predecessore.containsKey(b) && !predecessore.containsKey(a)) {
					//di sicuro b è il vertice sorgente
					predecessore.put(a, b);
		//			System.out.println(a+ " scoperto da " + b);
				}
				else if(predecessore.containsKey(a)&& !predecessore.containsKey(b)) {
					//se non conoscevo b allora conoscevo a : ho scoperto b
					predecessore.put(b, a);
			//		System.out.println(b+ " scoperto da " + a);
				}
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> e) {
	/**			
	 * Metodo meno efficente del precedente edgeTraversed
	 
				Fermata nuova=(e.getVertex());
				Fermata precedente = null;
				predecessore.put(nuova,precedente);
	*/
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> e) {
			}
			
		});
		
		
//uso una delle tre versioni (vedi la documentazione) : quella in cui il primo paramentro e il grafo e il secondo il vertice di partenza
		List<Fermata> result = new ArrayList<>();
		
		while(bfv.hasNext()) { //finche il metodo next e vero ho dei vertici da aggiungere
			Fermata f= bfv.next();
			result.add(f);
		}
		
	return result;
	}
	
	public List<Fermata> trovaCammino(Fermata partenza, Fermata arrivo){
		fermateRaggiungibili2(partenza); //viene costruita la mappa dei predecesori
		List<Fermata> result = new LinkedList<>();
		List<Fermata> result1 = new LinkedList<>();

		result.add(arrivo);
		result1.add(arrivo);
		Fermata f= arrivo;
		while(predecessore.get(f)!=null) { //al posto che fare tutta la mappa potevo usare getparent(f)
			
			f= predecessore.get(f);
			result.add(0,f);
		
		}
		return result; //in ordine inverso
		
	}
		public List<Fermata> trovaCammino2(Fermata partenza, Fermata arrivo) {
			BreadthFirstIterator<Fermata, DefaultEdge> bfv = 
					new BreadthFirstIterator<Fermata, DefaultEdge>(this.grafo, partenza) ;
			
			// fai lavorare l'iteratore per trovare tutti i vertici
			while(bfv.hasNext())
				bfv.next() ; // non mi serve il valore
			
			List<Fermata> result = new LinkedList<>() ;
			Fermata f = arrivo ;
			while(f!=null) {
				result.add(f) ;
				f = bfv.getParent(f) ;
			}
			
			return result ;
		}

		//Ma se nell'arco ci sono dei pesi diversi?? Come trovo il cammino minimo? 
		//Potrei avere un percorso che è piu veloce pur avendo più tratte (archi)
		
		
	
}










