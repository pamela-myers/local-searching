import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *      Briana Collins brcollins@alaska.edu
 *      Pamela Myers pamyers@alaska.edu
 *
 *      {@code City} creates and holds references to the N-by-N random city cost graph as well
 *      as contains all methods for creating and manipulating the cities of a random search tour.
 *
 **/

public class City {

    private final int UPPER_COST_BOUND = 2400;
    private final int LOWER_COST_BOUND = 100;
    private int numOfCities = 0;
    private int[][] cityCostGraph;
    private int[] startingTourArrangement;
    private int[] currentTourArrangement;
    private int[] restartedTourArrangement;


    private int[] bestTourArrangement;
    private int bestCost;

    /**
     * Creates a random graph of travel costs for N cities.  The graph is read as
     * row = From City and col = To City; i.e., graph[i][j] is the cost of travelling from
     * city i to city j.  The diagonal is set to -1 to represent N/A. The travel costs
     * are between [$100, $2500] dollars.
     * @param cityNumber an {@code int} that determines how large the graph is.
     */
    public City(int cityNumber)
    {
        numOfCities = cityNumber;
        cityCostGraph = new int[numOfCities][numOfCities];
        Random randomCost = new Random();
        for (int row = 0; row < cityNumber; row++)
        {
            for (int col = 0; col< cityNumber; col++)
            {
                if (row != col)
                {
                    cityCostGraph[row][col] = randomCost.nextInt(UPPER_COST_BOUND + 1) + LOWER_COST_BOUND;
                }
                if (row == col)
                {
                    cityCostGraph[row][col] = -1;
                }
            }
        }

        generateRandomStartingTour();

    }

    /**
     *  Once the {@code City} constructor is called to generate the N-by-N
     *  city cost graph, an initial random
     *  search tour is generated as a {@code int[]} of length N.  It then
     *  initializes {@code currentTourArrangment} and {@code restartedTourArrangement}
     *  to keep references available for {@code HillClimbing} and {@code SimulatedAnnealing}
     *  to use.
     */
    public void generateRandomStartingTour()
    {
        ArrayList<Integer> tour = new ArrayList<>();
        for (int i = 0; i < numOfCities; i++){
            tour.add(i);
        }
        Collections.shuffle(tour);

        int[] tourArrangement = new int[numOfCities];
        for (int i = 0; i < numOfCities; i++)
        {
            tourArrangement[i] = tour.get(i);
        }

        startingTourArrangement = tourArrangement;
        currentTourArrangement = startingTourArrangement.clone();   //create separate copy of startingTour so we can manipulate currentTour and then always return to initial conditions
        restartedTourArrangement = startingTourArrangement.clone();
    }


    /**
     * The {@code HillClimbing} needs to know what its available move set is for a given city size;
     * this will go through and generate the possible combination of pairwise city swapping that can occur
     * (ignoring starting city).
     * @return an {@code ArrayList} that holds a listing of {@code int[2]} pairs that
     *          reference the indices of the {@code int[] tour} array that will be swapped.
     */
    public ArrayList<int[]> generateHillClimbingMoveSet()
    {

        ArrayList<int[]> cityPairList = new ArrayList<>();

        for (int first = 1; first < numOfCities; first++)
        {
            for (int second = first+1; second < numOfCities; second++)
            {
                cityPairList.add(new int[]{first, second});
            }

        }

        return cityPairList;
    }

    /**
     * Randomly rearranges all cities in the tour array except for the starting tour.
     */
    public void generateRandomRestartTour()
    {
        ArrayList<Integer> tourRestart = new ArrayList<>();
        for (int i = 1; i < numOfCities; i++)
        {
            tourRestart.add(startingTourArrangement[i] );       //ignore starting city
        }
        Collections.shuffle(tourRestart, new SecureRandom());

        int[] tourToRestart = new int[numOfCities];
        tourToRestart[0] = startingTourArrangement[0];

        for(int i = 1; i< numOfCities; i++)
        {
            tourToRestart[i] = tourRestart.get(i-1);
        }

        restartedTourArrangement = tourToRestart;
    }


