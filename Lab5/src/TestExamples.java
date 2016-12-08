import static org.junit.Assert.*;

import org.junit.Test;

public class TestExamples {

	@Test
	public void example_1() {
		int n = 5;
		int nbr_com = 13;
		int nbr_res = 12;
		int[] point_distr = {5, 4, 3, -3, -4, -5};

		assertEquals("Något gick fel eheheh", "14", 
				UrbanPlanning.solve(n, nbr_com, nbr_res, point_distr));
	}


	@Test
	public void example_2() {
		int n = 5;
		int nbr_com = 7;
		int nbr_res = 18;
		int[] point_distr = {5, 4, 3, -3, -4, -5};
		
		assertEquals("Något gick fel eheheh", "36", 
				UrbanPlanning.solve(n, nbr_com, nbr_res, point_distr));
	}


	@Test
	public void example_3() {
		int n = 7;
		int nbr_com = 20;
		int nbr_res = 29;
		int[] point_distr = {7, 6, 5, 4, -4, -5, -6, -7};
		
		assertEquals("Något gick fel eheheh", "58", 
				UrbanPlanning.solve(n, nbr_com, nbr_res, point_distr));
	}

}
