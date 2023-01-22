import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *      Briana Collins brcollins@alaska.edu
 *      Pamela Myers pamyers@alaska.edu
 *
 *      Support class for {@code SimulatedAnnealing} and {@code HillClimbing}; keeps track of various
 *      performance stats and then formats them nicely in table form and saves to disk.
 **/

public class Results {


    boolean isHillClimbing;
    City cityGraph;

    ArrayList<Map<String, Object>> records = new ArrayList<>();

    /**
     * Base constructor
     * @param whichType simple {@code boolean} flag to determine which algorithm is run; True for HillClimbing,
     *                  False for SimulatedAnnealing
     * @param startingCityGraph reference to the {@code City} that the algorithms are currently running on.
     */
    public Results(boolean whichType, City startingCityGraph)
    {
        if (whichType)
        {
            isHillClimbing = true;
        } else
        {
            isHillClimbing = false;
        }
        cityGraph = startingCityGraph;
    }

    /**
     * Adds a line of search data to the @{code ArrayList<Map(String, Object)>} data array; each record added is
     * one complete search run through the {@code City} provided through the constructor.
     * @param newRecord the data is collected throughout an algorithm search run and then stored in a {@code Map<>()}
     *                  object.
     */
    public void addRecord(Map<String, Object> newRecord)
    {
        Map<String, Object> recordCopy = new HashMap<String, Object>();
        for (String key: newRecord.keySet())
        {
            if(recordCopy.containsKey(key)) //safety check just in case.
            {
                recordCopy.remove(key);
            }
            recordCopy.put(key, newRecord.get(key));
        }
        records.add(recordCopy);

    }

    /**
     * Simple method to see what records are in the {@code Results} object.
     */
    public void printRecords()
    {
        System.out.println("length of records " + records.size());
        for (Map<String, Object> record: records)
        {
            for (String key :  record.keySet()) {
                Object value = record.get(key);
                System.out.print(key + " : ");
                if (value instanceof Integer) {
                    System.out.print((int) value);
                } else {
                    City.printTour((int[])value);
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    /**
     * Takes all {@code Map<String, Object>()} elements in the the {@code ArrayList<>} and formats them nicely in a table.
     * @return the data from the {@code ArrayList<Map<String, Object>>()} object as a ASCII table.
     */
    public String tabulateRecords()
    {

        StringBuilder table = new StringBuilder();

        if(isHillClimbing)
        {
            table.append("Hill-Climbing with Random Restart\n\n");
            table.append(" Run |              Starting Tour             | Starting Cost |            Best Found Tour             | Best Cost | Best Time | Total Time\n");
            table.append("=====+========================================+===============+========================================+===========+===========+===========\n");
        }
        else
        {
            table.append("Simulated Annealing\n\n");
            table.append(" Run |              Starting Tour             | Starting Cost |            Best Found Tour             | Best Cost | Best Time | Total Time \n");
            table.append("=====+========================================+===============+========================================+===========+===========+============\n");
        }

        int runNumber = 0;
        int[] startingTour = new int[cityGraph.getCityNumber()];
        int startingTourCost = 0;
        int[] bestFoundTour = new int[cityGraph.getCityNumber()];
        int bestTourCost = 0;
        int bestFoundTime = 0;
        int totalTime = 0;
        int restarts = 0;
        int upperTemperature = 0;

        for (Map<String, Object> record: records)
        {
            for (String key :  record.keySet())
            {
                if (key.equals("Run Number"))
                {
                    runNumber = (int) record.get(key);
                }
                else if( key.equals("Starting Tour"))
                {
                    startingTour = (int[]) record.get(key);
                }
                else if (key.equals("Starting Cost"))
                {
                    startingTourCost = (int) record.get(key);
                }
                else if (key.equals("Best Found Cost"))
                {
                    bestTourCost = (int) record.get(key);
                }
                else if( key.equals("Best Tour"))
                {
                    bestFoundTour = (int[]) record.get(key);
                }
                else if (key.equals("Restarts"))
                {
                    restarts = (int) record.get(key);
                }
                else if (key.equals("ExecutionTime"))
                {
                    totalTime = (int) record.get(key);
                }
                else if (key.equals("BestExecutionTime"))
                {
                    bestFoundTime = (int) record.get(key);
                }
                else if (key.equals("Upper Temperature"))
                {
                    upperTemperature = (int) record.get(key);
                }

            }

            table.append(String.format("%5d|%17s|%15d|%23s|%11d|%11d|%12d\n", runNumber,
                    City.tourToString(startingTour), startingTourCost, City.tourToString(bestFoundTour), bestTourCost,
                    bestFoundTime, totalTime));

        }
        if (isHillClimbing)
        {
            table.append("Restarts per Run: " + restarts);
        } else
        {
            table.append("Annealing Schedule: " + upperTemperature + " - TemperatureAdjustment");
        }
        table.append("\n\n");

        return table.toString();
    }

    /**
     * Saves the {@code City} graph and {@code String} table data to file, although it only saves the graph if
     * {@code isHillClimbing} is false.  (Allows for printing the same graph only once for each search algorithm).
     */
    public void saveToFile()
    {
        String graph = cityGraph.toString();
        String data = tabulateRecords();

        final String fileName = "Collected Data";

        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter = null;

        try
        {
            File file = new File(fileName);

            if (!file.exists())
            {
                file.createNewFile();
            }

            fileWriter = new FileWriter(file.getAbsolutePath(), true);
            bufferedWriter = new BufferedWriter(fileWriter);

            if(!isHillClimbing)
            {
                bufferedWriter.write(graph);
            }

            bufferedWriter.write(data);
            bufferedWriter.close();
            fileWriter.close();

        } catch( IOException e)
        {
            e.printStackTrace();
        }
    }
}
