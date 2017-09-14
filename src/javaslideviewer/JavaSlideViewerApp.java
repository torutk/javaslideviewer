/*
 * © 2017 TAKAHASHI,Toru
 */
package javaslideviewer;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 * プレゼンテーション用のスライドビューア。
 */
public class JavaSlideViewerApp extends Application {
    private double initialSceneWidth; // 初期起動時のシーンの大きさ（幅）
    private double initialSceneHeight; // 初期起動時のシーンの大きさ（高さ）
    /* ルートノードに適用する座標変換（スケール）のプロパティ */
    private final ObjectProperty<Scale> scaleProperty = new SimpleObjectProperty<>(new Scale(1d, 1d, 0d, 0d));
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("JavaSlideMainView.fxml"));
        root.getTransforms().add(scaleProperty.get());

        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
        
        // 起動時のシーンの大きさを保存（stage.show()の後でないと値が確定しない）
        initialSceneWidth = scene.getWidth();
        initialSceneHeight = scene.getHeight();
        // シーンの大きさが変更された際、起動時のシーンの大きさに対する比をルートノードのスケールに反映
        scaleProperty.get().xProperty().bind(Bindings.divide(scene.widthProperty(), initialSceneWidth));
        scaleProperty.get().yProperty().bind(Bindings.divide(scene.heightProperty(), initialSceneHeight));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
