package sample;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class ListViewer extends Scene {
    Scene currScene = this;
    BorderPane bp;
    double width;
    double height;
    DoList newList;
    Stage primaryStage;
    Timer t = new Timer();
    HashMap<String, StackPane> buttons = new HashMap<String, StackPane>();
    HashMap<DoList, StackPane> buttonList = new HashMap<DoList, StackPane>();
    HashMap<StackPane, DoList> invbuttonList = new HashMap<StackPane, DoList>();
    HashMap<StackPane, Timer> btnTimers = new HashMap<StackPane, Timer>();
    StackPane selected = null;
    Calendar cal = Calendar.getInstance();
    MediaPlayer sfx;
    FlowPane fp = new FlowPane();
    int listNum = 0;
    VBox right = new VBox();
    StackPane dateEdit = null;
    StackPane contEdit = null;
    StackPane addList = null;
    StackPane deleteList = null;
    Label nameLbl;
    Label dateLbl;
    CheckBox activeCheck = new CheckBox();
    public ListViewer(Parent parent, double width, double height, Stage primaryStage, DoList newList)
    {
        super(parent, width, height);
        bp = (BorderPane) parent;
        bp.resize(width, height);
        this.width = width;
        this.height = height;
        this.newList = newList;
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
        right.setOpacity(0);
        getStylesheets().add(getClass().getResource("Squidward.css").toExternalForm());
        StackPane topBarStuff = new StackPane();
        Rectangle topbar = new Rectangle(width, 100);
        topbar.setVisible(true);
        Stop[] stopss = new Stop[] { new Stop(0, Main.colors[Main.theme][2]), new Stop(1, Main.colors[Main.theme][1])};
        LinearGradient lg11 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stopss);
        topbar.setFill(lg11);
        Label mainLbl = new Label("My Lists");
        mainLbl.setPadding(new Insets(20));
        mainLbl.setTranslateX(200);
        mainLbl.setFont(new Font("Microsoft Yahei UI Light", 40));
        mainLbl.setTextFill(Color.WHITE);
        topBarStuff.setAlignment(Pos.CENTER_LEFT);
        topBarStuff.getChildren().addAll(topbar, mainLbl);
        bp.setTop(topBarStuff);
        TranslateTransition tt = new TranslateTransition(Duration.millis(2000), mainLbl);
        tt.setByX(-200);
        tt.setCycleCount(1);
        tt.setAutoReverse(true);
        tt.play();
        for (int i = 0; i < bp.getChildren().size(); i++)
        {
            FadeTransition ft = new FadeTransition(Duration.millis(3000), bp.getChildren().get(i));
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.setCycleCount(1);
            ft.setAutoReverse(true);
            ft.play();
        }
        if (newList != null)
        {
            Main.allLists.add(newList);
        }
        fp.setHgap(10);
        fp.setVgap(10);
        ScrollPane sp = new ScrollPane(fp);
        fp.setPrefWidth(450);
        fp.setPadding(new Insets(10));
        sp.setPrefWidth(450);
        bp.setLeft(sp);
        makeButton(1, "editDate", 150, 50, Main.colors[Main.theme][3], "Edit date");
        makeButton(1, "editCont", 150, 50, Main.colors[Main.theme][3], "Edit list");
        makeButton(1, "deleteList", 150, 50, Color.web("ff7575"), "Delete list");
        makeButton(2, "addList", 200, 50, Main.colors[Main.theme][3], "Create list");
        makeButton(2, "return", 200, 50, Main.colors[Main.theme][3], "Return to menu");
        addList = buttons.get("addList");
        HBox bottom = new HBox(buttons.get("return"), buttons.get("addList"));
        bottom.setSpacing(10);
        bottom.setAlignment(Pos.CENTER);
        Region r = new Region();
        VBox trueright = new VBox(new VBox(right), r, bottom);
        bp.setCenter(trueright);
        dateEdit = buttons.get("editDate");
        contEdit = buttons.get("editCont");
        deleteList = buttons.get("deleteList");
        Stop[] stops = new Stop[]{new Stop(0, Color.web("fc5656", 0.4)), new Stop(1, Color.web("c91a1a"))};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        ((Rectangle) ((StackPane) (deleteList.getChildren().get(0))).getChildren().get(2)).setFill(lg1);
        nameLbl = new Label();
        nameLbl.setFont(new Font("Microsoft Yahei UI Light", 30));
        nameLbl.setPrefHeight(0);
        dateLbl = new Label();
        dateLbl.setFont(new Font("Microsoft Yahei UI Light", 20));
        right.setPadding(new Insets(15));
        Label activeLbl = new Label("Set active");
        activeLbl.setFont(new Font("Microsoft Yahei UI Light", 20));
        activeCheck.setPrefSize(30,30);
        activeCheck.setDisable(true);
        HBox activeBox = new HBox (activeCheck, activeLbl);
        HBox editBox = new HBox(contEdit, dateEdit);
        editBox.setSpacing(20);
        HBox deleteBox = new HBox(deleteList);
        deleteBox.setAlignment(Pos.CENTER);
        Region re = new Region();
        r.setPrefSize(10, 50);
        re.setPrefSize(10, 100);
        right.getChildren().addAll(nameLbl, dateLbl, editBox, activeBox, r, deleteBox, re);
        if (newList != null)
        {
            Stage dateStage = new Stage();
            BorderPane bpp = new BorderPane();
            selected = buttonList.get(newList);
            DateEditor dateScene = new DateEditor(dateStage, bpp, 450, 160, newList);
            dateScene.lv = this;
            dateStage.setTitle("Set Date?");
            dateStage.setScene(dateScene);
            dateStage.show();
            dateStage.setAlwaysOnTop(true);
        }
        else
        {
            showLists();
        }
        dateEdit.setOnMouseClicked(e ->
        {
            if (selected == null)
            {
                return;
            }
            Stage dateStage = new Stage();
            DateEditor dateScene = new DateEditor(dateStage, new BorderPane(), 450, 160, invbuttonList.get(selected));
            dateScene.lv = this;
            dateStage.setTitle("Set Date?");
            dateStage.setScene(dateScene);
            dateStage.show();
            dateStage.setAlwaysOnTop(true);
        });
        contEdit.setOnMouseClicked(e ->
        {
            if (selected == null)
            {
                return;
            }
            primaryStage.setScene(new ListCreator(new BorderPane(), 900, 650, primaryStage, invbuttonList.get(selected)));
        });
        deleteList.setOnMouseClicked(e ->
        {
            if (selected == null)
            {
                return;
            }
            Stage confirmStage = new Stage();
            BorderPane bpp = new BorderPane();
            Scene confirmScene = new Scene(bpp, 450, 160);
            confirmStage.setTitle("Set Date?");
            confirmStage.setScene(confirmScene);
            currScene = confirmScene;
            confirmStage.initModality(Modality.APPLICATION_MODAL);
            confirmStage.show();
            confirmStage.setAlwaysOnTop(true);
            confirmScene.getStylesheets().add(getClass().getResource("Squidward.css").toExternalForm());
            Label question = new Label("Do you really want to delete this list?");
            HBox questionBox = new HBox(question);
            questionBox.setAlignment(Pos.CENTER);
            questionBox.setTranslateY(30);
            question.setFont(new Font("Microsoft Yahei UI Light", 20));
            bpp.setCenter(question);
            makeButton(2, "yesDelete", 150, 50, Main.colors[Main.theme][3], "Yes!");
            makeButton(2, "noDelete", 150, 50, Main.colors[Main.theme][3], "No!");
            HBox confirmBox = new HBox(buttons.get("yesDelete"), buttons.get("noDelete"));
            confirmBox.setAlignment(Pos.CENTER);
            confirmBox.setSpacing(20);
            bpp.setBottom(confirmBox);
            confirmBox.setTranslateY(-10);
            buttons.get("yesDelete").setOnMouseClicked(f ->
            {
                Main.allLists.remove(invbuttonList.get(selected));
                fp.getChildren().remove(selected);
                selected = null;
                right.setOpacity(0);
                try
                {
                    t.cancel();
                    t = new Timer();
                    t.scheduleAtFixedRate((new TimerTask() {

                        @Override
                        public void run()
                        {
                            Platform.runLater(() ->
                            {
                                if (right.getOpacity() > 0)
                                {
                                    right.setOpacity(Math.max(0, right.getOpacity() - 0.01));
                                }
                            });
                        }
                    }), 0, 10);
                }
                catch(Exception g){};
                confirmStage.close();
                setCursor(Cursor.DEFAULT);
                buttons.remove("noDelete");
                buttons.remove("yesDelete");
            });
            buttons.get("noDelete").setOnMouseClicked(f ->
            {
                confirmStage.close();
                setCursor(Cursor.DEFAULT);
                buttons.remove("noDelete");
                buttons.remove("yesDelete");
            });
            confirmStage.setOnCloseRequest(f ->
            {
                currScene = this;
            });
        });
        activeCheck.setOnAction(e ->
        {
            if (selected == null)
            {
                return;
            }
            invbuttonList.get(selected).active = activeCheck.isSelected();
        });
        addList.setOnMouseClicked(e ->
        {
            primaryStage.setScene(new ListCreator(new BorderPane(), 900, 650, primaryStage,null));
            primaryStage.centerOnScreen();
        });
        buttons.get("return").setOnMouseClicked(e ->
        {
            primaryStage.setScene(new Menu(new BorderPane(), 500, 500, primaryStage));
            primaryStage.centerOnScreen();
        });
    }

    public void makeButton(int list, String name, int width, int height, Color color, String string) {
        StackPane hitbutton = new StackPane();
        StackPane button = new StackPane();
        Label label = new Label(string);
        label.setVisible(true);
        label.setFont(new Font("Microsoft Yahei UI Light", height / 2.5));
        if (list == 0) {
            label.setFont(new Font("Microsoft Yahei UI Light", 20));
        }
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
        buttonHover(button, fd, hb, list);
    }

    public void buttonHover(StackPane button, Rectangle fd, Rectangle hb, int list) {
        hb.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                try {
                    if (hb.getOpacity() > 0 && button.getParent().getOpacity() > 0) {
                        if (selected != null || list != 1)
                        {
                            currScene.setCursor(Cursor.HAND);
                        }
                        btnTimers.get(button).cancel();
                        btnTimers.put(button, new Timer());
                        btnTimers.get(button).scheduleAtFixedRate((new TimerTask() {

                            @Override
                            public void run() {
                                Platform.runLater(() ->
                                {
                                    if (fd.getOpacity() < 0.4) {
                                        fd.setOpacity(Math.min(0.5, fd.getOpacity() + 0.01));
                                    }
                                    if (button.getTranslateY() > -9.5) {
                                        button.setTranslateY((-10 + button.getTranslateY()) / 2);
                                    } else {
                                        button.setTranslateY(-10);
                                    }
                                });
                            }
                        }), 0, 10);
                    }
                } catch (Exception e) {};
                if (list == 0 && selected == null)
                {
                    nameLbl.setText(invbuttonList.get(hb.getParent()).name);
                    if (invbuttonList.get(hb.getParent()).dateSet.equals("specDate"))
                    {
                        dateLbl.setText("Set for: " + invbuttonList.get(hb.getParent()).date.toString());
                    }
                    else if (invbuttonList.get(hb.getParent()).dateSet.equals("ongoingDate"))
                    {
                        dateLbl.setText("Set from: " + invbuttonList.get(hb.getParent()).date.toString() + " to: " + invbuttonList.get(hb.getParent()).toDate.toString());
                    }
                    else
                    {
                        dateLbl.setText("No date set");
                    }
                    activeCheck.setSelected(invbuttonList.get(hb.getParent()).active);
                    try
                    {
                        t.cancel();
                        t = new Timer();
                        t.scheduleAtFixedRate((new TimerTask() {

                            @Override
                            public void run()
                            {
                                Platform.runLater(() ->
                                {
                                    if (right.getOpacity() < 1)
                                    {
                                        right.setOpacity(Math.min(1, right.getOpacity() + 0.01));
                                    }
                                });
                            }
                        }), 0, 10);
                    }
                    catch(Exception g){};
                }
            }
        });
        hb.setOnMouseExited(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent me)
            {
                currScene.setCursor(Cursor.DEFAULT);
                try {
                    btnTimers.get(button).cancel();
                    btnTimers.put(button, new Timer());
                    btnTimers.get(button).scheduleAtFixedRate((new TimerTask()
                    {
                        @Override
                        public void run() {
                            Platform.runLater(() ->
                            {
                                if (fd.getOpacity() > 0) {
                                    fd.setOpacity(Math.max(0, fd.getOpacity() - 0.007));
                                }
                                if (button.getTranslateY() < -0.1) {
                                    button.setTranslateY((button.getTranslateY()) / 2);
                                } else {
                                    button.setTranslateY(0);
                                }
                            });
                        }
                    }), 0, 10);
                }
                catch (Exception e) {}
                if (list == 0 && selected == null)
                {
                    try
                    {
                        t.cancel();
                        t = new Timer();
                        t.scheduleAtFixedRate((new TimerTask() {

                            @Override
                            public void run()
                            {
                                Platform.runLater(() ->
                                {
                                    if (right.getOpacity() > 0)
                                    {
                                        right.setOpacity(Math.max(0, right.getOpacity() - 0.01));
                                    }
                                });
                            }
                        }), 0, 10);
                    }
                    catch(Exception g){};
                }
            }
        });
    }

    public void showLists()
    {
        fp.getChildren().clear();
        System.out.println(fp.getChildren().size());
        Collections.sort(Main.allLists);
        Main.updateJson();
        for (int i = 0; i < Main.allLists.size(); i++)
        {
            makeButton(0, "newList" + listNum, 200, 150, Main.colors[Main.theme][3], Main.allLists.get(i).name);
            fp.getChildren().add(buttons.get("newList" + listNum));
            boolean green = true;
            for (int j = 0; j < Main.allLists.get(i).listItems.size(); j++)
            {
                if (!Main.allLists.get(i).listItems.get(j).done)
                {
                    green = false;
                    break;
                }
            }
            if (green && Main.allLists.get(i).listItems.size() > 0)
            {
                Main.allLists.get(i).done = true;
                ((Rectangle) ((StackPane) (buttons.get("newList" + listNum).getChildren().get(0))).getChildren().get(0)).setFill(Color.web("1aeb4b"));
                Stop[] stops = new Stop[]{new Stop(0, Color.web("9cf7b0", 0.4)), new Stop(1, Color.web("20d649"))};
                LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                ((Rectangle) ((StackPane) (buttons.get("newList" + listNum).getChildren().get(0))).getChildren().get(2)).setFill(lg1);
            }
            buttonList.put(Main.allLists.get(i), buttons.get("newList" + listNum));
            invbuttonList.put(buttons.get("newList" + listNum), Main.allLists.get(i));
            buttons.get("newList" + listNum).setOnMouseClicked(e ->
            {
                if (selected != null)
                {
                    selected.setScaleX(1);
                    selected.setScaleY(1);
                }
                if (selected != (StackPane)((Node) e.getTarget()).getParent())
                {
                    selected = (StackPane)((Node) e.getTarget()).getParent();
                    selected.setScaleX(1.05);
                    selected.setScaleY(1.05);
                    activeCheck.setDisable(false);
                }
                else
                {
                    selected = null;
                    activeCheck.setDisable(true);

                }
            });
            listNum++;
        }
    }
}

