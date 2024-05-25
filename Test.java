package subwaysystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Test
{
	private List<Route> routes;
	
	public Test() 
	{
		this.routes= new ArrayList <Route> ();
	}
	
	private void readFile() 
	{
    	try 
    	{
    		Scanner s = new Scanner(new File("subway.txt"));
    		int rankLine = 0;
    		while (s.hasNext()) 
    		{
    			String line =s.nextLine();
    			if(line.contains("线")) 
    			{
    			    Route r  = new Route();
    			    r.setName(line.substring(0,(line.indexOf("线")) + 1));
    			    this.routes.add(r);
    			    rankLine++;  
    			}
    			if(line.contains("---")||line.contains("—"))
    			{
    				setIntervals(this.routes.get(rankLine-1), line);
    			} 
    		}
    		s.close();
    	}
    	catch (NullPointerException e) 
    	{
    		e.printStackTrace();
		}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    }
	
	 /**
		 * set an interval from the read line
		 * @param r the route
		 * @param l the read string
		 * @throws IOException
		 */
		private void setIntervals(Route r, String l) throws IOException  
		{
		    String separator = l.contains("---") ? "---" : "—";
			String[] s1 = l.split(separator);
			String[] s2 = s1[1].split("\t");
			Interval in = new Interval(s1[0], s2[0], Double.valueOf(s2[1]));
			r.getIntevals().add(in);
		}
		
		  private List<TransferStation> identifyTransferStations() 
		  {
		        Map<String, List<String>> stationToLines = new HashMap<>();
		        List<TransferStation> transferStations = new ArrayList<>();

		        for (Route route : this.routes)
		        {
		            String currentLine = route.getName();
		            for (Interval interval : route.getIntevals()) 
		            {
		                for (int i = 0; i < interval.getStops().length; i++) 
		                {
		                    String stationName = interval.getStops()[i];
		                    // 如果站点还没有被记录过，或者当前线路不在站点已有的线路列表中，则添加线路
		                    if (!stationToLines.containsKey(stationName) || !stationToLines.get(stationName).contains(currentLine)) 
		                    {
		                        if (!stationToLines.containsKey(stationName)) 
		                        {
		                            stationToLines.put(stationName, new ArrayList<>());
		                        }
		                        stationToLines.get(stationName).add(currentLine);
		                    }
		                }
		            }
		        }

		        // 过滤出至少有两条线路通过的站点
		        for (Map.Entry<String, List<String>> entry : stationToLines.entrySet()) 
		        {
		            if (entry.getValue().size() > 1) 
		            {
		                TransferStation ts = new TransferStation(entry.getKey());
		                ts.getLines().addAll(entry.getValue());
		                transferStations.add(ts);
		            }
		        }

		        return transferStations;
		    }
		
		  /**
		     * 获取距离指定站点小于n的所有站点集合
		     *
		     * @param stationName 指定的站点名
		     * @param n           最大距离
		     * @return 站点集合
		     */
		    public List<Object[]> getNearbyStations(String stationName, double n) 
		    {
		        List<Object[]> nearbyStations = new ArrayList<>();
		        for (Route route : this.routes) 
		        {
		            for (Interval interval : route.getIntevals()) 
		            {
		                for (String stop : interval.getStops()) 
		                {
		                    if (stop.equals(stationName)) 
		                    {
		                        for (Interval otherInterval : route.getIntevals()) 
		                        {
		                            for (String otherStop : otherInterval.getStops())
		                            {
		                                if (!otherStop.equals(stationName) && Math.abs(interval.getDistance() - otherInterval.getDistance()) <= n
		                                		&& interval.getDistance() - otherInterval.getDistance() > 0) 
		                                {
		                                    Object[] stationInfo = {otherStop, route.getName(), Math.abs(interval.getDistance() - otherInterval.getDistance())};
		                                    nearbyStations.add(stationInfo);
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }
		        // 去重处理
		        Set<Object[]> uniqueStations = new HashSet<>(nearbyStations);
		        // 将Set转换回List
		        List<Object[]> distinctNearbyStations = new ArrayList<>(uniqueStations);

		        return distinctNearbyStations;
		    }
		  
			private ArrayList<String> getAllRoutes(String stop) {
				ArrayList <String> all = new ArrayList<>();
				for(Route x: this.routes) {
					for(Interval i: x.getIntevals()) {
						if((i.getStops()[0].equals(stop))||(i.getStops()[1].equals(stop))) {
							all.add(x.getName());
							break;
						}
					}
				}
				return all;
			}
		    
		    private ArrayList<String> getAllStops() {
        		ArrayList< String> all = new ArrayList<>();
        		for(Route r: this.routes) {
        			for(Interval i: r.getIntevals()) {
        				for(String s: i.getStops()) {
        					boolean b = true;
        					for(int j = 0; j<all.size(); j++) {
        						if(all.get(j).equals(s)) {
        							b = false;
        						}
        					}
        					if(b == true) {
        						all.add(s);
        					}
        				}
        			}
        		}
        		return all;
        	}
		    
		    private void initialDistance(double[][] map, ArrayList<String> all, double max) {
		    	for(Route r : this.routes) {
		    		for(Interval interval : r.getIntevals()) {
		    			for(int i = 0; i<all.size(); i++) {
		    				if(interval.getStops()[0].equals(all.get(i))) {
		    					for(int j = 0; j<all.size(); j++) {
		    						if(all.get(j).equals(interval.getStops()[1])) {
		    							map[i][j] = interval.getDistance();
		    							map[j][i] = interval.getDistance();
		    						}
		    					}
		    				break;
		    				}
		    			}
		    		}
		    	}
		    	for(int i = 0; i<map.length; i++) {
		    		for(int j = 0; j<map.length; j++) {
		    			if(map[i][j]==0.0 && i!=j) { //if can't reach
		    				map[i][j] = max;
		    			}
		    		}
		    	}
			}
			
            
        	private ArrayList<String> getBestPath(String start, String end) {
            	ArrayList<String> best = new ArrayList<>();
            	ArrayList<String> all = getAllStops();
            	double max = 1000;
            	double[][] map = new double[all.size()][all.size()];
            	initialDistance(map, all, max);
            	boolean[] isVisited = new boolean[map.length];//mark whether the stop is considered
            	double[] minDistance = new double[map.length];//store the minimal distances from start to other stops
            	for(int i = 0; i<map.length; i++) {  //initialize data
            		isVisited[i] = false;
            		minDistance[i] = max;
            	}
            	int startIndex = 0;
            	int endIndex = 0;
            	//mark the start stop and the end stop
            	for(int i=0;i<all.size();i++) {   
            		if(all.get(i).equals(start)) {
            		    minDistance[i]=0;
            		    isVisited[i]=true;
            		    startIndex = i;
            		}
            		if(all.get(i).equals(end)) {
            			endIndex = i;
            		}
            	}
            	int unVisitedNum = map.length;  //record the number of unvisited stops
            	int indexNow = startIndex;  //store the index of the stop of minimal distance
            	int[] pre = new int[map.length];
            	while(unVisitedNum > 0 && indexNow!=endIndex) {
            		double min = max;
            		//find the minimal in the group of unvisited stops
            		for(int i = 0; i<map.length; i++) {   
            			if(!isVisited[i]) {
            				if(minDistance[i]<min) {
            				    min = minDistance[i];
            				    indexNow = i;  
            				}
            			}
            		}
            		isVisited[indexNow] = true;
            		unVisitedNum--;
            		//update the distance of the start stop to other stops
            		for(int j=0; j<map.length; j++) {
            			if(!isVisited[j]) {     
            	            if(minDistance[indexNow] + map[indexNow][j] < minDistance[j]) {  
            	    		    minDistance[j] = minDistance[indexNow] + map[indexNow][j];
            	    		    pre[j] = indexNow;
            	            }
            			}
            	    }
            	}
            	returnBestRoute(startIndex, endIndex, all, pre, best);
            	return best;
            }  
        	
        	private void returnBestRoute(int startIndex, int endIndex, ArrayList<String> all, int[] pre, ArrayList<String>best) {
            	if(endIndex==startIndex) {
            		best.add(all.get(endIndex));
            		return;
            	}
            	returnBestRoute(startIndex, pre[endIndex], all, pre, best);
            	best.add(all.get(endIndex));
            }
	
        	private boolean inSameRoute(String s1 , String s2) {
            	for(Route r : this.routes) {
            		boolean b1 = false;
            		boolean b2 = false;
            		for(Interval i : r.getIntevals()) {
            			for(String s : i.getStops()) {
            				if(s1.equals(s)) {
            					b1 = true;
            				}
            				else if(s2.equals(s)) {
            					b2 = true;
            				}
            			}
            		}
            		if(b1 && b2) {
            			return true;
            		}
            	}
            	return false;
            }
            
            /**
             * get the route by two stops
             * @param s1 stop1
             * @param s2 stop2
             * @return
             */
        	private String getRouteByTwoStops(String s1, String s2) {
            	for(String r1 : getAllRoutes(s1)) {
            		for(String r2 : getAllRoutes(s2)) {
            			if(r1.equals(r2)) {
            				return r2;
            			}
            		}
            	}
            	return null;
            }
            
            /**
             * print the best route standardly
             * @param start the start stop
             * @param end the end stop
             */
        	private String printBestPath(String start, String end) {
            	ArrayList<String> path = getBestPath(start, end);
            	String result = "乘" + getRouteByTwoStops(path.get(0), path.get(1)) + "从" +"["+ start + "]"+" 到[ ";
            	for(int i = 2; i<path.size(); i++) {
            		if((!inSameRoute(path.get(i), path.get(i-2)))||(inSameRoute(path.get(i), path.get(i-2))&&!getRouteByTwoStops(path.get(i), path.get(i-2)).equals(path.get(i-2)))) {
            			if(!getRouteByTwoStops(path.get(i), path.get(i-1)).equals(getRouteByTwoStops(path.get(i-1), path.get(i-2)))) {
            			result = result +path.get(i-1)+"], 换乘" + getRouteByTwoStops(path.get(i-1), path.get(i)) + "从" + "["+path.get(i-1) +"]"+ " 到 [";
            			}
            			}
            	}
            	result = result + end+"]" ;
            	return result;
            }
            
            /**
             * get the distance of two stops
             * @param s1 stop1
             * @param s2 stop2
             * @return
             */
        	private double getDistance(String s1, String s2) {
            	for(Route r : this.routes) {
            		for(Interval i : r.getIntevals()) {
            			if((i.getStops()[0].equals(s1) && i.getStops()[1].equals(s2)) || (i.getStops()[0].equals(s2) && i.getStops()[1].equals(s1))){
            				return i.getDistance();
            			}
            		}
            	}
            	return 0;
            }
            
            /**
             * get the distance of a path
             * @param path an array of all the stops of the path
             * @return
             */
        	private double getDistance(ArrayList<String> path) {
            	double dsum = 0;
            	for(int i = 1; i<path.size(); i++) {
            		dsum = dsum + getDistance(path.get(i), path.get(i-1));
            	}
            	return dsum;
            }
            
            /**
             * calculate the fee of the path
             * @param path an array of the stops of the path
             * @param type the type of payment
             * @return
             */
        	private double countPath(ArrayList<String> path, String type) {
            	Pay p = null;
            	if(type.equals("RegularPay")) {
            		return 0;
            	}
            	else if(type.equals("UsualPay")) {
            		p = new UsualPay(getDistance(path));
            	}
            	else if(type.equals("CardPay")) {
            		p = new CardPay(getDistance(path));
            	}
            	return p.count();
            }
            
		    
		public static void main(String[] args) 
		{
			Test t = new Test();
			t.readFile();
			 for (Route route : t.routes) 
			 {
			        System.out.println("线路: " + route.getName());
			        for (Interval interval : route.getIntevals()) 
			        {
			            System.out.println(  Arrays.toString(interval.getStops()) + "，距离: " + interval.getDistance());
			        }
			  }
			 //第一问
			 System.out.println("第一问：");
		      List<TransferStation> transferStations = t.identifyTransferStations();
		        for (TransferStation ts : transferStations)
		        {
		            System.out.println(ts);
		        }
		     //第二问
		        System.out.println("第二问：");
		        Scanner scanner = new Scanner(System.in);
		    try 
		    	{
		            System.out.print("请输入站点名：");
		            String stationName = scanner.next();
		            System.out.print("请输入距离限制：");
		            double n = scanner.nextDouble();

		            List<Object[]> nearbyStations = t.getNearbyStations(stationName, n);
		            System.out.println("距离" + stationName + "站小于" + n + "的所有站点为：");
		            for (Object[] stationInfo : nearbyStations) 
		            {
		                System.out.println("<" + stationInfo[0] + ", " + stationInfo[1] + ", " + stationInfo[2] + ">");
		            }

		        } 
		    catch (InputMismatchException e) 
		    	{
		            System.out.println("输入不合规，请重新运行程序并输入正确的站点名和距离限制。");
		        }
		    
		    //第三问
		    SubwayGraph graph = new SubwayGraph();
		    for (Route route : t.routes)
		    {
		    	for (Interval interval : route.getIntevals())
		    	{
		    			graph.addConnection(interval.getStops()[0],interval.getStops()[1]);
		    	}
		    }
		    
		    	System.out.println("第三问：");
	           System.out.print("请输入起始站点名：");
	            String startStation = scanner.next();
	            System.out.print("请输入终点站点名：");
	            String endStation = scanner.next();
	    	
	            List<List<String>> paths = graph.findAllPaths(startStation, endStation);
	            for (List<String> path : paths) 
	            {
	            	System.out.println(path);
	            }

		    //第四问
	            System.out.println("第四问：");
	            System.out.print("请输入起始站点名：");
	            String startStation_2 = scanner.next();
	            System.out.print("请输入终点站点名：");
	            String stopStation = scanner.next();
	            System.out.print("最短路径为：");
	            System.out.println(t.getBestPath( startStation_2,  stopStation));
	            
	            //第五问
	            System.out.println("第五问：");
	            System.out.print("乘车线路为：");
	            System.out.println(t.printBestPath(startStation_2, stopStation));
	            //第六问
	            System.out.println("第六问：");
	            t.getDistance(t.getBestPath( startStation_2,  stopStation));
	            System.out.println("该最短路径普通票价为："+t.countPath(t.getBestPath( startStation_2,  stopStation), "UsualPay")+"元");
	            //第七问
	            System.out.println("第七问：");
	            System.out.println("该最短路径武汉通票价为："+t.countPath(t.getBestPath( startStation_2,  stopStation), "CardPay")+"元");
	            System.out.println("该最短路径日票票价为："+t.countPath(t.getBestPath( startStation_2,  stopStation), "RegularPay")+"元");

		    
		    scanner.close();
		 }
		
}