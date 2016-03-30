

/*import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DomineeringBoardTest {
	DomineeringBoard domineeringBoard;
	@Before
	public void setUp() throws Exception {
		domineeringBoard = new DomineeringBoard(true, 3, 3);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		domineeringBoard = (DomineeringBoard) domineeringBoard.play(new DomineeringMove(0, 1));
		assertEquals(0, domineeringBoard.value());
		domineeringBoard = (DomineeringBoard) domineeringBoard.play(new DomineeringMove(2, 1));
		assertEquals(1, domineeringBoard.value());
		
		domineeringBoard = new DomineeringBoard(false, 3, 3);
		domineeringBoard = (DomineeringBoard) domineeringBoard.play(new DomineeringMove(0, 0));
		assertEquals(0, domineeringBoard.value());
		domineeringBoard = (DomineeringBoard) domineeringBoard.play(new DomineeringMove(1, 1));
		assertEquals(-1, domineeringBoard.value());
	}

}*/
