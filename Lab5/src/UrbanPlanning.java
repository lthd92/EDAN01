import org.jacop.constraints.Count;
import org.jacop.constraints.Element;
import org.jacop.constraints.LexOrder;
import org.jacop.constraints.SumInt;
import org.jacop.constraints.XmulCeqZ;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;
import org.jacop.search.SimpleMatrixSelect;

public class UrbanPlanning {

	
	public static String solve(int n, int nbr_com, int nbr_res, int[] point_distr){
		Store store = new Store();
		
		IntVar[][] grid = new IntVar[n][n];
		
		for(int y = 0; y < n; y++){
			for(int x = 0; x < n; x++) {
				//0 for residential, 1 for commerce
				grid[y][x] = new IntVar(store, "[" + y + ", " + x + "]", 0, 1);
			}
		}
		IntVar[] vector = flatten_matrix(grid);

		//Constraint for the number of comm = nbr_com, etc.
		do_element_count_constraint(store, nbr_com, vector);
		//The count for amount of com in row and cols. First n indexes
		//is the count for each row, and the rest is the count for the columns
		IntVar[] row_and_col_counter = do_element_counter(grid, n, store);
		//The constraint that calculates the total points
		IntVar[] points = do_points_calculation(grid, n, row_and_col_counter, point_distr, store);
		
        for (int i = 0; i < n - 1; i++) {
            store.impose(new LexOrder(grid[i], grid[i + 1], true));
            store.impose(new LexOrder(get_col(grid, i), get_col(grid, i + 1), true));
        }
		
        IntVar maxScore = new IntVar(store, -(n * n * 2), n * n * 2);
        store.impose(new SumInt(store, points, "==", maxScore));

        IntVar minScore = new IntVar(store, -(n * n * 2), n * n * 2);
        store.impose(new XmulCeqZ(maxScore, -1, minScore));
		
 
        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select = new SimpleMatrixSelect<IntVar>(grid, null, new IndomainMin<IntVar>());

        boolean result = search.labeling(store, select, minScore);

        if (result) {
            System.out.println("Solution: ");
            System.out.println("Maximum grid score: " + maxScore.value());
        } else {
            System.out.println("No solution found.");
        }
		return Integer.toString(maxScore.value());
	}
	
	private static IntVar[] do_points_calculation(IntVar[][] grid, int n, IntVar[] row_and_col_counter,
			int[] point_distr, Store store) {
		IntVar[] points = new IntVar[2 * n];
		for(int i = 0; i < 2 * n; i++){
			points[i] = new IntVar(store, -(n*n), (n*n));
			store.impose(new Element(row_and_col_counter[i], point_distr, points[i], -1));
		}
		return points;
	}

	private static IntVar[] do_element_counter(IntVar[][] grid, int n, Store store) {
		IntVar[] row_and_col_counter = new IntVar[n*2];
		for(int i = 0; i < n; i++){
			row_and_col_counter[i] = new IntVar(store, "Number of commercials in row [" + (i+1) + "]", 0, n);
			store.impose(new Count(get_row(grid, i), row_and_col_counter[i], 1));
			row_and_col_counter[n + i] = new IntVar(store, "Number of commercials in col [" + (i+1) + "]", 0, n);
			store.impose(new Count(get_col(grid, i), row_and_col_counter[n + i], 1));
		}
		return row_and_col_counter;
	}

	private static void do_element_count_constraint(Store store, int nbr_com, IntVar[] elements){
		IntVar nbr_of_com = new IntVar(store, "Nbr of commerce", nbr_com, nbr_com);
		store.impose(new SumInt(store, elements, "==", nbr_of_com));
	}
	
	
	
	
	
	private static IntVar[] flatten_matrix(IntVar[][] matrix){
		IntVar[] vector = new IntVar[matrix.length * matrix[0].length];
		for(int i = 0; i < matrix.length; i++){
			for(int j = 0; j < matrix[0].length; j++){
				vector[i * matrix.length + j] = matrix[i][j];
			}
		}
		return vector;
	}
	
	private static IntVar[] get_row(IntVar[][] matrix, int row_nbr){
		return matrix[row_nbr];
	}
	
	private static IntVar[] get_col(IntVar[][] matrix, int col_nbr) {
		IntVar[] col = new IntVar[matrix.length];
		for(int y = 0; y < matrix.length; y++){
			col[y] = matrix[y][col_nbr];
		}
		return col;
	}
	
	
	
	public static void main(String[] args){
		int n = 7;
		int nbr_com = 20;
		int nbr_res = 29;
		int[] point_distr = {7, 6, 5, 4, -4, -5, -6, -7};
		
		System.out.println(solve(n, nbr_com, nbr_res, point_distr));
	}
}
