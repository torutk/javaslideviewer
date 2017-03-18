/*
 * © 2017 TAKAHASHI,Toru
 */
package javaslideviewer;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author toru
 */
public class JavaSlideMainViewController implements Initializable {
    
    @FXML
    private Pane contentPane;
    @FXML
    private ToggleButton fullScreenButton;
    
    private JavaSlideViewModel model = new JavaSlideViewModel(Paths.get("."));
    
    @FXML
    private void nextAction(ActionEvent event) {
        model.next().ifPresent(this::changeContent);
    }

    @FXML
    private void previousAction(ActionEvent event) {
        model.previous().ifPresent(this::changeContent);
    }
    
    @FXML
    private void fullscreenAction(ActionEvent event) {
        Stage stage = (Stage)fullScreenButton.getParent().getScene().getWindow();
        stage.setFullScreen(fullScreenButton.isSelected());
    }
    
    private void changeContent(Pane pane) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(pane);
        pane.prefWidthProperty().bind(contentPane.widthProperty());
        pane.prefHeightProperty().bind(contentPane.heightProperty());
    }
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fullScreenButton.getScene();
        fullScreenButton.getScene().getWindow();
    }    
    
}
