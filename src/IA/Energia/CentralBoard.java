
package IA.Energia;

import IA.Energia.Cliente;
import IA.Energia.Clientes;
import IA.Energia.Centrales;
import IA.Energia.VEnergia;

public class CentralBoard{

    private static Clientes listClient;
    private static Centrales listCentral;
    private static double[][] dist;

    private int[] asignaClientToCentral;
    private double ingresos;
    private double[] energiaServida;

        /* Constructor */
    public CentralBoard(int asign,
    					Centrales listCentral,
    					Clientes listClient) {
        CentralBoard.listCentral = listCentral;
        CentralBoard.listClient = listClient;            
        
        ingresos = 0;        
        
        dist = new double[listClient.size()][listCentral.size()];
        calculateAllDistClientToCentral(dist, listClient, listCentral);
        
        asignaClientToCentral = new int[listClient.size()];
        for (int i = 0; i < asignaClientToCentral.length; ++i) this.asignaClientToCentral[i] = -1;
        
        energiaServida = new double[listCentral.size()];
        for (int i = 0; i < energiaServida.length; ++i) energiaServida[i] = 0.0;
            
        if (asign == 0) { asignacion((double) 3/5); }
        else if (asign == 1) { asignacion((double) 3/4); }
        else if (asign == 2) { asignacion((double) 4/5); }

        System.out.println("\nBoard Start Ready...!");
    }    
    
    public CentralBoard(CentralBoard board) {
        asignaClientToCentral = board.getAsignaClientToCentral().clone();
        ingresos = board.getIngresos();
        //rentabilidadCentralEachOne = board.getRentabilidadCentralEachOne().clone();
        energiaServida = board.getEnergiaServida().clone();
        
    }
    
    private void asignacion(double fraccionAsignacion) { 
    	double fraccion = fraccionAsignacion * listClient.size();
    	int totalAsignacion = (int) fraccion; 	
    	int asignados = 0;  
    	
    	// Asignaci�n de los garantizados y marca como no asignados a los no garantizados.
    	for(int i = 0; i < listClient.size(); ++i) {    			
    		if (listClient.get(i).getContrato() == Cliente.GARANTIZADO) {
    			boolean asignado = false;
    			int j = 0;
    			while (j < listCentral.size() && !asignado) {
    				asignado = asignarCentral(i, listCentral.size()-1-((i+j)%listCentral.size()));
    				if (asignado) ++asignados;    				
    				++j;
    			}    			
    			
            }
    		
    	}
    	
    	// Asigna los no garantizados si hay hueco dentro del l�mite de clientes asignados iniciales
        for(int i = 0; i < listClient.size() && asignados < totalAsignacion; ++i) {
        	if (listClient.get(i).getContrato() == Cliente.NOGARANTIZADO) {        		
        		boolean asignado = false;
    			int j = 0;    			
    			while (j < listCentral.size() && !asignado) {
    				asignado = asignarCentral(i, listCentral.size()-1-((i+j)%listCentral.size()));
    				if (asignado) ++asignados;
    				++j;
    			}       	       		 
            	
            }            
        	
        }
        
    }
    
    public boolean puedeAsignar(int i, int j) {				
		return ((energiaServida[j] + energiaServida(i, j)) < listCentral.get(j).getProduccion());
			
    }
    
    public boolean asignarCentral(int i, int j) {
    	boolean asignado = false;
    	if (asignaClientToCentral[i] < 0) {
    		double consumoNuevo = energiaServida(i, j);
    		if (puedeAsignar(i, j)) {
    			asignado = true;    		    			
    			double tarifa = 0.0;
    			if (listClient.get(i).getContrato() == Cliente.GARANTIZADO) {
    				try { tarifa = VEnergia.getTarifaClienteGarantizada(listClient.get(i).getTipo()); } catch (Exception ex) {};
    			} else {
    				try { tarifa = VEnergia.getTarifaClienteNoGarantizada(listClient.get(i).getTipo()); } catch (Exception ex) {};
    			}    	
    			
    			asignaClientToCentral[i] = j;
    			ingresos += listClient.get(i).getConsumo() * tarifa;
    			energiaServida[j] += consumoNuevo;		
    		
    		}
    	}  	
    	return asignado;
    }
    
