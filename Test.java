package subwaysystem;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Test {
    private Map<String, Map<String, Double>> map;
    private Map<String, Map<String, Node>> map2;
    public Test() 
    {
        this.map = new LinkedHashMap<>();
        this.map2 = new LinkedHashMap<>();
    }

    public void addLine(String lineName)
    {
        map.put(lineName, new LinkedHashMap<>());
    }
    public void addLine2(String lineName)
    {
        map2.put(lineName, new LinkedHashMap<>());
    }
    
    public void addStation(String lineName, String stationName, double distance) 
    {
        map.get(lineName).put(stationName, distance);
    }
    public void addStation2(String lineName, String stationName,double distance) {
		Node node = new Node(stationName, distance);
        map2.get(lineName).put(stationName, node);
        // 假设每条线路上的站点是顺序相连的，这里只添加直接相邻的站点
        if (map2.get(lineName).size() > 1) {
            String prevStation = map2.get(lineName).keySet().toArray(new String[0])[map2.get(lineName).size() - 2];
            map2.get(lineName).get(prevStation).neighbors.add(node);
            node.neighbors.add(map2.get(lineName).get(prevStation));
        }
    }

    public double getDistance(String lineName, String station1, String station2)
    {
        return map.get(lineName).get(station1) + map.get(lineName).get(station2);
    }
    
    public double getDistance2(String lineName, String station1, String station2)
    {
        return map2.get(lineName).get(station1).distance + map2.get(lineName).get(station2).distance;
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

    public String queryStationsFormatted(String stationName, double maxDistance) {
        List<String> result = new ArrayList<>();
        for (Map<String, Double> lineMap : map.values()) {
            for (Map.Entry<String, Double> entry : lineMap.entrySet()) {
                if (entry.getKey().equals(stationName)) {
                    for (Map.Entry<String, Double> otherEntry : lineMap.entrySet()) {
                        if (!entry.getKey().equals(otherEntry.getKey())) {
                            double distance = entry.getValue() + otherEntry.getValue();
                            if (distance <= maxDistance) {
                                String lineName = map.keySet().stream()
                                    .filter(key -> lineMap == map.get(key))
                                    .findFirst()
                                    .orElse("");
                                result.add("<" + otherEntry.getKey() + "，" + lineName + "号线，距离：" + distance + ">");
                            }
                        }
                    }
                }
            }
        }
        return result.isEmpty() ? "没有找到距离为 " + maxDistance + " 公里以内的站点。" : result.toString();
    }
    
    
    public static class Node 
    {
        String name;
        Set<Node> neighbors;
		private double distance;

        public Node(String name, double distance) {
            this.name = name;
            this.distance = distance;
            this.neighbors = new HashSet<>();
        }
    }
    public List<List<String>> getAllPaths(String start, String end) {
        Map<String, Node> allNodes = new HashMap<>();
        for (Map<String, Node> lineNodes : map2.values()) {
            allNodes.putAll(lineNodes);
        }
        return dfs(allNodes, start, end, new HashSet<>(), new ArrayList<>());
    }

    private List<List<String>> dfs(Map<String, Node> nodes, String current, String end, Set<String> visited, List<String> path) {
        visited.add(current);
        path.add(current);

        if (current.equals(end)) {
            return Arrays.asList(new ArrayList<>(path));
        }

        List<List<String>> paths = new ArrayList<>();
        Node currentNode = nodes.get(current);
        for (Node neighbor : currentNode.neighbors) {
            if (!visited.contains(neighbor.name)) {
                paths.addAll(dfs(nodes, neighbor.name, end, visited, path));
            }
        }

        path.remove(path.size() - 1);
        visited.remove(current);
        return paths;
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
                    subwayMap.addLine2(currentLine);
                } 
                else if (line.contains("阳逻线站点间距")) 
                {
                	currentLine = line.split("线站点间距")[0];
                    subwayMap.addLine(currentLine);
                    subwayMap.addLine2(currentLine);
                }
                else if (line.contains("---") || line.contains("—")) 
                {
                        String separator = line.contains("---") ? "---" : "—";
                    String[] parts = line.split(separator);
                    String station1 = parts[0].trim();
                    String station2 = parts[1].split("\t")[0].trim();
                    double distance = Double.parseDouble(parts[1].split("\t")[1].trim());
                    subwayMap.addStation(currentLine, station1, distance);
                    subwayMap.addStation2(currentLine, station1,distance);
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
        Scanner scanner = new Scanner(System.in);
        try 
        {
        	System.out.print("请输入站点名称: ");
            String stationNameInput = scanner.nextLine().trim();

            System.out.print("请输入最大距离（公里）: ");
            if (!scanner.hasNextDouble()) {
                System.out.println("输入的最大距离不合法，请输入一个数字！");
                scanner.close();
                return;
            }
            double maxDistance = scanner.nextDouble();
            scanner.nextLine(); // 消耗掉nextDouble后的换行符
            
            String formattedResult = subwayMap.queryStationsFormatted(stationNameInput, maxDistance);
            System.out.println(stationNameInput+"，最大距离为 "+maxDistance+" 的站点为:");
            System.out.println(formattedResult);

        } 
        catch (Exception e)
        {
            e.printStackTrace();
        
        }
        // 第三问
        System.out.print("请输入起点站名称: ");
        String startStation = scanner.nextLine().trim();

        System.out.print("请输入终点站名称: ");
        String endStation = scanner.nextLine().trim();

        List<List<String>> paths = subwayMap.getAllPaths(startStation, endStation);
        System.out.println("从 " + startStation + " 到 " + endStation + " 的路径有:");
        for (List<String> path : paths) 
        {
            System.out.println(path);
        }
        scanner.close();
    }
}