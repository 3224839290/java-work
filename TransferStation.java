package subwaysystem;

import java.util.ArrayList;
import java.util.List;

public class TransferStation 
{
    private String stationName;
    private List<String> lines = new ArrayList<>();

    public TransferStation(String stationName) 
    {
        this.stationName = stationName;
    }

    public String getStationName() 
    {
        return stationName;
    }

    public void addLine(String line)
    {
        lines.add(line);
    }

    public List<String> getLines()
    {
        return lines;
    }

    @Override
    public String toString() 
    {
        return "<" + stationName + ", " + lines + ">";
    }
}