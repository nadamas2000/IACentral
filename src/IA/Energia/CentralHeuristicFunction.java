
package IA.Energia;

import aima.search.framework.HeuristicFunction;

public class CentralHeuristicFunction implements HeuristicFunction {    

    public double getHeuristicValue(Object n){
        CentralBoard board = (CentralBoard) n;
        return -board.getBeneficios();
    }
}
