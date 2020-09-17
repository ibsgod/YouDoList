package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application
{
    static ArrayList<DoList> allLists = new ArrayList<DoList>();
    static Stage primaryStage;
    static JSONObject mainJson = new JSONObject();
    static String home = System.getProperty("user.home") + "/Documents/youdolists.json";
    static File file;
    static Color[][] colors = new Color[3][4];
    static int theme = 0;
    static int remindTime = 1;
    static MediaPlayer mediaPlayer;
    static MediaPlayer soundPlayer;
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        colors[0][0] = Color.web("e1e7f7");
        colors[0][1] = Color.web("9cb0f7");
        colors[0][2] = Color.web("2049d6");
        colors[0][3] = Color.web("a9baf5");
        colors[1][0] = Color.web("e1f7e7");
        colors[1][1] = Color.web("9cf7b6");
        colors[1][2] = Color.web("20d672");
        colors[1][3] = Color.web("a9f5cf");
        colors[2][0] = Color.web("f7e1e7");
        colors[2][1] = Color.web("f79c9c");
        colors[2][2] = Color.web("d62020");
        colors[2][3] = Color.web("f5a9b6");
        file = new File(home);
        if (file.createNewFile())
        {
            primaryStage.setScene(new Tutorial(new BorderPane(), 650, 650, primaryStage));
        }
        else
        {
            System.out.println("loaded");
            readJson();
            primaryStage.setScene(new Menu(new BorderPane(), 500, 500, primaryStage));
        }
        //primaryStage.setScene(new Tutorial(new BorderPane(), 650, 650, primaryStage));
        this.primaryStage = primaryStage;
        primaryStage.setResizable(false);
        primaryStage.setTitle("oonga boonga");
        primaryStage.show();
        Media media = new Media(getClass().getResource("jazz.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        if (!primaryStage.isIconified())
        {
            mediaPlayer.play();
        }
        mediaPlayer.setVolume(0.5);
        int milisInAMinute = 60000;
        long time = System.currentTimeMillis();
        primaryStage.setOnCloseRequest(e ->
        {
            System.exit(0);
            updateJson();
        });
        primaryStage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(primaryStage.isIconified())
                {
                    mediaPlayer.pause();
                }
                else
                {
                    mediaPlayer.play();
                }
            }
        });
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                try
                {
                    for (int i = 0; i < allLists.size(); i++) {
                        DoList currList = allLists.get(i);
                        if (currList.active && (currList.dateSet.equals("noDate")
                                || currList.dateSet.equals("specDate") && LocalDate.now().isEqual(currList.date)
                                || currList.dateSet.equals("ongoingDate") && LocalDate.now().compareTo(currList.date) >= 0 && LocalDate.now().compareTo(currList.toDate) <= 0))
                        {
                            for (int j = 0; j < currList.listItems.size(); j++) {
                                ListItem currItem = currList.listItems.get(j);
                                if (currItem.timeSet && !currItem.done) {
                                    if (currItem.hour == LocalDateTime.now().getHour() && currItem.minute == LocalDateTime.now().getMinute()) {
                                        TrayIconDemo td = new TrayIconDemo();
                                        td.displayTray(currItem.itemName, currItem.description);
                                    }
                                    int currTime = currItem.hour * 60 + currItem.minute;
                                    int localTime = LocalDateTime.now().getHour() * 60 + LocalDateTime.now().getMinute();
                                    if ((localTime - currTime) % remindTime == 0) {
                                        TrayIconDemo td = new TrayIconDemo();
                                        td.displayTray(currItem.itemName, currItem.description);
                                    }

                                }
                            }
                        }
                        else if (currList.active && (currList.dateSet.equals("specDate") && LocalDate.now().compareTo(currList.date) > 0
                                || currList.dateSet.equals("ongoingDate") && LocalDate.now().compareTo(currList.toDate) > 0))
                        {
                            for (int j = 0; j < currList.listItems.size(); j++) {
                                ListItem currItem = currList.listItems.get(j);
                                if (!currItem.done)
                                {
                                    currItem.due = true;
                                    currList.hasdue = true;
                                }
                            }
                        }
                    }
                }
                catch (Exception e) { }
            }
        }, time % milisInAMinute, milisInAMinute);

