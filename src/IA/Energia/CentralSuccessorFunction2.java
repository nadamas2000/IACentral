
package IA.Energia;

import java.util.ArrayList;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class CentralSuccessorFunction2 implements SuccessorFunction {	

	public List<Successor> getSuccessors(Object aState) {

		ArrayList<Successor> retVal = new ArrayList<Successor>();
		CentralBoard board = (CentralBoard) aState;		
		CentralBoard newBoard = new CentralBoard(board);
		
		for (int k = board.getNCentrales()-1; k >= 0 && board.getEnMarcha(k); --k) {
			newBoard = new CentralBoard(board);
			ArrayList<Integer> cG = new ArrayList<Integer>();
			ArrayList<Integer> cNG = new ArrayList<Integer>();
			for (int i = 0; i < board.getNClientes(); ++i) {
				if (board.getAsignado(i) == k) {
					if (board.getCliente(i).getContrato() == Cliente.GARANTIZADO) {
						cG.add(i);
					} else {
						cNG.add(i);
					}
				}
			}
			
			CentralBoard testBoard = new CentralBoard(board);		
			boolean garantizadosReasignados = true;				
			for (int i = 0; i < cG.size() && garantizadosReasignados; ++i) {
				if (!testBoard.reasignarClienteEnMarcha(cG.get(i))) garantizadosReasignados = false;
			}			
			
			if (garantizadosReasignados) {
				newBoard = new CentralBoard(testBoard);
				newBoard.apagarCentral(k);
				for (int i = 0; i < cNG.size(); ++i) {
					newBoard.quitaAsignacion(cNG.get(i));
				}
				retVal.add(new Successor("Central vacia: " + k
					+ ", garantizados reasignados., beneficio anterior: " + (double)Math.round(board.getBeneficios() * 1000d) / 1000d
					+ ", beneficio nuevo: " + (double)Math.round(newBoard.getBeneficios() * 1000d) / 1000d, newBoard));				
			}
			
		}
			
		CentralBoard asignaBoard = new CentralBoard(newBoard);
		for (int i = 0; i < newBoard.getNClientes(); ++i) {
			for (int j = 0; j < newBoard.getNCentrales(); ++j) {					
				if (newBoard.getEnMarcha(j) && newBoard.puedeAsignar(i, j)) {
					asignaBoard = new CentralBoard(newBoard);					
					int centralActual = asignaBoard.getAsignado(i);					
					asignaBoard.cambioCentral(i, j);
					retVal.add(new Successor("Central actual: " + centralActual
						+ ", Central nueva: " + j
						+ ", Cliente: " + i
						+ ", consumo anterior en central: " + (double)Math.round(newBoard.getEnergiaServida()[j] * 1000d) / 1000d
						+ ", consumo nuevo en central: " + (double)Math.round(asignaBoard.getEnergiaServida()[j] * 1000d) / 1000d							
						+ ", beneficio anterior: " + (double)Math.round(newBoard.getBeneficios() * 1000d) / 1000d
						+ ", beneficio nuevo: " + (double)Math.round(asignaBoard.getBeneficios() * 1000d) / 1000d, asignaBoard));
		
				}
			}
		}
		return retVal;
	}	
	
}
