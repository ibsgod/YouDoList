package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Tutorial extends Scene
{
    int index = 0;
    BorderPane bp;
    double width;
    double height;
    Stage primaryStage;
    HashMap<String, StackPane> buttons = new HashMap<String, StackPane>();
    HashMap<StackPane, Timer> btnTimers= new HashMap<StackPane, Timer>();
    Label topLbl = new Label();
    Label botLbl = new Label();
    String[] topText = {"Welcome to YouDoList!",
            "How does it work?",
            "Scheduling lists and activities",
            "Storing lists",
            "Startup",
            "Startup",
            "Startup",
            "Startup"};
    String[] botText = {"This is an app that helps you keep lists of things you need to do. Let's go through some basic information about this program.",
            "Simple! You create lists, fill them up with activities, and check them off as you complete them!",
            "You can schedule lists for chosen dates, and activities at chosen times. When that time rolls around, you will receive notifications reminding you to complete those tasks!",
            "Your lists will be stored in a file on your computer (probably in your documents), so you can go back and edit them by clicking \"Edit List\" whenever you want to.",
            "It is highly recommended that you set this program to start on Windows startup, so that it'll be up and running as soon as you turn on your computer. The following steps will show you how to do this.",
            "First, press the Windows Key + R to open the \"Run\" dialog box, then type \"shell:startup\" before hitting OK.",
            "A file explorer window will open. Now, open a new file explorer and locate the .exe that this program is running from (probably in your downloads).",
            "Now holding right click, drag the .exe into the startup folder. Then select \"Create shortcut here\". And voila! YouDoList will now open when you start your computer."};
    String[] images = {"welcome.gif", "2.gif", "3.gif", "4.gif", "cool.gif", "startup1.png", "5.png", "6.gif"};
    ImageView i;

    public Tutorial(Parent parent, double width, double height, Stage primaryStage)
    {
        super(parent, width, height);
        bp = (BorderPane) parent;
        bp.resize(width, height);
        this.width = width;
        this.height = height;
        this.primaryStage = primaryStage;
        start();
    }
    public void start()
    {
        topLbl.setText(topText[index]);
        botLbl.setText(botText[index]);
        botLbl.setWrapText(true);
        StackPane topBarStuff = new StackPane();
        Rectangle topbar = new Rectangle(width, 100);
        topbar.setVisible(true);
        Stop[] stops = new Stop[] { new Stop(0, Main.colors[Main.theme][2]), new Stop(1, Main.colors[Main.theme][1])};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        topbar.setFill(lg1);
        topLbl.setFont(new Font("Microsoft Yahei UI Light", 40));
        topLbl.setTextFill(Color.WHITE);
        botLbl.setFont(new Font("Microsoft Yahei UI Light", 25));
        botLbl.setTextFill(Color.BLACK);
        botLbl.setTextAlignment(TextAlignment.CENTER);
        botLbl.setPadding(new Insets(10));
        HBox topCent = new HBox(topLbl);
        topCent.setAlignment(Pos.CENTER);
        topBarStuff.getChildren().addAll(topbar, topCent);
        bp.setTop(topBarStuff);
        getStylesheets().add(getClass().getResource("Squidward.css").toExternalForm());
        i = new ImageView(new Image(getClass().getResource(images[index]).toString()));
        makeButton("next", 100, 40, Color.web("a9baf5"), "Next");
        makeButton("prev", 100, 40, Color.web("a9baf5"), "Previous");
        HBox moveBtns = new HBox(buttons.get("prev"), buttons.get("next"));
        moveBtns.setSpacing(100);
        moveBtns.setAlignment(Pos.CENTER);
        VBox centText = new VBox(i, botLbl, moveBtns);
        centText.setSpacing(15);
        centText.setAlignment(Pos.CENTER);
        bp.setCenter(centText);
        buttons.get("next").setOnMouseClicked(e ->
        {
            if (index >= images.length - 2)
            {
                ((Label)((StackPane)(buttons.get("next").getChildren().get(0))).getChildren().get(1)).setText("Finish");
            }
            else
            {
                ((Label)((StackPane)(buttons.get("next").getChildren().get(0))).getChildren().get(1)).setText("Next");
            }
            if (index == images.length - 1)
            {
                primaryStage.setScene(new Menu(new BorderPane(), 500, 500, primaryStage));
                primaryStage.centerOnScreen();
                return;
            }
            index ++;
            i.setImage(new Image(getClass().getResource(images[index]).toString()));
            topLbl.setText(topText[index]);
            botLbl.setText(botText[index]);
        });
        buttons.get("prev").setOnMouseClicked(e ->
        {
            if (index == 0)
            {
                return;
            }
            index --;
            i.setImage(new Image(getClass().getResource(images[index]).toString()));
            topLbl.setText(topText[index]);
            botLbl.setText(botText[index]);
        });
    }
    public void makeButton(String name, int width, int height, Color color, String string)
    {
        StackPane hitbutton = new StackPane();
        StackPane button = new StackPane();
        Label label = new Label(string);
        label.setVisible(true);
        label.setFont(new Font("Microsoft Yahei UI Light", height/2.5));
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
        Stop[] stops = new Stop[] { new Stop(0, Color.web("9cb0f7", 0.4)), new Stop(1, Color.web("2049d6"))};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        fd.setFill(lg1);
        Rectangle hb = new Rectangle(width, height + (height/5));
        hb.setFill(Color.web("FFFFFF", 0.0));
        hb.setArcHeight(10);
        hb.setArcWidth(10);
        hb.setVisible(true);
        hb.setTranslateY(height/-10);
        button.getChildren().addAll(r, label, fd);
        hitbutton.getChildren().addAll(button, hb);
        buttons.put(name, hitbutton);
        btnTimers.put(button, new Timer());
        fd.setOpacity(0.0);
        buttonHover(button, fd, hb);
    }

    public void buttonHover(StackPane button, Rectangle fd, Rectangle hb)
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