// This will update for the current minute, it will be updated again in at most one minute.
    }


    public static void main(String[] args)
    {
        launch();
    }
    public static void updateJson ()
    {
        System.out.println("update");
        FileWriter fileWriter;
        BufferedWriter bw = null;
        OutputStream os;
        OutputStreamWriter osw;
        try
        {
            fileWriter = new FileWriter(file, false);
            bw = new BufferedWriter(fileWriter);

            JSONArray listArray = new JSONArray();
            for (int i = 0; i < allLists.size(); i++)
            {
                JSONObject listInfo = new JSONObject();
                JSONArray itemArray = new JSONArray();
                for (int j = 0; j < allLists.get(i).listItems.size(); j++)
                {
                    JSONObject item = new JSONObject();
                    item.put("name", allLists.get(i).listItems.get(j).itemName);
                    item.put("description", allLists.get(i).listItems.get(j).description);
                    item.put("done", allLists.get(i).listItems.get(j).done);
                    item.put("hour", allLists.get(i).listItems.get(j).hour);
                    item.put("minute", allLists.get(i).listItems.get(j).minute);
                    item.put("timeSet", allLists.get(i).listItems.get(j).timeSet);
                    itemArray.put(new JSONObject(item.toString()));
                }
                listInfo.put("items", new JSONArray(itemArray.toString()));
                listInfo.put("dateSet", allLists.get(i).dateSet);
                if (allLists.get(i).date != null)
                {
                    listInfo.put("year", allLists.get(i).date.getYear());
                    listInfo.put("month", allLists.get(i).date.getMonthValue());
                    listInfo.put("day", allLists.get(i).date.getDayOfMonth());
                }
                if (allLists.get(i).toDate != null)
                {
                    listInfo.put("toyear", allLists.get(i).toDate.getYear());
                    listInfo.put("tomonth", allLists.get(i).toDate.getMonthValue());
                    listInfo.put("today", allLists.get(i).toDate.getDayOfMonth());
                }
                listInfo.put("done", allLists.get(i).done);
                listInfo.put("active", allLists.get(i).active);
                listInfo.put("name", allLists.get(i).name);
                listArray.put(new JSONObject(listInfo.toString()));
            }
            mainJson.put("lists", new JSONArray(listArray.toString()));
            mainJson.put("theme", Main.theme);
            mainJson.put("remind", Main.remindTime);
            bw.write(mainJson.toString());
            bw.close();

        }
        catch (Exception f)
        {
            f.printStackTrace();
        }
    }
    public void readJson()
    {
        FileReader fileReader;
        BufferedReader br = null;
        InputStream in;
        InputStreamReader inr = null;
        try
        {
            fileReader = new FileReader(file);
            br = new BufferedReader(fileReader);
            String s = br.readLine();
            if (s == null)
            {
                return;
            }
            JSONObject mj = new JSONObject(s);
            Main.theme = mj.getInt("theme");
            Main.remindTime = mj.getInt("remind");
            JSONArray lists = mj.getJSONArray("lists");
            allLists.clear();
            for (int i = 0; i < lists.length(); i++)
            {
                try {
                    JSONObject listInfo = lists.getJSONObject(i);
                    JSONArray itemArray = listInfo.getJSONArray("items");
                    ArrayList<ListItem> listItems = new ArrayList<ListItem>();
                    for (int j = 0; j < itemArray.length(); j++) {
                        JSONObject item = itemArray.getJSONObject(i);
                        ListItem l = new ListItem(item.getString("name"), item.getBoolean("done"));
                        l.description = item.getString("description");
                        l.timeSet = item.getBoolean("timeSet");
                        l.hour = item.getInt("hour");
                        l.minute = item.getInt("minute");
                        listItems.add(l);
                    }
                    DoList list = new DoList(listInfo.getString("name"), listItems);
                    list.dateSet = listInfo.getString("dateSet");
                    if (!list.dateSet.equals("noDate")) {
                        list.date = LocalDate.of(listInfo.getInt("year"), listInfo.getInt("month"), listInfo.getInt("day"));
                    }
                    if (list.dateSet.equals("ongoingDate")) {
                        list.toDate = LocalDate.of(listInfo.getInt("toyear"), listInfo.getInt("tomonth"), listInfo.getInt("today"));
                    }
                    list.done = listInfo.getBoolean("done");
                    list.active = listInfo.getBoolean("active");
                    allLists.add(list);
                }
                catch(Exception e){}
            }
            br.close();
        }
        catch (Exception f)
        {
            f.printStackTrace();
        }

    }
}
