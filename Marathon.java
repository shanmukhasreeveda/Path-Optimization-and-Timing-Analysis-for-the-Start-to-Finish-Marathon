import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Marathon
{
    private static final double average_speed = 17.0; // Average speed of Running over paved roads: 17km/h
    private Map<String, List<Edge>> routemap;

    private static String firstTown;
    private static String lastTown;


    //Edge class Represents an edge in the graph, containing the destination town and the distance to that town.
    private static class Edge
    {
        String destination;
        double distance;

        Edge(String destination, double distance)
        {
            this.destination = destination;
            this.distance = distance;
        }
    }

    public Marathon()
    {

        routemap = new HashMap<>();
    }

    // addEdge method adds an edge to the routemap (graph) connecting two towns with a given distance.
    public void addEdge(String source, double distance, String destination)
    {
        routemap.putIfAbsent(source, new ArrayList<>()); // Adds values into ArrayList with source as key when key is NULL.
        routemap.get(source).add(new Edge(destination, distance));
        // Assuming unconnected paths as 0 distance.
        routemap.putIfAbsent(destination, new ArrayList<>());
        routemap.get(destination).add(new Edge(source, distance));
    }
    // findoptimalPath method calculates the optimal path and time to travel from the starting town to the finishing town using Dijkstra's algorithm.
    public List<String> findoptimalPath(String starttown, String finishtown)
    {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));

        for (String town : routemap.keySet()) {
            if (town.equals(starttown)) {
                distances.put(town, 0.0);
                pq.offer(new Node(town, 0.0));
            } else {
                distances.put(town, Double.POSITIVE_INFINITY);
                pq.offer(new Node(town, Double.POSITIVE_INFINITY));
            }
            previous.put(town, null);
        }

        while (!pq.isEmpty()) {
            Node currentnode = pq.poll();
            String currenttown = currentnode.town;

            if (currenttown.equals(finishtown)) {
                break; // found the finish town, exit the loop.
            }

            if (distances.get(currenttown) < currentnode.distance) {
                continue; // skip this iteration if already found a shorter distance to this town
            }

            for (Edge edge : routemap.get(currenttown)) {
                double newdistance = distances.get(currenttown) + edge.distance / average_speed;

                if (newdistance < distances.get(edge.destination)) {
                    distances.put(edge.destination, newdistance);
                    previous.put(edge.destination, currenttown);
                    pq.offer(new Node(edge.destination, newdistance));
                }
            }
        }

        List<String> optimalpath = new ArrayList<>();
        //double optimalDistance = 0.0;
        String currTown = finishtown;

        while (currTown != null) {
            optimalpath.add(0, currTown);
            currTown = previous.get(currTown);
        }
        System.out.println("Optimal Time: "+String.format("%.4f",distances.get(finishtown))+" hours");
        System.out.println("Total Distance Traveled: "+String.format("%.4f",distances.get(finishtown)*average_speed)+" kms");

        if(distances.get(finishtown).equals(Double.POSITIVE_INFINITY)) {
            return  null;
        }
        return optimalpath;
    }
    // Node class represents a node in the graph during Dijkstra's algorithm. It contains the town and the current distance from the starting town.
    private static class Node
    {
        String town;
        double distance;

        Node(String town, double distance)
        {
            this.town = town;
            this.distance = distance;
        }
    }
    // Methode isTownValid Checks if a given town is present in the routemap (graph).
    private boolean isTownValid(String town) {

        return this.routemap.containsKey(town);
    }

    public static void main(String[] args)
    {
        Marathon solver = new Marathon();
        try {
            solver.readFromCSV("Marathon_data2.csv"); // Enter the File name 
        } catch (IOException e) {
            System.out.println("Failed to read the CSV file.");
            return;
        }
        //global set in readCSV
        String starttown = firstTown;
        System.out.println("Start town is " + starttown);
        String finishtown = lastTown;
        System.out.println("Finish town is " + finishtown);


        if(!(solver.isTownValid(starttown)&&solver.isTownValid(finishtown))) {
            System.out.println("Input Start or End town is invalid");
            return;
        }

        List<String> optimalPath = solver.findoptimalPath(starttown, finishtown);
        if(optimalPath == null) {
            System.out.println("Start and finish towns are not connected");
        } else {
            System.out.println("Towns traveled to get Optimal time: " + optimalPath);
        }
    }
    // readFromCSV method Reads data from a CSV file and populates the routemap (graph) using the addEdge() method
    public void readFromCSV(String filepath) throws IOException
    {

        BufferedReader file = new BufferedReader(new FileReader(filepath));

        String line = "";
        boolean firstLine = true;
        while ((line = file.readLine()) != null) {
            String[] towns = line.trim().split(",");
            String source = towns[0].trim();
            double distance = Double.parseDouble(towns[1]);
            String destination = towns[2].trim();
            addEdge(source, distance, destination);
            if(firstLine) {
                firstTown = source;
                firstLine = false;
            }
            lastTown = destination;
        }
    }
}
