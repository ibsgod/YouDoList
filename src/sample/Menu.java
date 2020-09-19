package sample;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Menu extends Scene
{
    BorderPane bp;
    double width;
    double height;
    Stage primaryStage;
    static ArrayList<DoList> allLists = new ArrayList<DoList>();
    HashMap<String, StackPane> buttons = new HashMap<String, StackPane>();
    HashMap<StackPane, Timer> btnTimers= new HashMap<StackPane, Timer>();
    public Menu(Parent parent, double width, double height, Stage primaryStage)
    {
        super(parent, width, height, Main.colors[Main.theme][0]);
        bp = (BorderPane) parent;
        bp.resize(width, height);
        this.width = width;
        this.height = height;
        this.primaryStage = primaryStage;
        start();
    }
    public void start()
    {
        switch (Main.theme)
        {
            case 0:
                getStylesheets().add(getClass().getResource("blue.css").toExternalForm());
                break;
            case 1:
                getStylesheets().add(getClass().getResource("green.css").toExternalForm());
                break;
            case 2:
                getStylesheets().add(getClass().getResource("red.css").toExternalForm());
                break;
        }
        makeButton("newList", 110, 110, Main.colors[Main.theme][3], new Image(getClass().getResource("plus.png").toString()), "New List");
        makeButton("myLists", 110, 110, Main.colors[Main.theme][3], new Image(getClass().getResource("lists.png").toString()), "My Lists");
        makeButton("settings", 110, 110, Main.colors[Main.theme][3], new Image(getClass().getResource("settings.png").toString()), "Settings");
        buttons.get("newList").setOnMouseClicked(e ->
        {
            primaryStage.setScene(new ListCreator(new BorderPane(), 900, 650, primaryStage, null));
            primaryStage.centerOnScreen();
        });
        buttons.get("myLists").setOnMouseClicked(e ->
        {
            primaryStage.setScene(new ListViewer(new BorderPane(), 900, 650, primaryStage, null));
            primaryStage.centerOnScreen();
        });
        buttons.get("settings").setOnMouseClicked(e ->
        {
            primaryStage.setScene(new Settings(new BorderPane(), 500, 500, primaryStage));
            primaryStage.centerOnScreen();
        });
        Pane topBarStuff = new Pane();
        Rectangle topbar = new Rectangle(width, 100);
        topbar.setVisible(true);
        Stop[] stops = new Stop[] { new Stop(0, Main.colors[Main.theme][2]), new Stop(1, Main.colors[Main.theme][1])};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        topbar.setFill(lg1);
        Label mainLbl = new Label("YouDoList");
        mainLbl.setTranslateX(215);
        mainLbl.setTranslateY(18);
        mainLbl.setFont(new Font("Microsoft Yahei UI Light", 40));
        mainLbl.setTextFill(Color.WHITE);
        mainLbl.setAlignment(Pos.CENTER_LEFT);
        Label notifLabel = new Label();
        notifLabel.setFont(new Font("Microsoft Yahei UI Light", 20));
        HBox notifBox = new HBox(notifLabel);
        int dones = 0;
        for (int i = 0; i < Main.allLists.size(); i++)
        {
            if (Main.allLists.get(i).hasdue)
            {
                for (int j = 0; j < Main.allLists.get(i).listItems.size(); j++)
                {
                    if (Main.allLists.get(i).listItems.get(j).due)
                    {
                        dones ++;
                    }
                }
            }
        }
        notifLabel.setText("You have " + dones + " overdue tasks.");
        if (dones == 0)
        {
            notifLabel.setText(notifLabel.getText() + " Nice!");
        }
        notifBox.setAlignment(Pos.BOTTOM_RIGHT);
        notifBox.setTranslateY(110);
        notifBox.setTranslateX(10);
        topBarStuff.getChildren().addAll(topbar, mainLbl, notifBox);
        HBox hbox = new HBox(buttons.get("newList"), buttons.get("myLists"), buttons.get("settings"));
        hbox.setSpacing(30);
        hbox.setAlignment(Pos.CENTER);
        bp.setCenter(hbox);
        hbox.setTranslateY(-20);
        bp.setTop(topBarStuff);
        Label bottomLbl = new Label("By Sankeeth Ganeswaran.");
        bottomLbl.setTextFill(Color.web("000000", 0.5));
        makeButton("quit", 100, 50, Main.colors[Main.theme][3], "Quit");
        buttons.get("quit").setTranslateY(-30);
        buttons.get("quit").setTranslateX(-250);
        buttons.get("quit").setOnMouseClicked(e ->
        {
            Main.updateJson();
            System.exit(0);
        });
        HBox bbox = new HBox(buttons.get("quit"), bottomLbl);
        bbox.setAlignment(Pos.TOP_RIGHT);
        bottomLbl.setTranslateX(-10);
        bottomLbl.setTranslateY(10);
        bp.setBottom(bbox);
        getStylesheets().add(getClass().getResource("Squidward.css").toExternalForm());
        TranslateTransition tt = new TranslateTransition(Duration.millis(2000), mainLbl);
        tt.setByX(-200);
        tt.setCycleCount(1);
        tt.setAutoReverse(true);
        tt.play();
        for (int i = 0; i < bp.getChildren().size(); i++)
        {
            if (bp.getChildren().get(i) == bp.getBottom())
            {
                continue;
            }
            FadeTransition ft = new FadeTransition(Duration.millis(3000), bp.getChildren().get(i));
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.setCycleCount(1);
            ft.setAutoReverse(true);
            ft.play();
        }
    }
    public void makeButton(String name, int width, int height, Color color, Image image, String string)
    {
        StackPane hitbutton = new StackPane();
        StackPane button = new StackPane();
        ImageView img1 = new ImageView(image);
        Label label = new Label(string);
        label.setOpacity(0);
        label.setVisible(true);
        label.setFont(new Font("Microsoft Yahei UI Light", 20));
        label.setTextFill(Color.BLACK);
        Rectangle r = new Rectangle(width, height);
        r.setArcHeight(20);
        r.setArcWidth(20);
        r.setVisible(true);
        r.setFill(color);
        Rectangle fd = new Rectangle(width, height);
        fd.setArcHeight(20);
        fd.setArcWidth(20);
        fd.setVisible(true);
        Stop[] stops = new Stop[] { new Stop(0, Main.colors[Main.theme][1]), new Stop(1, Main.colors[Main.theme][2])};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        fd.setFill(lg1);
        Rectangle hb = new Rectangle(width, height + (height/5));
        hb.setFill(Color.web("FFFFFF", 0.0));
        hb.setArcHeight(20);
        hb.setArcWidth(20);
        hb.setVisible(true);
        hb.setTranslateY(height/-10);
        button.getChildren().addAll(r, img1, fd);
        label.setTranslateY(height/2 + label.getHeight() + height/5);
        hitbutton.getChildren().addAll(button, label, hb);
        buttons.put(name, hitbutton);
        btnTimers.put(button, new Timer());
        fd.setOpacity(0.0);
        buttonHover(button, fd, hb, label);
    }
    public void makeButton(String name, int width, int height, Color color, String string)
    {
        StackPane hitbutton = new StackPane();
        StackPane button = new StackPane();
        Label label = new Label(string);
        label.setVisible(true);
        label.setFont(new Font("Microsoft Yahei UI Light", height / 2.5));
        label.setTextFill(Color.BLACK);
        Rectangle r = new Rectangle(width, height);
        r.setArcHeight(10);
        r.setArcWidth(10);
        r.setVisible(true);
        r.setFill(color);
        Rectangle fd = new Rectangle(width, height);
        fd.setArcHeight(10);
        fd.setArcWidth(10);
        fd.setVisible(true);
        Stop[] stops = new Stop[]{new Stop(0, Main.colors[Main.theme][1]), new Stop(1, Main.colors[Main.theme][2])};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        fd.setFill(lg1);
        Rectangle hb = new Rectangle(width, height + (height / 5));
        hb.setFill(Color.web("FFFFFF", 0.0));
        hb.setArcHeight(10);
        hb.setArcWidth(10);
        hb.setVisible(true);
        hb.setTranslateY(height / -10);
        button.getChildren().addAll(r, label, fd);
        hitbutton.getChildren().addAll(button, hb);
        buttons.put(name, hitbutton);
        btnTimers.put(button, new Timer());
        fd.setOpacity(0.0);
        buttonHover(button, fd, hb, null);
    }
    public void buttonHover(StackPane button, Rectangle fd, Rectangle hb, Label l)
    {
        hb.setOnMouseEntered(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent me)
            {
                setCursor(Cursor.HAND); //Change cursor to hand
                try
                {
                    btnTimers.get(button).cancel();
                    btnTimers.put(button, new Timer());
                    btnTimers.get(button).scheduleAtFixedRate((new TimerTask() {

                        @Override
                        public void run()
                        {
                            Platform.runLater(() ->
                            {
                                if (fd.getOpacity() < 0.4)
                                {
                                    fd.setOpacity(Math.min(0.5, fd.getOpacity() + 0.01));
                                }
                                if (l != null && l.getOpacity() < 1)
                                {
                                    l.setOpacity(Math.min(1, l.getOpacity() + 0.01));
                                }
                                if (button.getTranslateY() > -19.5)
                                {
                                    button.setTranslateY((-20 + button.getTranslateY())/2);
                                }
                                else
                                {
                                    button.setTranslateY(-20);
                                }
                            });
                        }
                    }), 0, 10);
                }
                catch(Exception e){};

            }
        });
        hb.setOnMouseExited(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent me)
            {
                setCursor(Cursor.DEFAULT);
                try
                {
                    btnTimers.get(button).cancel();
                    btnTimers.put(button, new Timer());
                    btnTimers.get(button).scheduleAtFixedRate((new TimerTask() {

                        @Override
                        public void run()
                        {
                            Platform.runLater(() ->
                            {
                                if (fd.getOpacity() > 0)
                                {
                                    fd.setOpacity(Math.max(0, fd.getOpacity() - 0.007));
                                }
                                if (l != null && l.getOpacity() > 0)
                                {
                                    l.setOpacity(Math.max(0, l.getOpacity() - 0.01));
                                }
                                if (button.getTranslateY() < -0.1)
                                {
                                    button.setTranslateY((button.getTranslateY())/2);
                                }
                                else
                                {
                                    button.setTranslateY(0);
                                }
                            });
                        }
                    }), 0, 10);
                }
                catch(Exception e){};
            }
        });

    }
}
