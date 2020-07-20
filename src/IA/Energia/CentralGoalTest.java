
package IA.Energia;

import aima.search.framework.GoalTest;

public class CentralGoalTest implements GoalTest {

	public boolean isGoalState(Object aState) {		
		CentralBoard board = (CentralBoard) aState;
		return (board.isGoal());
	}
}
