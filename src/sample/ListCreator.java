package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.util.*;

public class ListCreator extends Scene
{
    Stage primaryStage;
    DoList list;
    int transX = 360;
    int transY = 20;
    HashMap<String, StackPane> buttons = new HashMap<String, StackPane>();
    HashMap<StackPane, Timer> btnTimers= new HashMap<StackPane, Timer>();
    HashMap<StackPane, ListItem> listItems = new HashMap<StackPane, ListItem>();
    HashMap<StackPane, VBox> editBtns = new HashMap<StackPane, VBox>();
    ArrayList<StackPane> allBtns = new ArrayList<StackPane>();
    BorderPane bp;
    double width;
    double height;
    int newItems = 0;
    TextArea itemLbl = new TextArea();
    TextArea descArea = new TextArea();
    Timer t = new Timer();
    StackPane editing = null;
    StackPane apply;
    StackPane cancel;
    boolean hovered = false;
    HBox timeBox = new HBox();
    MediaPlayer sfx;
    boolean clicked;
    double mX;
    double mY;
    VBox listBox;
    Rectangle r;

    public ListCreator(Parent parent, double width, double height, Stage primaryStage, DoList list)
    {
        super(parent, width, height);
        bp = (BorderPane)parent;
        bp.resize(width, height);
        this.width = width;
        this.height = height;
        this.primaryStage = primaryStage;
        this.list = list;
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
        r = new Rectangle (400, 10);
        r.setVisible(true);
        Calendar cal = Calendar.getInstance();
        Pane topBarStuff = new Pane();
        Rectangle topbar = new Rectangle(width, 100);
        topbar.setVisible(true);
        TextField listName = new TextField();
        listName.setPrefWidth(width);
        listName.setPrefHeight(100);
        listName.setPromptText("Enter List Name Here");
        listName.setFont(new Font("Microsoft Yahei UI Light", 40));
        getStylesheets().add(getClass().getResource("Squidward.css").toExternalForm());
        listName.setBorder(new Border(new BorderStroke(topbar.getFill(), BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.EMPTY)));
        topBarStuff.getChildren().addAll(topbar, listName);
        Stop[] stopss = new Stop[] { new Stop(0, Main.colors[Main.theme][2]), new Stop(1, Main.colors[Main.theme][1])};
        LinearGradient lg11 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stopss);
        topbar.setFill(lg11);
        listName.setStyle("-fx-background-color: transparent;-fx-text-inner-color: white;");
        bp.setTop(topBarStuff);
        listBox = new VBox();
        VBox listBoxx = new VBox(listBox);
        listBox.setSpacing(1);
        makeButton(2, "addItem", 180, 40, Main.colors[Main.theme][3], "Add item...");
        makeButton(2, "doneList", 180, 40, Main.colors[Main.theme][3], "Done");
        HBox bleh = new HBox(buttons.get("addItem"), buttons.get("doneList"));
        bleh.setSpacing(10);
        listBoxx.setPadding(new Insets(5, 5, 0, 5));
        bleh.setAlignment(Pos.CENTER_LEFT);
        itemLbl.setVisible(true);
        descArea.setVisible(true);
        itemLbl.setOpacity(0);
        descArea.setOpacity(0);
        descArea.setPromptText("Enter description here...");
        descArea.setWrapText(true);
        itemLbl.setEditable(false);
        descArea.setEditable(false);
        itemLbl.setFont(new Font("Microsoft Yahei UI Light", 30));
        descArea.setFont(new Font("Microsoft Yahei UI Light", 25));
        makeButton(2, "apply", 60, 40, Main.colors[Main.theme][3], "Apply");
        makeButton(2, "cancel", 60, 40, Main.colors[Main.theme][3], "Cancel");
        apply = buttons.get("apply");
        cancel = buttons.get("cancel");
        apply.setDisable(true);
        cancel.setDisable(true);
        apply.setOpacity(0);
        cancel.setOpacity(0);
        HBox confirm = new HBox(cancel, apply);
        confirm.setSpacing(20);
        ObservableList<String> allHours = FXCollections.observableArrayList();
        ObservableList<String> allMinutes = FXCollections.observableArrayList();
        ObservableList<String> amPm = FXCollections.observableArrayList();
        amPm.add("AM");
        amPm.add("PM");
        for (int i = 0; i < 60; i++)
        {
            String s = Integer.toString(i);
            if (i < 13 && i != 0)
            {
                allHours.add(s + ":");
            }
            if (i < 10)
            {
                s = "0" + s;
            }
            allMinutes.add(s);
        }
        ComboBox hours = new ComboBox(allHours);
        ComboBox minutes = new ComboBox(allMinutes);
        ComboBox amPms = new ComboBox(amPm);
        CheckBox timeCheck = new CheckBox();
        timeCheck.setSelected(true);
        Label setTime = new Label ("Set Time");
        setTime.setFont(new Font("Microsoft Yahei UI Light", 20));
        HBox timeeBox = new HBox(hours, minutes, amPms);
        timeBox = new HBox (timeCheck, setTime, timeeBox);
        timeBox.setPadding(new Insets(20));
        timeBox.setAlignment(Pos.CENTER_LEFT);
        timeeBox.setSpacing(10);
        timeBox.setSpacing(10);
        VBox descBox = new VBox(itemLbl, descArea, timeBox, confirm);
        descBox.setPadding(new Insets(0));
        descBox.setAlignment(Pos.TOP_CENTER);
        itemLbl.setPrefWidth(350);
        itemLbl.setPrefRowCount(1);
        itemLbl.setMaxHeight(70);
        descArea.setPrefWidth(400);
        descArea.setPrefHeight(300);
        bp.setRight(descBox);
        timeBox.setOpacity(0);
        ((CheckBox)timeBox.getChildren().get(0)).setDisable(true);
        ((ComboBox)timeeBox.getChildren().get(0)).setDisable(true);
        ((ComboBox)timeeBox.getChildren().get(1)).setDisable(true);
        ((ComboBox)timeeBox.getChildren().get(2)).setDisable(true);
        if (list != null)
        {
            listName.setText(list.name);
            for (int i = 0; i < list.listItems.size(); i++)
            {
                makeButton(0,"newItem" + newItems, 400, 80, Main.colors[Main.theme][3], list.listItems.get(i).itemName);
                allBtns.add(buttons.get("newItem" + newItems));
                makeButton(1, "edit" + newItems, 20, 20, Color.TRANSPARENT, new Image(getClass().getResource("edititem.png").toString()));
                makeButton(1, "delete" + newItems, 20, 20, Color.TRANSPARENT, new Image(getClass().getResource("delete.png").toString()));
                VBox itemOpt = new VBox(buttons.get("edit" + newItems), buttons.get("delete" + newItems));
                ((Rectangle) ((StackPane) ((StackPane)itemOpt.getChildren().get(0)).getChildren().get(0)).getChildren().get(2)).setFill(Color.TRANSPARENT);
                ((Rectangle) ((StackPane) ((StackPane)itemOpt.getChildren().get(1)).getChildren().get(0)).getChildren().get(2)).setFill(Color.TRANSPARENT);
                StackPane s = new StackPane(buttons.get("newItem" + newItems), itemOpt);
                listBox.getChildren().add(s);
                itemOpt.setSpacing(5);
                itemOpt.setTranslateX(transX);
                itemOpt.setTranslateY(transY);
                itemOpt.setManaged(false);
                StackPane blehh = buttons.get("newItem" + newItems);
                editBtns.put(blehh, itemOpt);
                if(!listItems.containsKey(blehh))
                {
                    listItems.put(blehh, list.listItems.get(i));
                }
                ((StackPane)itemOpt.getChildren().get(0)).setOnMouseClicked(f ->
                {
                    if (editing != null)
                    {
                        editing.setTranslateX(0);
                        editBtns.get(editing).setTranslateX(transX);
                    }
                    editing = blehh;
                    editing.setTranslateX(20);
                    editBtns.get(editing).setTranslateX(transX+10);
                    itemLbl.setText(listItems.get(blehh).itemName);
                    descArea.setText(listItems.get(blehh).description);
                    itemLbl.setOpacity(1);
                    descArea.setOpacity(1);
                    itemLbl.setEditable(true);
                    descArea.setEditable(true);
                    ((CheckBox)timeBox.getChildren().get(0)).setDisable(false);
                    ((ComboBox)timeeBox.getChildren().get(0)).setDisable(false);
                    ((ComboBox)timeeBox.getChildren().get(1)).setDisable(false);
                    ((ComboBox)timeeBox.getChildren().get(2)).setDisable(false);
                    apply.setDisable(false);
                    cancel.setDisable(false);
                    apply.setOpacity(1);
                    cancel.setOpacity(1);
                    confirm.setTranslateX(120);
                    descArea.requestFocus();
                });
                ((StackPane)itemOpt.getChildren().get(1)).setOnMouseClicked(f ->
                {
                    if (editing != null)
                    {
                        ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).setId("rect");
                        ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).setFocusTraversable(true);
                        ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).requestFocus();
                        Media media = null;
                        try
                        {
                            media = new Media(getClass().getResource("wrong.mp3").toURI().toString());
                        }
                        catch (URISyntaxException uriSyntaxException)
                        {
                            uriSyntaxException.printStackTrace();
                        }
                        sfx = new MediaPlayer(media);
                        sfx.play();
                        return;
                    }
                    itemLbl.setOpacity(0);
                    descArea.setOpacity(0);
                    listBox.getChildren().remove(s);
                    list.listItems.remove(listItems.get(blehh));
                    listItems.remove(blehh);
                    timeBox.setOpacity(0);
                });
                if (listItems.get(blehh).done)
                {
                    ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(0)).setFill(Color.web("1aeb4b"));
                    Stop[] stops = new Stop[]{new Stop(0, Color.web("9cf7b0", 0.4)), new Stop(1, Color.web("20d649"))};
                    LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                    ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(2)).setFill(lg1);
                }
                else
                {
                    ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(0)).setFill(Main.colors[Main.theme][3]);
                    Stop[] stops = new Stop[]{new Stop(0, Main.colors[Main.theme][1]), new Stop(1, Main.colors[Main.theme][2])};
                    LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                    ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(2)).setFill(lg1);
                }
                if (listItems.get(blehh).due)
                {
                    ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(0)).setFill(Color.web("bf1515"));
                    Stop[] stops = new Stop[]{new Stop(0, Color.web("f79c9c", 0.4)), new Stop(1, Color.web("d62020"))};
                    LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                    ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(2)).setFill(lg1);
                }
                blehh.setOnMouseClicked(f ->
                {
                    if (editing == null)
                    {
                        if (!listItems.get(blehh).done)
                        {
                            try
                            {
                                Main.soundPlayer = new MediaPlayer(new Media(getClass().getResource("bell.mp3").toURI().toString()));
                            }
                            catch (URISyntaxException e) { }
                            Main.soundPlayer.play();
                            ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(0)).setFill(Color.web("1aeb4b"));
                            Stop[] stops = new Stop[]{new Stop(0, Color.web("9cf7b0", 0.4)), new Stop(1, Color.web("20d649"))};
                            LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                            ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(2)).setFill(lg1);
                            listItems.get(blehh).done = true;
                            listItems.get(blehh).due = false;
                        }
                        else
                        {
                            ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(0)).setFill(Main.colors[Main.theme][3]);
                            Stop[] stops = new Stop[]{new Stop(0, Main.colors[Main.theme][1]), new Stop(1, Main.colors[Main.theme][2])};
                            LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                            ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(2)).setFill(lg1);
                            listItems.get(blehh).done = false;
                        }
                    }
                    else
                    {
                        ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).setId("rect");
                        ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).setFocusTraversable(true);
                        ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).requestFocus();
                        Media media = null;
                        try
                        {
                            media = new Media(getClass().getResource("wrong.mp3").toURI().toString());
                        }
                        catch (URISyntaxException uriSyntaxException)
                        {
                            uriSyntaxException.printStackTrace();
                        }
                        sfx = new MediaPlayer(media);
                        sfx.play();
                    }
                });
                newItems ++;
            }
        }
        listBoxx.getChildren().add(bleh);
        ScrollPane sp = new ScrollPane(listBoxx);
        bp.requestFocus();
        bp.setLeft(sp);
        timeCheck.setOnAction(e ->
        {
            if (timeCheck.isSelected())
            {
                timeeBox.setDisable(false);
                timeeBox.setOpacity(1);
            }
            else
            {
                timeeBox.setDisable(true);
                timeeBox.setOpacity(0);
            }
        });
        listName.setOnKeyPressed(e ->
        {
            if (e.getCode() == KeyCode.ENTER)
            {
                bp.requestFocus();
            }
        });
        itemLbl.setOnKeyPressed(e ->
        {
            if (e.getCode() == KeyCode.ENTER)
            {
                descArea.requestFocus();
                itemLbl.setText(itemLbl.getText().substring(0, itemLbl.getText().length()-1));
            }
        });
        buttons.get("addItem").setOnMouseClicked(e ->
        {
            if (editing != null)
            {
                ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).setId("rect");
                ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).setFocusTraversable(true);
                ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).requestFocus();
                Media media = null;
                try
                {
                    media = new Media(getClass().getResource("wrong.mp3").toURI().toString());
                }
                catch (URISyntaxException uriSyntaxException)
                {
                    uriSyntaxException.printStackTrace();
                }
                sfx = new MediaPlayer(media);
                sfx.play();
                return;
            }
            makeButton(0,"newItem" + newItems, 400, 80, Main.colors[Main.theme][3], "New Item" + newItems);
            allBtns.add(buttons.get("newItem" + newItems));
            makeButton(1, "edit" + newItems, 20, 20, Color.TRANSPARENT, new Image(getClass().getResource("edititem.png").toString()));
            makeButton(1, "delete" + newItems, 20, 20, Color.TRANSPARENT, new Image(getClass().getResource("delete.png").toString()));
            VBox itemOpt = new VBox(buttons.get("edit" + newItems), buttons.get("delete" + newItems));
            ((Rectangle) ((StackPane) ((StackPane)itemOpt.getChildren().get(0)).getChildren().get(0)).getChildren().get(2)).setFill(Color.TRANSPARENT);
            ((Rectangle) ((StackPane) ((StackPane)itemOpt.getChildren().get(1)).getChildren().get(0)).getChildren().get(2)).setFill(Color.TRANSPARENT);
            StackPane s = new StackPane(buttons.get("newItem" + newItems), itemOpt);
            listBox.getChildren().add(s);
            itemOpt.setSpacing(5);
            itemOpt.setTranslateX(transX);
            itemOpt.setTranslateY(transY);
            itemOpt.setManaged(false);
            StackPane blehh = buttons.get("newItem" + newItems);
            editBtns.put(blehh, itemOpt);
            if(!listItems.containsKey(blehh))
            {
                listItems.put(blehh, new ListItem("New Item" + newItems, false));
            }
            if (list != null)
            {
                list.listItems.add(listItems.get(blehh));
            }
            ((StackPane)itemOpt.getChildren().get(0)).setOnMouseClicked(f ->
            {
                if (editing != null)
                {
                    editing.setTranslateX(0);
                    editBtns.get(editing).setTranslateX(transX);
                }
                editing = blehh;
                editing.setTranslateX(30);
                editBtns.get(editing).setTranslateX(transX+10);
                itemLbl.setText(listItems.get(blehh).itemName);
                descArea.setText(listItems.get(blehh).description);
                itemLbl.setOpacity(1);
                descArea.setOpacity(1);
                itemLbl.setEditable(true);
                descArea.setEditable(true);
                ((CheckBox)timeBox.getChildren().get(0)).setDisable(false);
                ((ComboBox)timeeBox.getChildren().get(0)).setDisable(false);
                ((ComboBox)timeeBox.getChildren().get(1)).setDisable(false);
                ((ComboBox)timeeBox.getChildren().get(2)).setDisable(false);
                apply.setDisable(false);
                cancel.setDisable(false);
                apply.setOpacity(1);
                cancel.setOpacity(1);
                confirm.setTranslateX(120);
                descArea.requestFocus();
            });
            ((StackPane)itemOpt.getChildren().get(1)).setOnMouseClicked(f ->
            {
                if (editing != null)
                {
                    ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).setId("rect");
                    ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).setFocusTraversable(true);
                    ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).requestFocus();
                    Media media = null;
                    try
                    {
                        media = new Media(getClass().getResource("wrong.mp3").toURI().toString());
                    }
                    catch (URISyntaxException uriSyntaxException)
                    {
                        uriSyntaxException.printStackTrace();
                    }
                    sfx = new MediaPlayer(media);
                    sfx.play();
                    return;
                }
                itemLbl.setOpacity(0);
                descArea.setOpacity(0);
                listBox.getChildren().remove(s);
                if (list != null)
                {
                    list.listItems.remove(listItems.get(blehh));
                }
                allBtns.remove(blehh);
                listItems.remove(blehh);
                timeBox.setOpacity(0);
            });
            blehh.setOnMouseClicked(f ->
            {
                if (editing == null)
                {
                    if (!listItems.get(blehh).done)
                    {
                        try
                        {
                            Main.soundPlayer = new MediaPlayer(new Media(getClass().getResource("bell.mp3").toURI().toString()));
                        }
                        catch (URISyntaxException g) { }
                        Main.soundPlayer.play();
                        ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(0)).setFill(Color.web("1aeb4b"));
                        Stop[] stops = new Stop[]{new Stop(0, Color.web("9cf7b0", 0.4)), new Stop(1, Color.web("20d649"))};
                        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                        ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(2)).setFill(lg1);
                        listItems.get(blehh).done = true;
                        listItems.get(blehh).due = false;
                    }
                    else
                    {
                        ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(0)).setFill(Main.colors[Main.theme][3]);
                        Stop[] stops = new Stop[]{new Stop(0, Main.colors[Main.theme][1]), new Stop(1, Main.colors[Main.theme][2])};
                        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
                        ((Rectangle) ((StackPane) (blehh.getChildren().get(0))).getChildren().get(2)).setFill(lg1);
                        listItems.get(blehh).done = false;
                    }
                }
                else
                {
                    ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).setId("rect");
                    ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).setFocusTraversable(true);
                    ((Rectangle) ((StackPane) apply.getChildren().get(0)).getChildren().get(0)).requestFocus();
                    Media media = null;
                    try
                    {
                        media = new Media(getClass().getResource("wrong.mp3").toURI().toString());
                    }
                    catch (URISyntaxException uriSyntaxException)
                    {
                        uriSyntaxException.printStackTrace();
                    }
                    sfx = new MediaPlayer(media);
                    sfx.play();
                }
            });
            newItems++;
        });
        buttons.get("doneList").setOnMouseClicked(e ->
        {
            if (list != null)
            {
                list.name = listName.getText();
                primaryStage.setScene(new ListViewer(new BorderPane(), 900, 650, primaryStage, null));
            }
            else
            {
                if (allBtns.size() == 0)
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("0 items");
                    String s = "Please add atleast 1 item";
                    alert.setContentText(s);
                    alert.show();
                    return;
                }
                ArrayList<ListItem> items = new ArrayList<ListItem>();
                for (int i = 0; i < allBtns.size(); i++)
                {
                    items.add(listItems.get(allBtns.get(i)));
                }
                primaryStage.setScene(new ListViewer(new BorderPane(), 900, 650, primaryStage, new DoList(listName.getText(), items)));
            }
        });
        cancel.setOnMouseClicked(e ->
        {
            if (cancel.getOpacity() == 0)
            {
                return;
            }
            editing.setTranslateX(0);
            editBtns.get(editing).setTranslateX(transX);
            itemLbl.setEditable(false);
            descArea.setEditable(false);
            ((CheckBox)timeBox.getChildren().get(0)).setDisable(true);
            ((ComboBox)timeeBox.getChildren().get(0)).setDisable(true);
            ((ComboBox)timeeBox.getChildren().get(1)).setDisable(true);
            ((ComboBox)timeeBox.getChildren().get(2)).setDisable(true);
            apply.setOpacity(0);
            cancel.setOpacity(0);
            String s = listItems.get(editing).itemName;
            if (s.length() >= 25)
            {
                s = s.substring(0, 22) + "...";
            }
            itemLbl.setText(s);
            if (listItems.get(editing).description != null)
            {
                descArea.setText(listItems.get(editing).description);
            }
            else
            {
                descArea.clear();
                bp.requestFocus();
            }
            ((CheckBox)timeBox.getChildren().get(0)).setSelected(listItems.get(editing).timeSet);
            if (((CheckBox)timeBox.getChildren().get(0)).isSelected())
            {
                ((HBox)timeBox.getChildren().get(2)).setDisable(false);
                ((HBox)timeBox.getChildren().get(2)).setOpacity(1);
            }
            else
            {
                ((HBox)timeBox.getChildren().get(2)).setDisable(true);
                ((HBox)timeBox.getChildren().get(2)).setOpacity(0);
            }
            ((ComboBox)((HBox)timeBox.getChildren().get(2)).getChildren().get(0)).setValue(listItems.get(editing).hour % 12 + ":");
            if (listItems.get(editing).hour % 12 == 0)
            {
                ((ComboBox)((HBox)timeBox.getChildren().get(2)).getChildren().get(0)).setValue(12 + ":");
            }
            ((ComboBox)((HBox)timeBox.getChildren().get(2)).getChildren().get(1)).setValue(listItems.get(editing).minute);
            if (listItems.get(editing).minute < 10)
            {
                ((ComboBox)((HBox)timeBox.getChildren().get(2)).getChildren().get(1)).setValue("0" + listItems.get(editing).minute);
            }
            if (listItems.get(editing).hour >= 12)
            {
                ((ComboBox)((HBox)timeBox.getChildren().get(2)).getChildren().get(2)).setValue("PM");
            }
            else
            {
                ((ComboBox)((HBox)timeBox.getChildren().get(2)).getChildren().get(2)).setValue("AM");
            }
            editing = null;
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
                            if (itemLbl.getOpacity() > 0)
                            {
                                itemLbl.setOpacity(Math.max(0, itemLbl.getOpacity() - 0.01));
                                descArea.setOpacity(Math.max(0, descArea.getOpacity() - 0.01));
                                timeBox.setOpacity(Math.max(0, timeBox.getOpacity() - 0.01));
                            }
                        });
                    }
                }), 0, 10);
            }

            catch(Exception g){};
        });
        apply.setOnMouseClicked(e ->
        {
            if (apply.getOpacity() == 0)
            {
                return;
            }
            editing.setTranslateX(0);
            editBtns.get(editing).setTranslateX(transX);
            itemLbl.setEditable(false);
            descArea.setEditable(false);
            ((CheckBox)timeBox.getChildren().get(0)).setDisable(true);
            ((ComboBox)timeeBox.getChildren().get(0)).setDisable(true);
            ((ComboBox)timeeBox.getChildren().get(1)).setDisable(true);
            ((ComboBox)timeeBox.getChildren().get(2)).setDisable(true);
            apply.setOpacity(0);
            cancel.setOpacity(0);
            if (list == null)
            {
                listItems.put(editing, new ListItem(itemLbl.getText(), listItems.get(editing).done));
            }
            else
            {
                listItems.get(editing).itemName = itemLbl.getText();
            }
            listItems.get(editing).setDescription(descArea.getText());
            String s = itemLbl.getText();
            if (s.length() >= 40)
            {
                s = s.substring(0, 37) + "...";
            }
            ((Label)((StackPane)editing.getChildren().get(0)).getChildren().get(1)).setText(s);
            listItems.get(editing).timeSet = ((CheckBox)timeBox.getChildren().get(0)).isSelected();
            if (listItems.get(editing).timeSet)
            {
                listItems.get(editing).hour = Integer.parseInt((((ComboBox)timeeBox.getChildren().get(0)).getValue().toString()).substring(0, (((ComboBox)timeeBox.getChildren().get(0)).getValue().toString()).length()-1));
                listItems.get(editing).minute = Integer.parseInt((((ComboBox)timeeBox.getChildren().get(1)).getValue().toString()));
                if (((ComboBox)timeeBox.getChildren().get(2)).getValue().toString().equals("PM"))
                {
                    listItems.get(editing).hour += 12;
                }
                if (((ComboBox)timeeBox.getChildren().get(2)).getValue().toString().equals("AM") && listItems.get(editing).hour == 12)
                {
                    listItems.get(editing).hour = 0;
                }
            }
            editing = null;
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
                            if (itemLbl.getOpacity() > 0)
                            {
                                itemLbl.setOpacity(Math.max(0, itemLbl.getOpacity() - 0.01));
                                descArea.setOpacity(Math.max(0, descArea.getOpacity() - 0.01));
                                timeBox.setOpacity(Math.max(0, timeBox.getOpacity() - 0.01));
                            }
                        });
                    }
                }), 0, 10);
            }
            catch(Exception g){};
        });
    }
    public void makeButton(int list, String name, int width, int height, Color color, String string)
    {
        StackPane hitbutton = new StackPane();
        StackPane button = new StackPane();
        Label label = new Label(string);
        label.setVisible(true);
        label.setFont(new Font("Microsoft Yahei UI Light", height/2.5));
        if (list == 0)
        {
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
        Stop[] stops = new Stop[] { new Stop(0, Main.colors[Main.theme][1]), new Stop(1, Main.colors[Main.theme][2])};
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
        buttonHover(button, fd, hb, list);
    }
    public void makeButton(int list, String name, int width, int height, Color color, Image image)
    {
        setFill(Main.colors[Main.theme][0]);
        StackPane hitbutton = new StackPane();
        StackPane button = new StackPane();
        ImageView img1 = new ImageView(image);
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
        hitbutton.getChildren().addAll(button, hb);
        buttons.put(name, hitbutton);
        btnTimers.put(button, new Timer());
        fd.setOpacity(0.0);
        buttonHover(button, fd, hb, list);
    }
    public void buttonHover(StackPane button, Rectangle fd, Rectangle hb, int list)
    {
        hb.setOnMouseEntered(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent me)
            {
                try
                {
                    if (hb.getOpacity() > 0 && (button.getParent() != cancel && button.getParent() != apply || button.getParent().getOpacity() > 0))
                    {
                        setCursor(Cursor.HAND);
                        btnTimers.get(button).cancel();
                        btnTimers.put(button, new Timer());
                        btnTimers.get(button).scheduleAtFixedRate((new TimerTask() {

                            @Override
                            public void run() {
                                Platform.runLater(() ->
                                {
                                    if (fd.getOpacity() < 0.4)
                                    {
                                        fd.setOpacity(Math.min(0.5, fd.getOpacity() + 0.01));
                                    }
                                    if (button.getTranslateY() > -9.5)
                                    {
                                        button.setTranslateY((-10 + button.getTranslateY()) / 2);
                                    }
                                    else
                                    {
                                        button.setTranslateY(-10);
                                    }
                                });
                            }
                        }), 0, 10);
                    }
                }
                        catch(Exception e){};
                if (list == 0 && editing == null) {
                    String s = listItems.get(button.getParent()).itemName;
                    if (s.length() >= 25) {
                        s = s.substring(0, 22) + "...";
                    }
                    itemLbl.setText(s);
                    if (listItems.get(button.getParent()).description != null) {
                        descArea.setText(listItems.get(button.getParent()).description);
                    } else {
                        descArea.clear();
                        bp.requestFocus();
                    }
                    ((CheckBox) timeBox.getChildren().get(0)).setSelected(listItems.get(button.getParent()).timeSet);
                    if (((CheckBox) timeBox.getChildren().get(0)).isSelected()) {
                        ((HBox) timeBox.getChildren().get(2)).setDisable(false);
                        ((HBox) timeBox.getChildren().get(2)).setOpacity(1);
                    } else {
                        ((HBox) timeBox.getChildren().get(2)).setDisable(true);
                        ((HBox) timeBox.getChildren().get(2)).setOpacity(0);
                    }
                    ((ComboBox) ((HBox) timeBox.getChildren().get(2)).getChildren().get(0)).setValue(listItems.get(button.getParent()).hour % 12 + ":");
                    if (listItems.get(button.getParent()).hour % 12 == 0) {
                        ((ComboBox) ((HBox) timeBox.getChildren().get(2)).getChildren().get(0)).setValue(12 + ":");
                    }
                    ((ComboBox) ((HBox) timeBox.getChildren().get(2)).getChildren().get(1)).setValue(listItems.get(button.getParent()).minute);
                    if (listItems.get(button.getParent()).minute < 10) {
                        ((ComboBox) ((HBox) timeBox.getChildren().get(2)).getChildren().get(1)).setValue("0" + listItems.get(button.getParent()).minute);
                    }
                    else
                    {
                        ((ComboBox) ((HBox) timeBox.getChildren().get(2)).getChildren().get(1)).setValue("" + listItems.get(button.getParent()).minute);
                    }
                    System.out.println(listItems.get(button.getParent()).hour);
                    if (listItems.get(button.getParent()).hour >= 12) {
                        ((ComboBox) ((HBox) timeBox.getChildren().get(2)).getChildren().get(2)).setValue("PM");
                    } else {
                        ((ComboBox) ((HBox) timeBox.getChildren().get(2)).getChildren().get(2)).setValue("AM");
                    }
                    try {
                        t.cancel();
                        t = new Timer();
                        t.scheduleAtFixedRate((new TimerTask() {

                            @Override
                            public void run() {
                                Platform.runLater(() ->
                                {
                                    if (itemLbl.getOpacity() < 1) {
                                        itemLbl.setOpacity(Math.min(1, itemLbl.getOpacity() + 0.01));
                                        descArea.setOpacity(Math.min(1, descArea.getOpacity() + 0.01));
                                        timeBox.setOpacity(Math.min(1, timeBox.getOpacity() + 0.01));
                                    }
                                });
                            }
                        }), 0, 10);
                    } catch (Exception g) { };
                }
                if (list == 1)
                {
                    hovered = true;
                    itemLbl.setOpacity(1);
                    descArea.setOpacity(1);
                    timeBox.setOpacity(1);
                    t.cancel();
                }
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
                catch(Exception e){}
                if (list == 0 && editing == null && !hovered)
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
                                    if (itemLbl.getOpacity() > 0)
                                    {
                                        itemLbl.setOpacity(Math.max(0, itemLbl.getOpacity() - 0.01));
                                        descArea.setOpacity(Math.max(0, descArea.getOpacity() - 0.01));
                                        timeBox.setOpacity(Math.max(0, timeBox.getOpacity() - 0.01));
                                    }
                                });
                            }
                        }), 0, 10);
                    }
                    catch(Exception g){};
                }
                if (list == 1)
                {
                    hovered = false;
                }
            }
        });

    }
}
