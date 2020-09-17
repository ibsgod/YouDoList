package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class DateEditor extends Scene
{
    DoList list;
    BorderPane bp;
    DatePicker specdp = new DatePicker();
    DatePicker fromdp = new DatePicker();
    DatePicker todp = new DatePicker();
    HashMap<String, StackPane> buttons = new HashMap<String, StackPane>();
    HashMap<StackPane, Timer> btnTimers = new HashMap<StackPane, Timer>();
    MediaPlayer sfx;
    Stage stage;
    ListViewer lv = null;
    boolean hovered = true;
    public DateEditor(Stage stage, Parent parent, double width, double height, DoList list)
    {
        super(parent, width, height);
        bp = (BorderPane) parent;
        bp.resize(width, height);
        this.list = list;
        this.stage = stage;
        start();
        stage.initModality(Modality.APPLICATION_MODAL);
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
        getStylesheets().add(getClass().getResource("Squidward.css").toExternalForm());
        ObservableList<String> dateOptionss = FXCollections.observableArrayList();
        dateOptionss.addAll("Set no date", "Set specific date", "Set ongoing");
        HBox dateeBox = new HBox();
        ComboBox dateOptions = new ComboBox(dateOptionss);
        switch(list.dateSet)
        {
            case "noDate":
                dateOptions.setValue(dateOptionss.get(0));
                break;
            case "specDate":
                dateOptions.setValue(dateOptionss.get(1));
                break;
            case "ongoingDate":
                dateOptions.setValue(dateOptionss.get(2));
                break;
        }
        dateeBox.getChildren().clear();
        if (dateOptions.getValue().equals("Set specific date"))
        {
            dateeBox.getChildren().add(specdp);
            specdp.setValue(list.date);
        }
        else if (dateOptions.getValue().equals("Set ongoing"))
        {
            VBox bleh = new VBox(new Label("From:"), new Label("To:"));
            bleh.setSpacing(7);
            bleh.setTranslateY(4);
            HBox blehh = new HBox(bleh, new VBox(fromdp, todp));
            blehh.setSpacing(5);
            fromdp.setValue(list.date);
            todp.setValue(list.toDate);
            dateeBox.getChildren().add(blehh);
        }
        dateOptions.setStyle("-fx-font: 15px \"Microsoft Yahei UI Light\";");
        HBox dateBox = new HBox(dateOptions, dateeBox);
        specdp.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
            }
        });
        fromdp.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0);
            }
        });
        fromdp.setOnAction(e ->
        {
            if (todp.getValue() != null && fromdp.getValue().compareTo(todp.getValue()) >= 0) {
                todp.setValue(null);
            }
        });
        todp.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || fromdp.getValue() == null || date.compareTo(fromdp.getValue()) <= 0);
            }
        });
        dateOptions.setOnAction(e ->
        {
            dateeBox.getChildren().clear();
            if (dateOptions.getValue().equals("Set specific date"))
            {
                dateeBox.getChildren().add(specdp);
            }
            else if (dateOptions.getValue().equals("Set ongoing"))
            {
                VBox bleh = new VBox(new Label("From:"), new Label("To:"));
                bleh.setSpacing(7);
                bleh.setTranslateY(4);
                HBox blehh = new HBox(bleh, new VBox(fromdp, todp));
                blehh.setSpacing(5);
                dateeBox.getChildren().add(blehh);
            }
        });

        HBox centerBox = new HBox(dateBox);
        centerBox.setAlignment(Pos.TOP_CENTER);
        dateBox.setPadding(new Insets(20));
        dateBox.setAlignment(Pos.CENTER_LEFT);
        dateBox.setSpacing(10);
        bp.setTop(centerBox);
        makeButton(2, "dateOk", 150, 40, Main.colors[Main.theme][3], "OK");
        HBox bot = new HBox (buttons.get("dateOk"));
        bot.setAlignment(Pos.TOP_CENTER);
        bp.setBottom(bot);

        buttons.get("dateOk").setOnMouseClicked(e ->
        {
            if (specdp.getValue() == null && dateOptions.getValue().equals("Set specific date") || (fromdp.getValue() == null || todp.getValue() == null) && dateOptions.getValue().equals("Set ongoing"))
            {
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
            else
            {
                switch ((String) dateOptions.getValue())
                {
                    case "Set no date":
                        list.dateSet = "noDate";
                        break;
                    case "Set specific date":
                        list.dateSet = "specDate";
                        list.date = specdp.getValue();
                        break;
                    case "Set ongoing":
                        list.dateSet = "ongoingDate";
                        list.date = fromdp.getValue();
                        list.toDate = todp.getValue();
                        break;
                }
                if (list.name.equals("") && !list.dateSet.equals("noDate"))
                {
                    list.name = list.date.toString();
                }
                else if (list.name.equals(""))
                {
                    list.name = "NewList";
                }
                closeStage();
                stage.close();
            }
        });
        stage.setOnCloseRequest(e ->
        {
            if (list.name.equals("") && !list.dateSet.equals("noDate"))
            {
                list.name = list.date.toString();
            }
            else if (list.name.equals(""))
            {
                list.name = "NewList";
            }
            closeStage();
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
    public void buttonHover(StackPane button, Rectangle fd, Rectangle hb, int list)
    {
        hb.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                try {
                    if (hb.getOpacity() > 0 && button.getParent().getOpacity() > 0) {
                        setCursor(Cursor.HAND);
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
                }
                catch (Exception e) {}
            }
        });
        hb.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                setCursor(Cursor.DEFAULT);
                try {
                    btnTimers.get(button).cancel();
                    btnTimers.put(button, new Timer());
                    btnTimers.get(button).scheduleAtFixedRate((new TimerTask() {

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
                } catch (Exception e) {}

            }
        });

    }
    public void closeStage()
    {
        lv.showLists();
        ((Label)((StackPane)lv.buttonList.get(list).getChildren().get(0)).getChildren().get(1)).setText(list.name);
        if (list.dateSet.equals("specDate"))
        {
            lv.dateLbl.setText("Set for: " + list.date.toString());
        }
        else if (list.dateSet.equals("ongoingDate"))
        {
            lv.dateLbl.setText("Set from: " + list.date.toString() + " to " + list.toDate.toString());
        }
        else
        {
            lv.dateLbl.setText("No date set");
        }
        if (lv.selected != null)
        {
            lv.selected.setTranslateY(0);
            lv.selected = null;
        }
        try
        {
            lv.t.cancel();
            lv.t = new Timer();
            lv.t.scheduleAtFixedRate((new TimerTask() {

                @Override
                public void run()
                {
                    Platform.runLater(() ->
                    {
                        if (lv.right.getOpacity() > 0)
                        {
                            lv.right.setOpacity(Math.max(0, lv.right.getOpacity() - 0.01));
                        }
                    });
                }
            }), 0, 10);
        }
        catch(Exception g){};
    }
}

