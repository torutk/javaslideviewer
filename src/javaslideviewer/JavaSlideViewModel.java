/*
 * © 2017 TAKAHASHI,Toru
 */
package javaslideviewer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

/**
 * スライドのデータを保持しイテレートするモデルクラス。
 * <ul>
 * <li>指定したフォルダに存在する拡張子 .fxml のファイルを名前順でソートし保持する
 * <li>イテレートの際にfxmlファイルをロードしてノードのツリーを構成（プログラムを再起動しなくてもFXMLファイルの編集がページ遷移で反映）
 * </ul>
 */
public class JavaSlideViewModel {
    private static final Logger logger = Logger.getLogger(JavaSlideViewModel.class.getName());

    private Path folder;    
    private List<String> slides;
    private int currentIndex = -1;

    public JavaSlideViewModel(Path folder) {
        if (Files.isDirectory(folder)) {
            this.folder = folder;
        }
        slides = new ArrayList<String>();
        folderData();
    }

    public Optional<Pane> next() {
        if (slides.size() <= currentIndex + 1) {
            return Optional.empty();
        }
        return loadFxml(slides.get(++currentIndex));
    }    
    
    public Optional<Pane> previous() {
       if (currentIndex <= 0) {
           return Optional.empty();
       }
       return loadFxml(slides.get(--currentIndex));
    }
    
    private Optional<Pane> loadFxml(String fileName) {
        try {
            return Optional.of(FXMLLoader.load(Paths.get(fileName).toUri().toURL()));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return Optional.empty();
    }
    
    
    /**
     * 指定されたフォルダにあるFXMLファイル群からモデルを構築。
     */
    public void folderData() {
        if (folder == null) {
            testData();
        }
        try (Stream<Path> stream = Files.list(folder)) {
            slides = stream.map(path -> path.toFile().getName())
                    .filter(name -> name.toLowerCase().endsWith(".fxml"))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        logger.info(() -> String.format("Slides are %s%n", slides));
    }
    
    private void testData() {
        slides.addAll(Arrays.asList(
                "SampleAlfa.fxml", "SampleBravo.fxml", "SampleCharlie.fxml"
        ));
    }
}
