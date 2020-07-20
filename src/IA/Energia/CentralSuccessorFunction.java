
package IA.Energia;

import java.util.ArrayList;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class CentralSuccessorFunction implements SuccessorFunction {	

	public List<Successor> getSuccessors(Object aState) {

		ArrayList<Successor> retVal = new ArrayList<Successor>();
		CentralBoard board = (CentralBoard) aState;
	   	
	   	double beneficiosAnterior = board.getBeneficios();
		for (int i = 0; i < board.getNClientes(); ++i) {
			for (int j = 0; j < board.getNCentrales(); ++j) {				
				CentralBoard newBoard;			
				if (board.puedeAsignar(i, j)) {
					int[] asignaciones = board.getAsignaClientToCentral();
					int centralActual = asignaciones[i];
					newBoard = new CentralBoard(board);
					newBoard.cambioCentral(i, j);
					retVal.add(new Successor("Central actual: " + centralActual
							+ ", Central nueva: " + j
							+ ", Cliente: " + i
							+ ", consumo anterior en central: " + (double)Math.round(board.getEnergiaServida()[j] * 1000d) / 1000d
							+ ", consumo nuevo en central: " + (double)Math.round(newBoard.getEnergiaServida()[j] * 1000d) / 1000d							
							+ ", beneficio anterior: " + (double)Math.round(beneficiosAnterior * 1000d) / 1000d
							+ ", beneficio nuevo: " + (double)Math.round(newBoard.getBeneficios() * 1000d) / 1000d, newBoard));
				}
			}
		}
		return retVal;
	}	
	
}
