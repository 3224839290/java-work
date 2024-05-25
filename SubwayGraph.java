package subwaysystem;

import java.util.*;

public class SubwayGraph 
{
    private Map<String, List<String>> graph = new HashMap<>();

    public void addConnection(String station1, String station2) 
    {
        graph.putIfAbsent(station1, new ArrayList<>());
        graph.putIfAbsent(station2, new ArrayList<>());
        
        if (!graph.get(station1).contains(station2)) 
        {
            graph.get(station1).add(station2);
        }

        if (!graph.get(station2).contains(station1)) 
        {
            graph.get(station2).add(station1);
        }
    }

    public List<List<String>> findAllPaths(String startStation, String endStation)
    {
        List<List<String>> paths = new ArrayList<>();
        LinkedList<String> visited = new LinkedList<>();
        dfs(startStation, endStation, visited, paths);
        return paths;
    }

    private void dfs(String currentStation, String endStation, LinkedList<String> visited, List<List<String>> paths)
    {
        visited.add(currentStation); // Mark the current station as visited
        if (currentStation.equals(endStation)) 
        {
            paths.add(new ArrayList<>(visited));
        } 
        else 
        {
            List<String> stations = graph.get(currentStation);
            if (stations != null) 
            {
                for (String station : stations) 
                {
                    if (!visited.contains(station)) 
                    {
                        dfs(station, endStation, visited, paths);
                    }
                }
            }
        }
        visited.removeLast(); // Backtrack
    }
}