package it.polito.tdp.poweroutages.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.tdp.poweroutages.DAO.PowerOutageDAO;

public class Model {

	private PowerOutageDAO podao;

	private NercIdMap nercIdMap;
	private List<Nerc> nercList;

	private List<PowerOutages> eventList;
	private List<PowerOutages> eventListFiltered; //lista di eventi filtrata su quelli della regione NERC selezionata
	private List<PowerOutages> solution;

	private int maxAffectedPeople;

	public Model() {
		podao = new PowerOutageDAO();

		nercIdMap = new NercIdMap();
		nercList = podao.getNercList(nercIdMap);
		
		eventList = podao.getPowerOutageList(nercIdMap);
	}

	//funzione che si occupa della ricorsione
	//ha come dati i parametri ricavati da controller
	public List<PowerOutages> getWorstCase (int maxNumberOfYears, int maxHoursOfOutage, Nerc nerc){
		
		//fase di inizializzazione
		solution = new ArrayList<>();
		maxAffectedPeople = 0;

		//crea una nuova lista di eventi filtrata
		eventListFiltered = new ArrayList<>();

		for(PowerOutages event : eventList) {
			//prendiamo solo gli eventi riferiti al nerc scelto
			if(event.getNerc().equals(nerc)) {
				eventListFiltered.add(event);
			}
		}

		Collections.sort(eventListFiltered);

		recursive(new ArrayList<PowerOutages>(), maxNumberOfYears, maxHoursOfOutage);
		return solution;
	}

	public int sumAffectedPeople(List<PowerOutages> partial) {
		int sum = 0;

		for(PowerOutages event : partial) {
			sum += event.getAffectedPeople();
		}
		return sum;
	}

	public boolean checkMaxYears(List<PowerOutages> partial, int maxNumberOfYears) {
		if(partial.size() >= 2) {
			int y1 = partial.get(0).getYear();
			int y2 = partial.get(partial.size()-1).getYear();

			if((y2 - y1 + 1) > maxNumberOfYears) {//+1 perchè parto da zero
				return false;
			}
		}
		return true; //altrimenti sappiamo già che è vero perchè ci sarebbe solo un anno
	}

	public int sumOutageHours(List<PowerOutages> partial) {
		int sum = 0;
		for(PowerOutages event : partial) {
			sum += event.getOutageDuration();
		}
		return sum;
	}

	private boolean checkMaxHoursOfOutage(List<PowerOutages> partial, int maxHoursOfOutage) {
		int sum = sumOutageHours(partial);
		if(sum > maxHoursOfOutage) {
			return false;
		}
		return true;
	}
	
	private void recursive(List<PowerOutages> partial, int maxNumberOfYears, int maxHoursOfOutage) {
		//Aggiorna la soluzione migliore
		if(sumAffectedPeople(partial) > maxAffectedPeople) {
			maxAffectedPeople = sumAffectedPeople(partial);
			solution = new ArrayList<PowerOutages>(partial);
		}

		//Ci fermiamo quando vagliamo tutte le possibili soluzioni
		for(PowerOutages event : eventListFiltered) {
			//La soluzione parziale non deve contenere lo stesso evento
			if(!partial.contains(event)) {
				
				partial.add(event);

				// ha senso ricorrere quando rispettiamo il massimo numero di anni e di ore
				if(checkMaxHoursOfOutage(partial, maxHoursOfOutage) && checkMaxYears(partial, maxNumberOfYears)) {
					recursive(partial, maxNumberOfYears, maxHoursOfOutage);
				}
				
				partial.remove(event);
			}
		}
	}

	public List<Nerc> getNercList() {
				return this.nercList;
	}
	
	public List<Integer> getYearList(){
		//non contiene elementi duplicati di anno
		Set<Integer> yearSet = new HashSet<Integer>();
		for(PowerOutages event : eventList) {
			yearSet.add(event.getYear());
		}
		List<Integer> yearList = new ArrayList<Integer>(yearSet);
		yearList.sort(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
			
		});
		return yearList; //lista di anni relativi ad un dato nerc
	}

}
