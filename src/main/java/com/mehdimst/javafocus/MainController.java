package com.mehdimst.javafocus;

import com.jfoenix.controls.JFXButton;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController implements Initializable {
    private double xOffset = 0;
    private double yOffset = 0;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter fullDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), ae -> incrementTime());

    @FXML
    private JFXButton startTimerButton;
    @FXML
    private JFXButton pauseTimerButton;
    @FXML
    private TextField timerTxt;
    @FXML
    private Label errorMessage;

    @FXML
    private ListView<LogActivity> logsListView;
    @FXML
    private Label startTimeLbl;
    @FXML
    private Label endTimeLbl;
    @FXML
    private TextField descriptionTxt;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // First init app
        initTimeline();

        // For open log activity first next open main page with RUNNING status
        if (timerTxt != null && Main.timeline.getStatus().equals(Animation.Status.RUNNING)) {
            startTimerButton.setDisable(true);
            timerTxt.setDisable(true);
            timerTxt.setText(Main.time.format(dtf));
            resetKeyFrame(true);
        }

        // For open log activity first next open main page with STOPPED status
        if (timerTxt != null && Main.timeline.getStatus().equals(Animation.Status.STOPPED)) {
            Main.time = LocalTime.parse(Main.defaultTimeString);
            timerTxt.setText(Main.time.format(dtf));
            resetKeyFrame(false);
        }

        // For open log activity first next open main page with PAUSED status
        if (timerTxt != null && Main.timeline.getStatus().equals(Animation.Status.PAUSED)) {
            pauseTimerButton.setText("Continue");
            startTimerButton.setDisable(true);
            timerTxt.setDisable(true);
            timerTxt.setText(Main.time.format(dtf));
            resetKeyFrame(true);
            Main.timeline.pause();
        }

        // For log activity history
        if (logsListView != null) {
            logsListView.getItems().addAll(LogActivityService.getLogs());

            logsListView.setCellFactory(param -> new ListCell<LogActivity>() {
                @Override
                protected void updateItem(LogActivity item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getId() + " - "
                                + (item.getDescription() != null ? item.getDescription() + " - " : "")
                                + item.getStartTime().format(fullDateTimeFormat) + " - "
                                + (item.getEndTime() != null ? item.getEndTime().format(fullDateTimeFormat) : ""));
                    }
                }
            });

            logsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LogActivity>() {
                @Override
                public void changed(ObservableValue<? extends LogActivity> observableValue, LogActivity logActivity, LogActivity t1) {
                    LogActivity log = logsListView.getSelectionModel().getSelectedItem();
                    if (log != null) {
                        startTimeLbl.setText(log.getStartTime().format(fullDateTimeFormat));
                        LocalDateTime endDateTime = log.getEndTime();
                        if (endDateTime != null)
                            endTimeLbl.setText(endDateTime.format(fullDateTimeFormat));
                        descriptionTxt.setText(log.getDescription());
                    }
                }
            });

            descriptionTxt.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    LogActivity log = logsListView.getSelectionModel().getSelectedItem();
                    if (log != null) {
                        log.setDescription(descriptionTxt.getText());
                        logsListView.getItems().clear();
                        logsListView.getItems().addAll(LogActivityService.getLogs());
                    } else {
                        String q = descriptionTxt.getText();
                        logsListView.getItems().clear();
                        if (q == null || q.isEmpty()) {
                            logsListView.getItems().addAll(LogActivityService.getLogs());
                        } else {
                            logsListView.getItems().addAll(LogActivityService.filterLogs(descriptionTxt.getText()));
                        }

                    }
                }
            });
        }
    }

    private void initTimeline() {
        if (Main.timeline == null) {
            Main.timeline = new Timeline(keyFrame);
            Main.timeline.setCycleCount(Animation.INDEFINITE);
            Main.time = LocalTime.parse(Main.defaultTimeString);
            if (timerTxt != null)
                timerTxt.setText(Main.time.format(dtf));
        }
    }

    private void resetKeyFrame(boolean playFromStart) {
        Main.timeline.stop();
        Main.timeline.getKeyFrames().clear();
        Main.timeline.setCycleCount(Animation.INDEFINITE);
        Main.timeline.getKeyFrames().add(keyFrame);
        if (playFromStart)
            Main.timeline.playFromStart();
    }

    private void incrementTime() {
        Main.time = Main.time.plusSeconds(-1);
        int diffTime = Main.time.compareTo(LocalTime.parse("00:00:00"));
        if (diffTime <= 0) {
            endTimer(null);
            Main.showStaticStage();
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            Stage s = Main.stage;
            s.setWidth(bounds.getWidth());
            s.setHeight(bounds.getHeight());
            s.setX(0);
            s.setY(0);
        }
        if (timerTxt != null) {
            timerTxt.setText(Main.time.format(dtf));
        }
    }

    @FXML
    private void startTimer(ActionEvent event) {
        Main.defaultTimeString = timerTxt.getText();
        try {
            Main.time = LocalTime.parse(Main.defaultTimeString);
        } catch (Exception ex) {
            Main.defaultTimeString = "01:00:00";
            Main.time = LocalTime.parse(Main.defaultTimeString);
            timerTxt.setText(Main.defaultTimeString);
            errorMessage.setText(ex.getLocalizedMessage());
            return;
        }
        errorMessage.setText("");
        Main.timeline.play();
        startTimerButton.setDisable(true);
        timerTxt.setDisable(true);
        LogActivityService.start();
    }

    @FXML
    private void pauseTimer(ActionEvent event) {
        if (Main.timeline.getStatus().equals(Animation.Status.PAUSED)) {
            Main.timeline.play();
            startTimerButton.setDisable(true);
            timerTxt.setDisable(true);
            pauseTimerButton.setText("Pause");
            LogActivityService.start();
        } else if (Main.timeline.getStatus().equals(Animation.Status.RUNNING)) {
            Main.timeline.pause();
            startTimerButton.setDisable(true);
            timerTxt.setDisable(true);
            pauseTimerButton.setText("Continue");
            LogActivityService.stop();
        }
    }

    @FXML
    private void endTimer(ActionEvent event) {
        if (startTimerButton.isDisable()) {
            Main.timeline.stop();
            startTimerButton.setDisable(false);
            timerTxt.setDisable(false);
            Main.time = LocalTime.parse(Main.defaultTimeString);
            timerTxt.setText(Main.time.format(dtf));
            LogActivityService.stop();
        }
    }

    @FXML
    private void showActivity(ActionEvent event) throws IOException {
        Main.changeScene("log-activity.fxml");
    }

    @FXML
    private void showMain(ActionEvent event) throws IOException {
        Main.changeScene("main-view.fxml");
    }

    public boolean isMaximized(Event event) {
        Stage s = ((Stage) (((Node) (event.getSource())).getScene().getWindow()));
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        if (s.getWidth() == bounds.getWidth() && s.getHeight() == bounds.getHeight()) {
            return true;
        }
        return false;
    }

    @FXML
    public void MaxWindow(Event event) {
        Stage s = ((Stage) (((Node) (event.getSource())).getScene().getWindow()));
        if (isMaximized(event)) {
            s.setWidth(610);
            s.setHeight(361);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            s.setX(bounds.getWidth() / 2 - (305));
            s.setY(bounds.getHeight() / 2 - (180));
        } else {
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            s.setWidth(bounds.getWidth());
            s.setHeight(bounds.getHeight());
            s.setX(0);
            s.setY(0);
        }
    }

    @FXML
    public void MinWindow(Event event) {
        Stage s = ((Stage) (((Node) (event.getSource())).getScene().getWindow()));
        s.setIconified(true);
    }

    @FXML
    public void RootMousePressed(Event event) {
        MouseEvent e = (MouseEvent) event;
        xOffset = e.getSceneX();
        yOffset = e.getSceneY();
    }

    @FXML
    public void RootMouseDragged(Event event) {
        if (isMaximized(event)) {
            return;
        }
        MouseEvent e = (MouseEvent) event;
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setX(e.getScreenX() - xOffset);
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).setY(e.getScreenY() - yOffset);
    }

    @FXML
    public void OnMouseDoubleClick(Event event) {
        MouseEvent mouseEvent = (MouseEvent) event;
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                MaxWindow(event);
            }
        }
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        ((Stage) (((Node) (event.getSource())).getScene().getWindow())).hide();
    }
}