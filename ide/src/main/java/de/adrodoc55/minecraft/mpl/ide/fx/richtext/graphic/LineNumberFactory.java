package de.adrodoc55.minecraft.mpl.ide.fx.richtext.graphic;

import java.util.function.IntFunction;

import org.fxmisc.richtext.GenericStyledArea;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class LineNumberFactory implements IntFunction<Node> {
  private final Val<Integer> paragraphCount;

  public LineNumberFactory(GenericStyledArea<?, ?, ?> area) {
    paragraphCount = LiveList.sizeOf(area.getParagraphs());
  }

  @Override
  public Node apply(int idx) {
    Label node = new Label();
    Val<String> formatted = paragraphCount.map(n -> format(idx + 1, n));
    node.textProperty().bind(formatted.conditionOnShowing(node));
    node.getStyleClass().add("lineno");
    return node;
  }

  private String format(int x, int max) {
    int digits = (int) Math.log10(max) + 1;
    return String.format("%1$" + digits + "s", x);
  }
}
