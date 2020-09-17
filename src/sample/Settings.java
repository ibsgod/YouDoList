package sample;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Settings extends Scene
{
    BorderPane bp;
    double width;
    double height;
    Stage primaryStage;
    HashMap<String, StackPane> buttons = new HashMap<String, StackPane>();
    HashMap<StackPane, Timer> btnTimers = new HashMap<StackPane, Timer>();
    public Settings(Parent parent, double width, double height, Stage primaryStage)
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
        Pane topBarStuff = new Pane();
        Rectangle topbar = new Rectangle(width, 100);
        topbar.setVisible(true);
        Stop[] stops = new Stop[] { new Stop(0, Main.colors[Main.theme][2]), new Stop(1, Main.colors[Main.theme][1])};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        topbar.setFill(lg1);
        Label mainLbl = new Label("Settings");
        mainLbl.setTranslateX(215);
        mainLbl.setTranslateY(18);
        mainLbl.setFont(new Font("Microsoft Yahei UI Light", 40));
        mainLbl.setTextFill(Color.WHITE);
        mainLbl.setAlignment(Pos.CENTER_LEFT);
        topBarStuff.getChildren().addAll(topbar, mainLbl);
        bp.setTop(topBarStuff);
        Label themeLbl = new Label("Theme: ");
        themeLbl.setFont(new Font("Microsoft Yahei UI Light", 20));
        ComboBox<String> themeBox = new ComboBox<String>();
        themeBox.setCellFactory(
                new Callback<ListView<String>, ListCell<String>>() {
                    @Override
                    public ListCell<String> call(ListView<String> param) {
                        final ListCell<String> cell = new ListCell<String>() {
                            @Override
                            public void updateItem(String item,
                                                   boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    setText(item);
                                    setFont(new Font("Microsoft Yahei UI Light", 15));
                                } else {
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                });
        themeBox.setButtonCell(new ListCell(){
        @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item==null){
                    setFont(new Font("Microsoft Yahei UI Light", 20));
                } else {
                    setFont(new Font("Microsoft Yahei UI Light", 20));
                    setText(item.toString());
                }
            }
        });
        themeBox.getItems().addAll("Blue", "Green", "Red");
        themeBox.setValue(themeBox.getItems().get(Main.theme));
        HBox hbox1 = new HBox (themeLbl, themeBox);
        hbox1.setSpacing(10);
        Label remindLbl = new Label("Repeat notification after: ");
        remindLbl.setFont(new Font("Microsoft Yahei UI Light", 20));
        ComboBox<String> remindBox = new ComboBox<String>();
        remindBox.getEditor().setFont(new Font("Microsoft Yahei UI Light", 25));
        remindBox.getItems().addAll("1 minute", "2 minutes", "5 minutes", "10 minutes", "20 minutes", "30 minutes");
        remindBox.setValue(Integer.toString(Main.remindTime) + " minute" + (Main.remindTime > 1 ? "s" : ""));
        remindBox.setCellFactory(
                new Callback<ListView<String>, ListCell<String>>() {
                    @Override
                    public ListCell<String> call(ListView<String> param) {
                        final ListCell<String> cell = new ListCell<String>() {
                            @Override
                            public void updateItem(String item,
                                                   boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    setText(item);
                                    setFont(new Font("Microsoft Yahei UI Light", 15));
                                } else {
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                });
        remindBox.setButtonCell(new ListCell(){
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item==null){
                    setFont(new Font("Microsoft Yahei UI Light", 20));
                } else {
                    setFont(new Font("Microsoft Yahei UI Light", 20));
                    setText(item.toString());
                }
            }
        });
        HBox hbox2 = new HBox (remindLbl, remindBox);
        hbox2.setSpacing(10);
        Label soundLbl = new Label("Music volume: ");
        soundLbl.setFont(new Font("Microsoft Yahei UI Light", 20));
        Slider musSlider = new Slider(0, 100, 50);
        musSlider.setShowTickMarks(true);
        musSlider.setShowTickLabels(true);
        musSlider.setMajorTickUnit(10);
        musSlider.setMinorTickCount(1);
        musSlider.setPrefWidth(200);
        HBox hbox3 = new HBox(soundLbl, musSlider);
        hbox3.setSpacing(10);
        hbox3.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(hbox3, hbox1, hbox2);
        vbox.setSpacing(20);
        hbox1.setAlignment(Pos.CENTER);
        hbox2.setAlignment(Pos.CENTER);
        vbox.setAlignment(Pos.CENTER);
        bp.setCenter(vbox);
        getStylesheets().add(getClass().getResource("Squidward.css").toExternalForm());
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
        makeButton("back", 200, 60, Main.colors[Main.theme][3], "Main menu");
        vbox.getChildren().add(buttons.get("back"));
        remindBox.setOnAction(e ->
        {
            Main.remindTime = Integer.parseInt(remindBox.getValue().split(" ")[0]);
        });
        themeBox.setOnAction(e ->
        {
            Main.theme = themeBox.getItems().indexOf(themeBox.getValue());
            getStylesheets().clear();
            try {
                switch (Main.theme) {
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
            }
            catch(Exception eg){}
            Stop[] stopss = new Stop[] { new Stop(0, Main.colors[Main.theme][2]), new Stop(1, Main.colors[Main.theme][1])};
            LinearGradient lg11 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stopss);
            topbar.setFill(lg11);
            ((Rectangle)((StackPane)buttons.get("back").getChildren().get(0)).getChildren().get(0)).setFill(Main.colors[Main.theme][3]);
            Stop[] stopsss = new Stop[]{new Stop(0, Main.colors[Main.theme][1]), new Stop(1, Main.colors[Main.theme][2])};
            LinearGradient lg111 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stopsss);
            ((Rectangle)((StackPane)buttons.get("back").getChildren().get(0)).getChildren().get(2)).setFill(lg111);
        });
        musSlider.valueProperty().addListener(e ->
        {
            Main.mediaPlayer.setVolume(musSlider.getValue()/100.0);
        });
        buttons.get("back").setOnMouseClicked(e ->
        {
            Main.updateJson();
            primaryStage.setScene(new Menu(new BorderPane(), 500, 500, primaryStage));
        });
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
        buttonHover(button, fd, hb);
    }
    public void buttonHover(StackPane button, Rectangle fd, Rectangle hb)
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
}
