/*
 * © 2017 TAKAHASHI,Toru
 */
package javaslideviewer;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 *
 * @author toru
 */
public class Ccc008Controller implements Initializable {

    @FXML
    public void showDefaultStage(ActionEvent event) {
        Parent root = createSceneGraph();
        Stage slideStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 640, 480);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initOwner(slideStage);
        stage.setTitle("デフォルト設定のStage");
        stage.setOnCloseRequest(e -> {
            stage.close();
            e.consume();
        });
        
        stage.show();
    }
    
    @FXML
    public void showTransparentStage(ActionEvent event) {
        Parent root = createSceneGraph();
        Scene scene = new Scene(root, 640, 480);
        scene.setFill(Color.TRANSPARENT);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        Stage slideStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.initOwner(slideStage);
        stage.setTitle("デフォルト設定のStage");
        scene.setOnMouseClicked(e -> stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));
        stage.setOnCloseRequest(e -> {
            stage.close();
            e.consume();
        });
        stage.show();
    }
    
    private Parent createSceneGraph() {
        Group root = new Group();
        Circle circle = new Circle(320, 240, 240, Color.SKYBLUE);
        root.getChildren().add(circle);
        return root;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
}