    public void quitaAsignacion(int i) {
    	if (asignaClientToCentral[i] >= 0) {
    		energiaServida[asignaClientToCentral[i]] -= energiaServida(i, asignaClientToCentral[i]);
    		if (energiaServida[asignaClientToCentral[i]] < 0.9) energiaServida[asignaClientToCentral[i]] = 0.0;
    		double tarifa = 0.0;
    		if (listClient.get(i).getContrato() == Cliente.GARANTIZADO) {
    			try { tarifa = VEnergia.getTarifaClienteGarantizada(listClient.get(i).getTipo()); } catch (Exception ex) {};
    		} else {
    			try { tarifa = VEnergia.getTarifaClienteNoGarantizada(listClient.get(i).getTipo()); } catch (Exception ex) {};
    		}
    		ingresos -= listClient.get(i).getConsumo() * tarifa;
    		asignaClientToCentral[i] = -1;
    	}
    	
    }
    
    public boolean cambioCentral(int i, int j) {
    	quitaAsignacion(i);
    	return asignarCentral(i, j);
    }
    
    private double energiaServida(int cli, int cent) {			
		double perdidaDistancia = VEnergia.getPerdida(dist[cli][cent]);
		double demandaCliente = listClient.get(cli).getConsumo(); 
		return (demandaCliente * (1.0+perdidaDistancia));
	}    
    
    public boolean reasignarClienteEnMarcha(int i) {
    	boolean reasignado = false;
    	for (int j = 0; j < listCentral.size() && !reasignado; ++j) {    		
    		if (puedeAsignar(i, j) && energiaServida[j] >= 1.0) {
    			reasignado = true;
    			quitaAsignacion(i);
    			asignarCentral(i, j);    			
    		}
    	}
    	return reasignado;
    }
    
    public Double getEnergiaPerdida() {
    	double energiaTotalServida = 0.0;
    	double energiaTotalProducida = 0.0;
    	for (int j = 0; j < listCentral.size(); ++j) {
    		energiaTotalServida += energiaServida[j];
    		energiaTotalProducida += listCentral.get(j).getProduccion();
    	}
    	return energiaTotalProducida - energiaTotalServida;
    }
    
    
    public double getBeneficios() {
		double mWNoServidos = 0.0;
		for (int i = 0 ; i < listClient.size(); ++i) {	
    		if (asignaClientToCentral[i] < 0) {	mWNoServidos += listClient.get(i).getConsumo();	}    		
		}
		double indemnizaciones = mWNoServidos * 5.00;
		double costesProduccion = 0.0;				
		for (int j = 0; j < listCentral.size(); ++j) {
			int tipo = listCentral.get(j).getTipo();
			if (energiaServida[j] > 0.0 ) {
				try { costesProduccion += (listCentral.get(j).getProduccion() * VEnergia.getCosteProduccionMW(tipo))
						+ VEnergia.getCosteMarcha(tipo); } catch (Exception e) {}				
			} else {
				try { costesProduccion += VEnergia.getCosteParada(tipo); } catch (Exception e) {}
			}
		}
		
		return (ingresos - costesProduccion - indemnizaciones);	
		
	} 
    
    
    private static double euclidea(double cnx, double cny, double clx, double cly) {
        return (double)(Math.sqrt(Math.pow(cnx - clx, 2.0) + Math.pow(cny - cly, 2.0)));
    }
    
    
    private static void calculateAllDistClientToCentral(double[][] dist, Clientes listClient, Centrales listCentral) {
        for (int i = 0; i < listClient.size(); ++i) {
            Cliente cl = listClient.get(i);
            for (int j = 0; j < listCentral.size(); ++j) {
                Central ce = listCentral.get(j);
                dist[i][j] = euclidea(cl.getCoordX(), cl.getCoordY(), ce.getCoordX(),ce.getCoordY());
            }
        }
    }
    

    public static Centrales getListCentral() { return CentralBoard.listCentral; }

    public static Clientes getListClient() { return CentralBoard.listClient; }

    public int[] getAsignaClientToCentral() { return this.asignaClientToCentral; }

    public double[] getEnergiaServida() { return this.energiaServida; }

    public static double[][] getDist() { return CentralBoard.dist; }
    
    public int getAsignado(int i) { return asignaClientToCentral[i]; }
    
    public int getNClientes() { return listClient.size(); }
    
    public int getNCentrales() { return listCentral.size(); }
    
    public boolean isGoal() { return false; }
    
    public double getIngresos() { return ingresos; }
    
    public boolean getEnMarcha(int j) { return energiaServida[j] >= 0.0;  }
    
    public Cliente getCliente(int i) { return listClient.get(i);  }
    
    public void apagarCentral(int j) { energiaServida[j] = 0.0; }   
   
}
