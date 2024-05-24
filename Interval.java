package subwaysystem;

public class Interval
{
	private String[] stations = new String[2];
	private double distance;
	
	public Interval(String s1, String s2, double d)
	{
		this.stations[0] = s1;
		this.stations[1] = s2;
		this.distance = d;
	}
	
	public String[] getStops() 
	{
		return stations;
	}
	public void setStops(String[] stops) 
	{
		this.stations = stops;
	}
	public double getDistance() 
	{
		return distance;
	}
	public void setDistance(double distance)
	{
		this.distance = distance;
	}
	
}
