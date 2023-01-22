import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *      Briana Collins brcollins@alaska.edu
 *      Pamela Myers pamyers@alaska.edu
 *
 *      Runs Hill Climbing with Random Restart algorithm on a given {@code City} cost graph.
 **/

public class HillClimbing {
    City testTour;
    int bestCost;
    int currentTour[];
    int bestTour[];

    private final int NUM_RESTARTS = 15000;
    int totalExecutionNumber = 0;
    int executionNumberOfBestFound = 0;

    Results recorder;
    Map<String, Object> currentRun = new HashMap<String, Object>();
    int currentRunNumber = 0;


    /**
     * Base constructor
     * @param newTour the {@code City} that contains the cost graph and starting tour for the search
     */
    public HillClimbing(City newTour) {
        testTour = newTour;
        setCurrentTour(testTour.getStartingTourArrangement());
        setBestTour(currentTour);
        setBestCost(testTour.calculateTourCost(bestTour));
        recorder = new Results(true, newTour);
    }

    /**
     * Runs outer loop of hill climbing (the random restarts) and stores all collected data for run.
     */
    public void runHillClimbingSearch() {

        for (int i = 0; i < NUM_RESTARTS; i++) {
            getBestCostTour(testTour);
            testTour.generateRandomRestartTour();
        }
        currentRunNumber++;

        currentRun.put("Run Number", currentRunNumber);
        currentRun.put("Starting Tour", testTour.getStartingTourArrangement());
        currentRun.put("Starting Cost", testTour.calculateTourCost(testTour.getStartingTourArrangement()));
        currentRun.put("Best Found Cost", bestCost);
        currentRun.put("Best Tour", bestTour);
        currentRun.put("ExecutionTime", totalExecutionNumber);
        currentRun.put("BestExecutionTime", executionNumberOfBestFound);
        currentRun.put("Restarts", NUM_RESTARTS);

        recorder.addRecord(currentRun);
    }

    /**
     * Runs inner loop for Hill Climbing by iterating through the available move set and finding the best cost
     * among them.
     * @param currentCity the randomly restarted {@code int[]} tour from {@code runHillClimbingSearch}
     */
    public void getBestCostTour(City currentCity) {
        ArrayList<int[]> moveSetForHillClimbing = currentCity.generateHillClimbingMoveSet();

        for (int[] set : moveSetForHillClimbing) {
            int[] swappedArrangement = currentCity.citySwap(set[0], set[1]);
            int swappedCityCost = currentCity.calculateTourCost(swappedArrangement);

            totalExecutionNumber++;
            if (swappedCityCost < bestCost) {
                executionNumberOfBestFound = totalExecutionNumber;
                setBestCost(swappedCityCost);
                setBestTour(swappedArrangement);
            }
        }
    }

    /**
     * Resets necessary variables back to starting condition in order to rerun the same search instance
     * on the same {@code City} multiple times.
     */
    public void resetSearch() {
        setBestTour(testTour.getStartingTourArrangement());
        setBestCost(testTour.calculateTourCost(bestTour));
        executionNumberOfBestFound = 0;
        totalExecutionNumber = 0;
        currentRun.clear();
    }

    ////------------GETTERS & SETTERS---------------//////

    public void setBestCost(int cost) { bestCost = cost; }

    public void setBestTour(int tour[]) { bestTour = tour.clone(); }

    public void setCurrentTour(int tour[]) { currentTour = tour.clone(); }

    public Results getRecorder() {
        return recorder;
    }
}