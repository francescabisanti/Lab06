package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.tdp.meteo.DAO.MeteoDAO;


public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	List <Citta> soluzioneMigliore;
	private List <Citta> leCitta;
	double costoMigliore;
	int contoGenova=0;
	int contoTorino=0;
	int contoMilano=0;
	MeteoDAO meteo;
	public Model() {
		meteo= new MeteoDAO();
		this.leCitta=meteo.getAllCitta();
		this.soluzioneMigliore= new ArrayList <Citta>();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		Map<String, Float> mapDAO= new HashMap <String, Float>(meteo.getUmiditaMedia(mese));
		String risultato="";
		for(String s: mapDAO.keySet()) {
			risultato=risultato+ "Localita:  "+s+" UmiditaMedia: "+mapDAO.get(s)+"\n";
		}
		
		return risultato;
	}
	
	// of course you can change the String output with what you think works best
	public List <Citta> trovaSequenza(int mese) { //il metodo pubblico che ci permette di far partire la ricorsione
		List <Citta> parziale= new ArrayList<>();
		this.soluzioneMigliore= null;
		MeteoDAO dao= new MeteoDAO();
		for(Citta c: leCitta) {
			c.setRilevamenti(dao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
		cerca(parziale, 0);
		
		return this.soluzioneMigliore;
	}
	
	

	private void cerca(List<Citta> parziale, int livello) {
		if(livello==this.NUMERO_GIORNI_TOTALI) { //mi fermo quando il livello e uguale al numero di giorni totali
			double costo= calcolaCosto(parziale);
			
			
			if(soluzioneMigliore==null|| costo<calcolaCosto(soluzioneMigliore)) {// allora ho un nuovo migliore
				soluzioneMigliore= new ArrayList(parziale);
			}
			return;
		}
		
		for(Citta prova:leCitta) {
			if(aggiuntaValida(prova, parziale)) {//valuto se l'aggiunta che voglio fare ha senso
				parziale.add(prova);
				cerca(parziale, livello+1);
				parziale.remove(parziale.size()-1);
			}
		}
		
		
		
	}
	
	private boolean aggiuntaValida( Citta prova ,List <Citta>parziale) {
		int conta=0;
		for(Citta precedente: parziale) {
			if(precedente.equals(prova))
				conta++;
		}
		if(conta>=this.NUMERO_GIORNI_CITTA_MAX)
			return false; //ci siam stati troppe volte
		if(parziale.size()==0)
			return true; //ogni elemento è buono
		if(parziale.size()< this.NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
			return parziale.get(parziale.size()-1).equals(prova);
			//cioe mi baso solo sull'ultimo elemento della lista perche ancora non ho superato i 3 giorni
			//quindi devo avere 3 uguali
		}
		if(parziale.get(parziale.size()-1).equals(prova)) {
			return true; //perchè sono io che non voglio cambiare citta e quindi tutto bene
			
		}
		//se voglio cambiare citta devo verificare che negli ultimi 3 giorni devo restare nella stessa 
		for(int i=0; i<this.NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN-1; i++) {
			if(!parziale.get(parziale.size()-(i+1)).equals(parziale.get(parziale.size()-(i+2)))) {
				return false; //significa che non son stato un numero sufficientemente grande di giorni in una citta
				
			}
			
		}
		return true; //sono sopravvissuto ai casi
		
		/*giorno=parziale.size();
		
		
		for(Rilevamento r: parziale) {
			if(r.getLocalita().equals("Genova"))
				this.contoGenova++;
			if(r.getLocalita().equals("Milano"))
				this.contoMilano++;
			if(r.getLocalita().equals("Torino"))
				this.contoTorino++;
		}
		
		if(contoGenova >=this.NUMERO_GIORNI_CITTA_MAX) {
			return false;
		}
		if(contoTorino >=this.NUMERO_GIORNI_CITTA_MAX) {
			return false;
		}
		if(contoMilano >=this.NUMERO_GIORNI_CITTA_MAX) {
			return false;
		}
		boolean cambio=false;
		for(int i=0; i<giorno; i++) {
			if(parziale.size()==0) {
				cambio=true;
			}
			if(parziale.size()==1) {
				if(!parziale.get(i).getLocalita().equals(parziale.get(i-1).getLocalita()))
					return false;
				
			}
			int conto=0;
			
				if(parziale.get(i).getLocalita().equals(parziale.get(i-1).getLocalita()))
					conto++;
				if(parziale.get(i).getLocalita().equals(parziale.get(i-2).getLocalita()))
					conto++;
				if(parziale.get(i).getLocalita().equals(parziale.get(i-3).getLocalita()))
					conto++;
				if(conto>=3)
					cambio=true;
			
			if(parziale.size()>1 && conto < 3) {
				if(!parziale.get(i).getLocalita().equals(parziale.get(i-1).getLocalita()) || !parziale.get(i).getLocalita().equals(parziale.get(i-2).getLocalita()))
					return false;
			}
		}
		
		return true;*/
		
	}
	
	private double calcolaCosto(List <Citta> parziale) {
		double costo=0.0;
		for(int giorno=1; giorno<=this.NUMERO_GIORNI_TOTALI; giorno++) {
			//analizzo tutti i giorni
			Citta c= parziale.get(giorno-1); //creo una citta e la prima si trova in parziale 0
			double umid= c.getRilevamenti().get(giorno-1).getUmidita(); //cosi ottengo l'umidita per una data citta per il giorno considerato
			costo= costo+umid;
			
		}
		//ogni volta che cambio citta, devo aggiungere 100
		for(int giorno=2; giorno<= this.NUMERO_GIORNI_TOTALI; giorno++) {
			if(!parziale.get(giorno-1).equals(parziale.get(giorno-2))) {
				//se le due citta di due giorni consecutive non sono uguali allora devo aggiungere 100
				costo= costo+this.COST;
			}
		}
		return costo;
	}

	

}
