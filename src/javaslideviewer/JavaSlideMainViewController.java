/*
 * © 2017 TAKAHASHI,Toru
 */
package javaslideviewer;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * スライドビューアのフレームコントローラー。
 * <p>
 * ユーザー操作に基づき、スライドの切り替え制御を行う。
 */
public class JavaSlideMainViewController implements Initializable {
    
    @FXML
    private Pane contentPane;
    @FXML
    private ToggleButton fullScreenButton;
    @FXML
    private Parent rootPane;
    
    private final List<KeyCode> previousKeys = Arrays.asList(KeyCode.LEFT, KeyCode.UP, KeyCode.PAGE_UP, KeyCode.BACK_SPACE);
    private final List<KeyCode> nextKeys = Arrays.asList(KeyCode.RIGHT, KeyCode.DOWN, KeyCode.PAGE_DOWN, KeyCode.SPACE);
            
    private JavaSlideViewModel model = new JavaSlideViewModel(Paths.get("."));
    
    @FXML
    private void nextAction(ActionEvent event) {
        model.next().ifPresent(element -> changeContent(element, HPos.LEFT));
    }

    @FXML
    private void previousAction(ActionEvent event) {
        model.previous().ifPresent(element -> changeContent(element, HPos.RIGHT));
    }
    
    @FXML
    private void fullscreenAction(ActionEvent event) {
        Stage stage = (Stage)fullScreenButton.getParent().getScene().getWindow();
        stage.setFullScreen(fullScreenButton.isSelected());
    }
    
    private void setContent(Pane pane) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(pane);
        pane.prefWidthProperty().bind(contentPane.widthProperty());
        pane.prefHeightProperty().bind(contentPane.heightProperty());
    }
    
    private void changeContent(Pane pane, HPos direction) {
        Pane oldPane = (Pane) contentPane.getChildren().get(0);
        Transition slideOutTransition = createSlideOutTransition(oldPane, direction);
        slideOutTransition.setOnFinished(e -> setContent(pane));
        Transition slideInTransition = createSlideInTransition(pane, direction);
        SequentialTransition slideTransition = new SequentialTransition(slideOutTransition, slideInTransition);
        slideTransition.play();
    }
        
    private Transition createSlideOutTransition(Pane oldPane, HPos direction) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.75), oldPane);
        transition.setFromX(0);
        transition.setToX(direction == HPos.LEFT ? -oldPane.getWidth() : oldPane.getWidth());
        return transition;
    }
    
    private Transition createSlideInTransition(Pane pane, HPos direction) {
        double translateX = direction == HPos.LEFT ? contentPane.getWidth() : -contentPane.getWidth();
        pane.setTranslateX(translateX);
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.75), pane);
        transition.setFromX(translateX);
        transition.setToX(0);
        return transition;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootPane.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (previousKeys.contains(event.getCode())) {
                previousAction(new ActionEvent());
                event.consume();
            } else if (nextKeys.contains(event.getCode())) {
                nextAction(new ActionEvent());
                event.consume();
            }
        });
        model.next().ifPresent(this::setContent);
    }    
    
}
