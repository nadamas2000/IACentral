
import IA.Energia.CentralBoard;
import IA.Energia.CentralSuccessorFunction;
import IA.Energia.CentralSuccessorFunction2;
import IA.Energia.CentralHeuristicFunction;
import IA.Energia.CentralHeuristicFunction2;
import IA.Energia.CentralGoalTest;

import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.framework.SuccessorFunction;
import aima.search.framework.HeuristicFunction;
import aima.search.framework.GoalTest;
import aima.search.informed.SimulatedAnnealingSearch;
import aima.search.informed.HillClimbingSearch;

import IA.Energia.Clientes;
import IA.Energia.Info;
import IA.Energia.Centrales;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Main {
	
	private static final int nAsign = 3;
	private static final int nHeur = 2;
	private static final int nOper = 2;
	public static double[][][] tableResultHC = new double[nAsign][nHeur][nOper];
    public static long[][][] timeTableHC = new long[nAsign][nHeur][nOper];
    public static double[][][] tableResultSA = new double[nAsign][nHeur][nOper];
    public static long[][][] timeTableSA = new long[nAsign][nHeur][nOper];

    public static void main(String[] args) throws Exception {                

        for (int asign = 0; asign < nAsign; ++asign) {
            for (int heur = 0; heur < nHeur; ++heur) {
                for (int oper = 0; oper < nOper; ++oper) {

                    System.out.println("\nSettings -> Asignacion: " + (asign+1) +
                                       ", Heuristica: " + (heur+1) + ", Operador: " + (oper+1) );

                    int numClient = 1000;
                    int[] tipoCentral = {5, 10, 25}; // A, B, C
                    double[] tipoCliente = {0.25, 0.3, 0.45}; // XG, MG, G
                    double proporcionClientGarant = 0.75;
                    int seed = 1234;

                    Centrales listCentral = new Centrales(tipoCentral, seed);
                    Clientes listClient = new Clientes(numClient, tipoCliente, proporcionClientGarant, seed);                          

                    CentralBoard board = new CentralBoard(asign, listCentral, listClient);
                    
                    new Info(board, seed);
                    Info.infoSeed();                    
                    Info.infoEstat();
                    Info.stateCentrals();

                    /*** Las que tenemos que implementar son Hill Climbing y el Simulated Annealing ***/
                        
                        Hill_Climbing_Search(board, seed, asign, heur, oper);
                        
                        Simulated_Annealing_Search(board, 2000, 100, 5, 0.001, seed, asign, heur, oper);
                                                        // n√∫mero max iter. (2000)
                                                        // n√∫mero de iter. (100)
                                                        // por cada paso de temperutra (5)
                                                        // k y lambda comportamiento de temperatura (0.001)
                    
                        
                }
            }
        }

            // Output
        System.out.println("Resultados Hill Climbing"); 
        for (int asign = 0; asign < nAsign; ++asign) {
            for (int heur = 0; heur < nHeur; ++heur) {
                for (int oper = 0; oper < nOper; ++oper) {
                    String num = String.format("%.1f", tableResultHC[asign][heur][oper]);
                    System.out.println("Rentabilidad Centrales -> Asignacion: " + (asign+1) +
                                       ", Heuristica: " + (heur+1) + ", Operador: " + (oper+1) +
                                       ", Max.Rentabilidad: " + num + "Ä" +
                                       ", Time(ms): " + timeTableHC[asign][heur][oper]);
               }
           }
        }
        System.out.println("\nResultados Simulated Annealing");
        for (int asign = 0; asign < nAsign; ++asign) {
            for (int heur = 0; heur < nHeur; ++heur) {
                for (int oper = 0; oper < nOper; ++oper) {
                    String num = String.format("%.1f", tableResultSA[asign][heur][oper]);
                    System.out.println("Rentabilidad Centrales -> Asignacion: " + (asign+1) +
                                       ", Heuristica: " + (heur+1) + ", Operador: " + (oper+1) +
                                       ", Max.Rentabilidad: " + num + "Ä" +
                                       ", Time(ms): " + timeTableSA[asign][heur][oper]);
               }
           }
        }        
    }

    

        

    /*** Las que tenemos que implementar son Hill Climbing y el Simulated Annealing ***/

    private static void Hill_Climbing_Search(CentralBoard board, int seed, int asign, int heur, int oper) {
        System.out.println("\nHill Climbing -->");
        GoalTest gTest = new CentralGoalTest();
        HeuristicFunction heurFunc = new CentralHeuristicFunction();
        if (heur == 1) heurFunc = new CentralHeuristicFunction2();
        SuccessorFunction succFunc = new CentralSuccessorFunction();
        if (oper == 1) succFunc = new CentralSuccessorFunction2();
        
        try {  
        	
        	Problem problem =  new Problem(board, succFunc,	gTest,	heurFunc);
        	Search search =  new HillClimbingSearch();
        	
        	long startTime = System.nanoTime();
        	SearchAgent agent = new SearchAgent(problem, search);
        	long endTime = System.nanoTime();
        	
        	timeTableHC[asign][heur][oper] = (endTime - startTime) / 1000000;
        	printActions(agent.getActions());
        	printInstrumentation(agent.getInstrumentation());
        	
        	CentralBoard sol = (CentralBoard)search.getGoalState();           
            new Info(sol, seed);
            Info.infoEstat();
            Info.stateCentrals();
            tableResultHC[asign][heur][oper] = sol.getBeneficios();           

        } catch (Exception e) { e.printStackTrace(); }
    }
    
    private static void Simulated_Annealing_Search(CentralBoard board,
    												int steps,
    												int stiter,
    												int k,
    												double lamb,
    												int seed,
    												int asign,
    												int heur,
    												int oper) {
    	System.out.println("\nSimulated Annealing -->");
        try {
            Problem problem =  new Problem(board, new CentralSuccessorFunction(),
                                                      new CentralGoalTest(),
                                                      new CentralHeuristicFunction());
            Search search =  new SimulatedAnnealingSearch(steps, stiter, k, lamb);
            long startTime = System.nanoTime();
            SearchAgent agent = new SearchAgent(problem, search);
            long endTime = System.nanoTime();
            
            timeTableSA[asign][heur][oper] = (endTime - startTime) / 1000000;
            printActions(agent.getActions());
            printInstrumentation(agent.getInstrumentation());
            
            CentralBoard sol = (CentralBoard)search.getGoalState();           
            new Info(sol, seed);
            Info.infoEstat();
            Info.stateCentrals();
            tableResultSA[asign][heur][oper] = sol.getBeneficios();           

        } catch (Exception e) { e.printStackTrace(); }
    }
    

    /**************** out ****************/

    private static void printInstrumentation(Properties properties) {
        Iterator<?> keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

    private static void printActions(List<?> actions) {
        for (int i = 0; i < actions.size(); i++) {
            System.out.println( actions.get(i) );
        }
    }

}
