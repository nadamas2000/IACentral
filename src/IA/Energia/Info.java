package IA.Energia;

public class Info {
	
	private static Clientes listClient;
    private static Centrales listCentral;

    private static int[] asignaClientToCentral;
    
    private static int seed;
	
	public Info (CentralBoard board, int s) {
		listClient = CentralBoard.getListClient();
		listCentral = CentralBoard.getListCentral();
		asignaClientToCentral = board.getAsignaClientToCentral();
		seed = s;
		
	}
	
	
	public static void stateCentrals() {
		System.out.println("------------- Estado de las centrales -------------");
    	for (int j = 0; j < listCentral.size(); ++j) {
        	double clientesCentral = 0;
        	for (int i = 0; i < listClient.size(); ++i) {
        		if (asignaClientToCentral[i] == j) clientesCentral += (double) listClient.get(i).getConsumo();
        	}
        	System.out.println("Producción de Central " + j + ": " + listCentral.get(j).getProduccion()
        			+ " MW	|	Consumen: " + (double)Math.round(clientesCentral * 1000d) / 1000d
        			+ " MW	|	Disponibles: " + (double)Math.round((listCentral.get(j).getProduccion()-clientesCentral) * 1000d) / 1000d
        			+ " MW	|	Eficiencia: " + (double)Math.round(clientesCentral/listCentral.get(j).getProduccion() * 1000d) / 1000d);
        }
    	System.out.println("---------------------------------------------------");
    	System.out.println("");
    }
	
	
	public static void infoEstat() {
		double demandaTotal = 0.0;
		double mWNoServidos = 0.0;
		double ingresos = 0.0;
		int clientesNoAsignados = 0;
		double clientesGarantizados = 0;
		double clientesGarantAsign = 0;
		for (int i = 0 ; i < listClient.size(); ++i) {
			if (listClient.get(i).getContrato() == Cliente.GARANTIZADO) ++clientesGarantizados;
			double consumoCli = listClient.get(i).getConsumo();
    		demandaTotal = demandaTotal + consumoCli;
    		if (asignaClientToCentral[i] < 0) {
    			mWNoServidos += consumoCli;
    			++clientesNoAsignados;    			
        	} else {
        		double tarifa = 0;
        		if (listClient.get(i).getContrato() == Cliente.GARANTIZADO){
        			try { tarifa = VEnergia.getTarifaClienteGarantizada(listClient.get(i).getTipo()); } catch (Exception ex) {};
        			++clientesGarantAsign;
        		} else {
        			try { tarifa = VEnergia.getTarifaClienteNoGarantizada(listClient.get(i).getTipo()); } catch (Exception ex) {};
        		}
        		ingresos += consumoCli * tarifa;
        	}
    		
		}
		double indemnizaciones = mWNoServidos * 5.00;
		boolean[] centralesMarcha = new boolean[listCentral.size()];
		for (int j = 0; j < listCentral.size(); ++j) { centralesMarcha[j] = false; }
		for (int i = 0; i < listClient.size(); ++i) {
			if (asignaClientToCentral[i] >= 0) centralesMarcha[asignaClientToCentral[i]] = true; 
        }
		double costesProduccion = 0.0;
		int nMarcha = 0;		
		for (int j = 0; j < listCentral.size(); ++j) {
			int tipo = listCentral.get(j).getTipo();
			if (centralesMarcha[j] == true) {
				try {
					costesProduccion += (listCentral.get(j).getProduccion() * VEnergia.getCosteProduccionMW(tipo)) + VEnergia.getCosteMarcha(tipo);
				} catch (Exception e) {}
				++nMarcha;
			} else {
				try {
					costesProduccion += VEnergia.getCosteParada(tipo);
				} catch (Exception e) {}
			}
		}
		System.out.println("------------- Estado del tablero -------------");
		System.out.println("Clientes asignados: " + (listClient.size()-clientesNoAsignados)
				+ " | Clientes no asignados: " + clientesNoAsignados);
		System.out.println("Clientes garantizados asignados: " + (double)Math.round(((clientesGarantAsign/clientesGarantizados)*100) * 1000d) / 1000d
				+ "% | Clientes garantizados no asignados: " + ((int) (clientesGarantizados - clientesGarantAsign)));
		System.out.println("Centrales en marcha: "+ nMarcha
				+ " | Centrales paradas: "+ (listCentral.size()-nMarcha));
		
		System.out.println("Demanda Total: " + (double)Math.round(demandaTotal * 1000d) / 1000d
				+ " MW | Energía servida: " + (double)Math.round((demandaTotal - mWNoServidos) * 1000d) / 1000d
				+ " MW | Demanda no servida: " + (double)Math.round(mWNoServidos * 1000d) / 1000d + " MW");		
		System.out.println("Ingresos: " + (double)Math.round(ingresos * 1000d) / 1000d
				+ "€ | Costes de producción: " + (double)Math.round(costesProduccion * 1000d) / 1000d
				+ "€ | Indemnizaciones: " + (double)Math.round(indemnizaciones * 1000d) / 1000d + "€");
		System.out.println("Beneficios: " + (double)Math.round((ingresos - costesProduccion - indemnizaciones) * 1000d) / 1000d);
		System.out.println("----------------------------------------------");
		System.out.println("");
		
		
	}
    
	
	public static void infoSeed() {		
    	double demandaTotal = 0.0;
    	int contGarant = 0;
    	double prodMax = 0.0;
    	for (int i = 0 ; i < listClient.size(); ++i) {
    		demandaTotal = demandaTotal + listClient.get(i).getConsumo();
    		if (listClient.get(i).getContrato() == Cliente.GARANTIZADO) {
    			++contGarant;
    		}
    	}
    	for (int i = 0 ; i < listCentral.size(); ++i) {
    		prodMax += listCentral.get(i).getProduccion();
    	}
    	System.out.println("------ Información de Semilla ------");
    	System.out.println("Semilla: " + seed);
        System.out.println("Producción máxima(Mw): " + (double)Math.round(prodMax * 1000d) / 1000d);
        System.out.println("Demanda total(Mw): " + (double)Math.round(demandaTotal * 1000d) / 1000d);
        System.out.println("Contratos Garantizados: " + contGarant);
        System.out.println("------------------------------------");
        System.out.println("");
    }
	
	
	public static double getBeneficios() {
		double demandaTotal = 0.0;
		double mWNoServidos = 0.0;
		double ingresos = 0.0;
		for (int i = 0 ; i < listClient.size(); ++i) {
			double consumoCli = listClient.get(i).getConsumo();
    		demandaTotal = demandaTotal + consumoCli;
    		if (asignaClientToCentral[i] < 0) {	mWNoServidos += consumoCli;	}
    		else {
        		double tarifa = 0;
        		if (listClient.get(i).getContrato() == Cliente.GARANTIZADO){
        			try { tarifa = VEnergia.getTarifaClienteGarantizada(listClient.get(i).getTipo()); } catch (Exception ex) {};
        		} else {
        			try { tarifa = VEnergia.getTarifaClienteNoGarantizada(listClient.get(i).getTipo()); } catch (Exception ex) {};
        		}
        		ingresos += consumoCli * tarifa;
        	}
    		
		}
		double indemnizaciones = mWNoServidos * 5.00;
		boolean[] centralesMarcha = new boolean[listCentral.size()];
		for (int j = 0; j < listCentral.size(); ++j) { centralesMarcha[j] = false; }
		for (int i = 0; i < listClient.size(); ++i) {
			if (asignaClientToCentral[i] >= 0) centralesMarcha[asignaClientToCentral[i]] = true; 
        }
		double costesProduccion = 0.0;				
		for (int j = 0; j < listCentral.size(); ++j) {
			int tipo = listCentral.get(j).getTipo();
			if (centralesMarcha[j] == true) {
				try { costesProduccion += (listCentral.get(j).getProduccion() * VEnergia.getCosteProduccionMW(tipo))
						+ VEnergia.getCosteMarcha(tipo); } catch (Exception e) {}				
			} else {
				try { costesProduccion += VEnergia.getCosteParada(tipo); } catch (Exception e) {}
			}
		}
		
		return (double)Math.round((ingresos - costesProduccion - indemnizaciones) * 1000d) / 1000d;	
		
	}
	
	
}
