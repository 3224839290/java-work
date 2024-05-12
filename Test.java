package subwaysystem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Test {
    private Map<String, Map<String, Double>> map;

    public Test() 
    {
        this.map = new LinkedHashMap<>();
    }

    public void addLine(String lineName)
    {
        map.put(lineName, new LinkedHashMap<>());
    }

    public void addStation(String lineName, String stationName, double distance) 
    {
        map.get(lineName).put(stationName, distance);
    }


    public double getDistance(String lineName, String station1, String station2)
    {
        return map.get(lineName).get(station1) + map.get(lineName).get(station2);
    }
    

    public Set<String> getTransferStations()
    {
        Map<String, Set<String>> stationLines = new HashMap<>();
        for (String line : map.keySet()) 
        {
            for (String station : map.get(line).keySet()) 
            {
                stationLines.putIfAbsent(station, new HashSet<>());
                stationLines.get(station).add(line);
            }
        }

        Set<String> transferStations = new HashSet<>();
        for (String station : stationLines.keySet()) 
        {
            if (stationLines.get(station).size() > 1) 
            {
                StringBuilder sb = new StringBuilder();
                sb.append("<").append(station).append(", <");
                for (String line : stationLines.get(station))
                {
                    sb.append(line).append(" 号线、");
                }
                sb.setLength(sb.length() - 1); // Remove the last "、"
                sb.append(">>");
                transferStations.add(sb.toString());
            }
        }

        return transferStations;
    }

    public List<String> queryStations(String stationName, double maxDistance) 
    {
        List<String> result = new ArrayList<>();
        for (Map<String, Double> lineMap : map.values()) 
        {
            for (Map.Entry<String, Double> entry : lineMap.entrySet())
            {
                if (entry.getKey().equals(stationName)) 
                {
                    for (Map.Entry<String, Double> otherEntry : lineMap.entrySet())
                    {
                        if (!entry.getKey().equals(otherEntry.getKey()))
                        {
                            double distance = entry.getValue() + otherEntry.getValue();
                            if (distance <= maxDistance) 
                            {
                                result.add(otherEntry.getKey() + " -- " + entry.getKey() + " -- " + distance);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    @Override
    public String toString()
    {
        return this.map.values().toString();
    }
    
    
    public static void main(String[] args)
    {
        Test subwayMap = new Test();
        
        try (BufferedReader br = new BufferedReader(new FileReader("subway.txt")))
        {
            String line;
            String currentLine = null;
            while ((line = br.readLine()) != null) 
            {
                if (line.contains("号线站点间距")) 
                {
                    currentLine = line.split("号线站点间距")[0];
                    subwayMap.addLine(currentLine);
                } 
                else if (line.contains("阳逻线站点间距")) 
                {
                	currentLine = line.split("线站点间距")[0];
                    subwayMap.addLine(currentLine);
                }
                else if (line.contains("---") || line.contains("—")) 
                {
                        String separator = line.contains("---") ? "---" : "—";
                    String[] parts = line.split(separator);
                    String station1 = parts[0].trim();
                    String station2 = parts[1].split("\t")[0].trim();
                    double distance = Double.parseDouble(parts[1].split("\t")[1].trim());
                    subwayMap.addStation(currentLine, station1, distance);
                    subwayMap.addStation(currentLine, station2, distance);
                }
            }
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println(subwayMap);
        
        //第一问
        Set<String> transferStations = subwayMap.getTransferStations();
        System.out.println("Transfer Stations:");
        for (String station : transferStations)
        {
            System.out.println(station);
        }
        
        //第二问
       
    }
}