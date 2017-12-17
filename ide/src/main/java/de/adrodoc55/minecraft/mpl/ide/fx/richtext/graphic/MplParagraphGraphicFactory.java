package de.adrodoc55.minecraft.mpl.ide.fx.richtext.graphic;

import java.util.function.IntFunction;

import javax.annotation.Nullable;

import org.fxmisc.richtext.GenericStyledArea;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class MplParagraphGraphicFactory implements IntFunction<Node> {
  private static final Insets DEFAULT_PADDING = new Insets(0, 2, 0, 0);
  private static final Background DEFAULT_BACKGROUND =
      new Background(new BackgroundFill(Color.web("#ddd"), null, null));

  private final LineImageFactory lineImageFactory;
  private final LineNumberFactory lineNumberFactory;

  public MplParagraphGraphicFactory(GenericStyledArea<?, ?, ?> area) {
    lineImageFactory = new LineImageFactory();
    lineNumberFactory = new LineNumberFactory(area);
  }

  public void setImage(int idx, @Nullable Image image) {
    lineImageFactory.setImage(idx, image);
  }

  @Override
  public Node apply(int idx) {
    HBox result = new HBox(//
        lineImageFactory.apply(idx), //
        lineNumberFactory.apply(idx)//
    );
    result.setBackground(DEFAULT_BACKGROUND);
    result.setPadding(DEFAULT_PADDING);
    result.getStyleClass().add("line-ruler");
    return result;
  }
}
