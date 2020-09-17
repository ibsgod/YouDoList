package sample;

public class ListItem
{
    String itemName;
    boolean done = false;
    String description;
    boolean due = false;
    boolean timeSet = false;
    int hour;
    int minute;
    ListItem(String itemName, boolean done)
    {
        this.itemName = itemName;
        this.done = done;
    }
    public void setDescription(String desc)
    {
        description = desc;
    }
}
