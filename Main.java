import java.util.ArrayList;

/**
 *      Briana Collins brcollins@alaska.edu
 *      Pamela Myers pamyers@alaska.edu
 *
 *      Runs {@code SimulatedAnnealing} and {@code HillClimbing} five times each for a randomly generated
 *      cost graph, five times. (i.e., 25 runs total for each algorithm).
 **/

public class Main {

    public static void main(String[] args)
    {

        for (int i = 0; i < 5; i++)
        {
            City city = new City(10);
            System.out.println(city.toString());
            createSimulatedAnnealingRun(city, 5);
            createHillClimbingRun(city, 5);

        }
    }

    /**
     * Creates a {@code SimulatedAnnealing} search instance for the given {@code City} cost graph,
     * then runs the search for {@code numberOfRuns} times with different starting tours.
     * @param cityGraph
     * @param numberOfRuns
     */
    public static void createSimulatedAnnealingRun(City cityGraph, int numberOfRuns)
    {
        SimulatedAnnealing search = new SimulatedAnnealing(cityGraph);
        for (int i = 0; i < numberOfRuns; i++)
        {
            search.runAnnealingSearch();
            cityGraph.generateRandomStartingTour();
            search.resetSearch();
        }
        Results recorder = search.getRecorder();
        recorder.saveToFile();
        System.out.println(recorder.tabulateRecords());
    }

    /**
     * Creates a {@code HillClimbing} search instance for the given {@code City} cost graph,
     * then runs the search for {@code numberOfRuns} with different starting tours.
     * @param cityGraph
     * @param numberOfRuns
     */
    public static void createHillClimbingRun(City cityGraph, int numberOfRuns) {
        HillClimbing search = new HillClimbing(cityGraph);

        for (int i = 0; i < numberOfRuns; i++) {
            search.runHillClimbingSearch();
            cityGraph.generateRandomStartingTour();
            search.resetSearch();
        }

        Results recorder = search.getRecorder();
        recorder.saveToFile();
        System.out.println(recorder.tabulateRecords());
    }
 }


