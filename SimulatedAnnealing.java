import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 *      Briana Collins brcollins@alaska.edu
 *      Pamela Myers pamyers@alaska.edu
 *
 *      Runs Simulated Annealing algorithm on a given {@code City} cost graph.
 **/

public class SimulatedAnnealing
{
    int currentBestCost;
    int[] currentBestTour;

    City cityGraph;

    int totalExecutionNumber = 0;
    int executionNumberOfBestFound = 0;
    Results recorder;

    int temperatureUpperBound = 20;     //for chosen temperature schedule, anything much larger than 25 causes long
                                        //delays and negligible improvement on results
    int temperatureAdjustment = 0;

    Random randomProbability = new Random();


    Map<String, Object> currentRun = new HashMap<String, Object>();
    int currentRunNumber = 0;

    /**
     * Base constructor
     * @param tourGraph the {@code City} that contains the cost graph and starting tour for the search
     */
    public SimulatedAnnealing(City tourGraph)
    {
        cityGraph = tourGraph;
        currentBestTour = tourGraph.getStartingTourArrangement();
        currentBestCost = tourGraph.calculateTourCost(currentBestTour);
        recorder = new Results(false, tourGraph);
    }

    /**
     * Resets necessary variables back to starting condition in order to rerun the same search instance
     * on the same {@code City} multiple times.
     */
    public void resetSearch()
    {
        currentBestTour = cityGraph.getStartingTourArrangement();
        currentBestCost = cityGraph.calculateTourCost(currentBestTour);
        executionNumberOfBestFound = 0;
        totalExecutionNumber = 0;
        temperatureAdjustment = 0;
        currentRun.clear();
    }

    /**
     * helper method to determine the probability of choosing a worsening move for simulated annealing.
     * @param deltaE the difference between the current best cost and the current move in cost
     * @return the probability between (0, 1) of the current move being chosen
     */
    private double calculateAnnealingProbability(int deltaE)
    {
        int temperature = temperatureUpperBound - temperatureAdjustment;
        double probabilityOfAcceptance = (1)/( Math.exp( ((double)deltaE / temperature) ) );
        return probabilityOfAcceptance;
    }

    /**
     * runs algorithm and saves collected data.
     */
    public void runAnnealingSearch()
    {

        while( temperatureAdjustment < temperatureUpperBound)
        {
            int[] swappedCity = cityGraph.randomCitySwap();
            int swappedCityCost = cityGraph.calculateTourCost(swappedCity);
            int deltaE = (swappedCityCost - currentBestCost);

            //take better move
            if (deltaE <= 0)
            {
                currentBestCost = swappedCityCost;
                currentBestTour = swappedCity.clone();

                //only modify best found time if the new cost is actually better
                if(deltaE != 0)
                    executionNumberOfBestFound = totalExecutionNumber;

                temperatureAdjustment++;
            }

            //maybe take worse move
            else
            {
                double probability = calculateAnnealingProbability(deltaE);
                double randomChoice = randomProbability.nextDouble();  //returns number between (0, 1)

                //simulates randomly accepting or rejecting a worse move
                if(randomChoice <= probability)
                {
                    currentBestCost = swappedCityCost;
                    currentBestTour = swappedCity.clone();
                    executionNumberOfBestFound = totalExecutionNumber;
                }

            }
            totalExecutionNumber++;

        }
        currentRunNumber++;

        currentRun.put("Upper Temperature", temperatureUpperBound);
        currentRun.put("Run Number", currentRunNumber);
        currentRun.put("Starting Tour", cityGraph.getStartingTourArrangement());
        currentRun.put("Starting Cost", cityGraph.calculateTourCost(cityGraph.getStartingTourArrangement()));
        currentRun.put("Best Found Cost", currentBestCost);
        currentRun.put("Best Tour", currentBestTour);
        currentRun.put("ExecutionTime", totalExecutionNumber);
        currentRun.put("BestExecutionTime", executionNumberOfBestFound);

        recorder.addRecord(currentRun);
    }

    //////-----------------GETTERS--------------///////

    public Results getRecorder()
    {
        return recorder;
    }
}