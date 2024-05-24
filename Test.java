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
		    public List<Object[]> getNearbyStations(String stationName, double n) {
		        List<Object[]> nearbyStations = new ArrayList<>();
		        for (Route route : this.routes) {
		            for (Interval interval : route.getIntevals()) {
		                for (String stop : interval.getStops()) {
		                    if (stop.equals(stationName)) {
		                        for (Interval otherInterval : route.getIntevals()) {
		                            for (String otherStop : otherInterval.getStops()) {
		                                if (!otherStop.equals(stationName) && Math.abs(interval.getDistance() - otherInterval.getDistance()) <= n
		                                		&& interval.getDistance() - otherInterval.getDistance() > 0) {
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
		  
		public static void main(String[] args) 
		{
			Test t = new Test();
			t.readFile();
			 for (Route route : t.routes) {
			        System.out.println("线路: " + route.getName());
			        for (Interval interval : route.getIntevals()) {
			            System.out.println(  Arrays.toString(interval.getStops()) + "，距离: " + interval.getDistance());
			        }
			    }
		      List<TransferStation> transferStations = t.identifyTransferStations();
		        for (TransferStation ts : transferStations)
		        {
		            System.out.println(ts);
		        }
		    try (Scanner scanner = new Scanner(System.in))
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
		 }
		
}