    /**
     * Randomly swaps two cities in the tour array.  For use in {@code SimulatedAnnealing}
     * @return an {@code int[]} of the new tour arrangement.
     */
    public int[] randomCitySwap()
    {
        Random randomCity = new Random();
        int cityA = randomCity.nextInt(numOfCities - 1) + 1; //do not allow start city to be swapped.
        int cityB = randomCity.nextInt(numOfCities - 1) + 1;
        while(cityB == cityA)
        {
            cityB = randomCity.nextInt(numOfCities -1 ) + 1; //make sure different cities are selected.
        }

        int tourTemp = currentTourArrangement[cityA];
        currentTourArrangement[cityA] = currentTourArrangement[cityB];
        currentTourArrangement[cityB] = tourTemp;

        return currentTourArrangement;
    }

    /**
     * Swaps the two specified cities in the tour array. For use in {@code HillClimbing}
     * @param cityAIndex the first {@code int} index of the tour array to swap
     * @param cityBIndex the second {@code int} index of the tour array to swap
     * @return the {@code int[]} of the swapped tour arrangement.
     */
    public int[] citySwap(int cityAIndex, int cityBIndex)
    {

        //for hill-climbing, we need to reset currentTourArrangement back to a starting point
        // so that the tour doesn't become jumbled out of order
        currentTourArrangement = restartedTourArrangement.clone();

        int tourTemp = currentTourArrangement[cityAIndex];
        currentTourArrangement[cityAIndex] = currentTourArrangement[cityBIndex];
        currentTourArrangement[cityBIndex] = tourTemp;

        return currentTourArrangement;
    }

    /**
     * Calculates from the {@code City} instance this function is called on the cost
     * of travelling from {@code tour[i]} to {@code tour[i+1]} for each {@code i} from 0 to N, where
     * the last index is calculated {@code tour[N]} to {@code tour[0]}.
     * @param tour an {@code int[]} of length N
     * @return the {@code int} cost of travelling the given {@code tour}.
     */
    public int calculateTourCost(int[] tour)
    {
        int cost = 0;
        for (int i = 0; i < tour.length; i++)
        {

            if (i == tour.length - 1) {
                cost += cityCostGraph[tour[i]][tour[0]];         //add cost for returning to initial city.
            } else
            {
                cost += cityCostGraph[tour[i]][tour[i + 1]];
            }
          //  System.out.println(cost);
        }

        return cost;
    }


    ////---------DISPLAY METHODS------//////////

    public String toString()
    {
        StringBuilder display = new StringBuilder();
        display.append("     |");
        int entryWidth = 6;
        for (int col = 0; col < numOfCities; col++)
        {
            if(col != (numOfCities-1))
                display.append(String.format("%5d|", (col+1)));
            else
                display.append(String.format("%5d", (col+1)));
        }
        int lineWidth = ((numOfCities + 1) * entryWidth);
        display.append("\n");

        for (int i = 0; i < lineWidth; i++)
        {
            if( i % 6 != 0)
                display.append("-");
            else if (i != 0)
                display.append("+");
        }
        display.append("\n");

        for (int row = 0; row < numOfCities; row++)
        {
            display.append(String.format("%5d|", (row+1)));
            for (int col = 0; col< numOfCities; col++)
            {
                if(col != (numOfCities-1))
                    display.append(String.format("%5d|", cityCostGraph[row][col]));
                else
                    display.append(String.format("%5d", cityCostGraph[row][col]));
            }
            display.append("\n");
            for (int i = 0; i < lineWidth; i++)
            {
                if( i % 6 != 0)
                    display.append("-");
                else if (i != 0)
                    display.append("+");
            }
            display.append("\n");
        }
        return display.toString();
    }

    public static void printTour(int[] inputTour)
    {
        StringBuilder tour = new StringBuilder();
        for (int i = 0; i < inputTour.length; i++)
        {
            tour.append(" " + (inputTour[i] + 1) + " ");      //making sure indices match graph indices
        }
        // tour.append("\n");
        System.out.print(tour.toString());
    }

    public static String tourToString(int[] inputTour)
    {
        StringBuilder tour = new StringBuilder();
        for (int i = 0; i < inputTour.length; i++)
        {
            int tourIndex = inputTour[i] + 1;
            tour.append(String.format(" %2d ", tourIndex));      //making sure indices match graph indices
        }
        return tour.toString();
    }


    ///-------------GETTERS------------///////

    public int getCityNumber()
    {
        return numOfCities;
    }

    public int[] getStartingTourArrangement() {
        return startingTourArrangement;
    }

}
