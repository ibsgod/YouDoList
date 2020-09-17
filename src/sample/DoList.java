package sample;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;

public class DoList implements Comparable <DoList>
{
    ArrayList<ListItem> listItems;
    String name;
    LocalDate date, toDate;
    String dateSet = "noDate";
    boolean active = true;
    boolean hasdue = false;
    boolean done = false;
    public DoList(String name, ArrayList<ListItem> listItems)
    {
        this.name = name;
        this.listItems = listItems;
        JSONObject jo = new JSONObject();
        jo.put("name", "jon doe");
        jo.put("age", "22");
        jo.put("city", "chicago");

    }


    @Override
    public int compareTo(DoList o)
    {
        int thisDate = 0;
        int thatDate = 0;
        if (!dateSet.equals("noDate"))
        {
            thisDate += date.getYear() * 365 + date.getMonthValue() * 31 + date.getDayOfMonth();
        }
        if (!o.dateSet.equals("noDate"))
        {
            thatDate += o.date.getYear() * 365 + o.date.getMonthValue() * 31 + o.date.getDayOfMonth();
        }
        return thisDate - thatDate;
    }
}
