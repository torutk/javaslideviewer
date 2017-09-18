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
import javafx.scene.control.Button;
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
 * 操作は、画面右下のボタン（[前へ][次へ][全画面]）、またはキー押下で行う。
 */
public class JavaSlideMainViewController implements Initializable {
    
    @FXML
    private Pane contentPane;
    @FXML
    private Button printButton;
    @FXML
    private ToggleButton fullScreenButton;
    @FXML
    private Parent rootPane;
    
    // スライドの切り替えを行うキーをリストで定義
    private final List<KeyCode> previousKeys = Arrays.asList(KeyCode.LEFT, KeyCode.UP, KeyCode.PAGE_UP, KeyCode.BACK_SPACE);
    private final List<KeyCode> nextKeys = Arrays.asList(KeyCode.RIGHT, KeyCode.DOWN, KeyCode.PAGE_DOWN, KeyCode.SPACE);
            
    // スライドとなる画面のリストを管理、イテレートするモデル
    private JavaSlideViewModel model = new JavaSlideViewModel(Paths.get("."));
    
    // 次のスライドへ遷移する
    @FXML
    private void nextAction(ActionEvent event) {
        model.next().ifPresent(element -> changeContent(element, HPos.LEFT));
    }

    // 前のスライドへ遷移する
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
    
    // フルスクリーンモードへ移行またはフルスクリーンモードを解除する
    @FXML
    private void fullscreenAction(ActionEvent event) {
        Stage stage = (Stage)fullScreenButton.getParent().getScene().getWindow();
        stage.setFullScreen(fullScreenButton.isSelected());
    }
    
    // 指定した pane をスライドとして表示する。
    private void setContent(Pane pane) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(pane);
        pane.prefWidthProperty().bind(contentPane.widthProperty());
        pane.prefHeightProperty().bind(contentPane.heightProperty());
    }
    
    // 指定した pane を、アニメーション効果を付けて表示する。
    // 現在表示している pane を指定した方向にスライドアウトさせ、指定した pane を指定した方向へスライドインする。
    private void changeContent(Pane pane, HPos direction) {
        Pane oldPane = (Pane) contentPane.getChildren().get(0);
        Transition slideOutTransition = createSlideOutTransition(oldPane, direction);
        slideOutTransition.setOnFinished(e -> setContent(pane));
        Transition slideInTransition = createSlideInTransition(pane, direction);
        SequentialTransition slideTransition = new SequentialTransition(slideOutTransition, slideInTransition);
        slideTransition.play();
    }

    // 指定した pane を指定した方向へスライドアウトするアニメーションを生成する
    private Transition createSlideOutTransition(Pane oldPane, HPos direction) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.75), oldPane);
        transition.setFromX(0);
        transition.setToX(direction == HPos.LEFT ? -oldPane.getWidth() : oldPane.getWidth());
        return transition;
    }

    // 指定した pane を指定した方向へスライドインするアニメーションを生成する
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
        if (job == null) {
            return false;
        }
        Window topWindow = node.getScene().getWindow();
        if (!job.showPrintDialog(topWindow)) {
            return false;
        }
        Printer printer = job.getPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.EQUAL);
        double scaleX = pageLayout.getPrintableWidth() / topWindow.getWidth() * 0.8;
        double scaleY = pageLayout.getPrintableHeight() / topWindow.getHeight() * 0.8;
        double originalScaleX = node.getScaleX();
        double originalScaleY = node.getScaleY();
        node.setScaleX(scaleX);
        node.setScaleY(scaleY);
        boolean isPrinted = job.printPage(node);
        node.setScaleX(originalScaleX);
        node.setScaleY(originalScaleY);
        if (isPrinted) {
            return job.endJob();
        }
        return false;
    }
    
    /**
     * コントローラを初期化する（対になるFXMLファイルがロードされたときに呼ばれる）。
     * <ul>
     * <li>マウスクリックでスライドの次への遷移のイベント処理
     * <li>キー操作によるスライドの前/次遷移のイベント処理
     * <li>スライドの初期設定
     * </ul>
     * @param url
     * @param rb 
     */
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
        printButton.disableProperty().bind(fullScreenButton.selectedProperty());
    }    
    
}
