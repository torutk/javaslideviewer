/*
 * © 2017 TAKAHASHI,Toru
 */
package javaslideviewer;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.Window;
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
    private void printAction(ActionEvent event) {
        Task<Boolean> printTask = new Task<Boolean>() {
            @Override
            public Boolean call() {
                return printNode(contentPane);
            }
        };
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(printTask);
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
    
    private boolean printNode(Node node) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) 
            return false;
        Window topWindow = node.getScene().getWindow();
        if (!job.showPrintDialog(topWindow))
            return false;
        Printer printer = job.getPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.EQUAL);
        double scaleX = pageLayout.getPrintableWidth() / topWindow.getWidth() * 0.9;
        double scaleY = pageLayout.getPrintableHeight() / topWindow.getHeight() * 0.9;
        Scale scale = new Scale(scaleX, scaleY);
        node.getTransforms().add(scale);
        if (job.printPage(node)) {
            node.getTransforms().remove(scale);
            return job.endJob();            
        }
        node.getTransforms().remove(scale);
        return false;
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
        rootPane.setOnMouseClicked(event -> {
            if (event.isAltDown()) {
                previousAction(new ActionEvent());
            } else {
                nextAction(new ActionEvent());
            }
        });
        model.next().ifPresent(this::setContent);
    }    
    
}
