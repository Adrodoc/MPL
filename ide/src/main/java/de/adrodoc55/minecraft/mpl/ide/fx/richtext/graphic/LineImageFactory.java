package de.adrodoc55.minecraft.mpl.ide.fx.richtext.graphic;

import java.util.function.IntFunction;

import javax.annotation.Nullable;

import org.reactfx.value.Val;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LineImageFactory implements IntFunction<Node> {
  private final ObservableMap<Integer, Image> images = FXCollections.observableHashMap();

  public void setImage(int idx, @Nullable Image image) {
    if (image == null) {
      images.remove(idx);
    } else {
      images.put(idx, image);
    }
  }

  @Override
  public Node apply(int idx) {
    ImageView view = new ImageView();
    view.setFitHeight(16);
    view.setFitWidth(16);
    Val<Image> image = Val.create(() -> images.get(idx), images);
    view.imageProperty().bind(image.conditionOnShowing(view));
    return view;
  }
}
