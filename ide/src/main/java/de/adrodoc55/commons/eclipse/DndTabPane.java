package de.adrodoc55.commons.eclipse;

import static org.eclipse.fx.ui.controls.tabpane.DndTabPaneFactory.FeedbackType.MARKER;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.eclipse.fx.ui.controls.markers.PositionMarker;
import org.eclipse.fx.ui.controls.markers.TabOutlineMarker;
import org.eclipse.fx.ui.controls.tabpane.DndTabPaneFactory.DragSetup;
import org.eclipse.fx.ui.controls.tabpane.DndTabPaneFactory.DropType;
import org.eclipse.fx.ui.controls.tabpane.DndTabPaneFactory.DroppedData;
import org.eclipse.fx.ui.controls.tabpane.DndTabPaneFactory.FeedbackData;
import org.eclipse.fx.ui.controls.tabpane.DndTabPaneFactory.FeedbackType;
import org.eclipse.fx.ui.controls.tabpane.GenericTab;
import org.eclipse.fx.ui.controls.tabpane.GenericTabPane;
import org.eclipse.fx.ui.controls.tabpane.skin.DnDTabPaneSkin;
import org.eclipse.fx.ui.controls.tabpane.skin.DnDTabPaneSkinFullDrag;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.scene.Node;
import javafx.scene.control.TabPane;

public class DndTabPane extends TabPane {
  private static MarkerFeedback CURRENT_FEEDBACK;

  private final BooleanProperty allowDetach = new SimpleBooleanProperty();
  private final ObjectProperty<FeedbackType> feedbackType = new SimpleObjectProperty<>(MARKER);

  private @Nullable Consumer<GenericTab> detachHandler;

  public DndTabPane() {
    skinProperty().addListener((observable, oldValue, newValue) -> {
      if (oldValue != null && oldValue instanceof DragSetup) {
        teardown((DragSetup) oldValue);
      }
    });
    allowDetach.addListener((observable, oldValue, newValue) -> {
      if (oldValue != newValue) {
        skinProperty().set(createDefaultSkin());
      }
    });
  }

  public BooleanProperty allowDetachProperty() {
    return allowDetach;
  }

  public boolean getAllowDetach() {
    return allowDetach.get();
  }

  public void setAllowDetach(boolean feedbackType) {
    this.allowDetach.set(feedbackType);
  }

  public ObjectProperty<FeedbackType> feedbackTypeProperty() {
    return feedbackType;
  }

  public FeedbackType getFeedbackType() {
    return feedbackType.get();
  }

  public void setFeedbackType(FeedbackType feedbackType) {
    this.feedbackType.set(feedbackType);
  }

  @Override
  protected javafx.scene.control.Skin<?> createDefaultSkin() {
    if (allowDetach.get()) {
      DnDTabPaneSkinFullDrag skin = new DnDTabPaneSkinFullDrag(this);
      setup(skin);
      return skin;
    } else {
      DnDTabPaneSkin skin = new DnDTabPaneSkin(this);
      setup(skin);
      return skin;
    }
  }

  /**
   * Setup insert marker
   *
   * @param layoutNode the layout node used to position
   * @param setup the setup
   */
  void setup(DragSetup setup) {
    setup.setStartFunction((t) -> Boolean.TRUE);
    setup.setFeedbackConsumer(this::handleFeedback);
    setup.setDropConsumer(this::handleDropped);
    setup.setDragFinishedConsumer(this::handleFinished);
  }

  void teardown(DragSetup setup) {
    setup.setStartFunction(null);
    setup.setFeedbackConsumer(null);
    setup.setDropConsumer(null);
    setup.setDragFinishedConsumer(null);
  }

  private void handleFeedback(FeedbackData data) {
    if (data.dropType == DropType.NONE) {
      cleanup();
      return;
    }

    MarkerFeedback f = CURRENT_FEEDBACK;
    if (f == null || !f.data.equals(data)) {
      cleanup();
      if (feedbackType.get() == FeedbackType.MARKER) {
        CURRENT_FEEDBACK = handleMarker(data);
      } else {
        CURRENT_FEEDBACK = handleOutline(data);
      }
    }
  }

  private void handleDropped(DroppedData data) {
    if (data.dropType == DropType.DETACH) {
      if (detachHandler != null) {
        detachHandler.accept(data.draggedTab);
      }
    } else if (data.targetTab != null) {
      GenericTabPane targetPane = data.targetTab.getOwner();
      data.draggedTab.getOwner().remove(data.draggedTab);
      int idx = targetPane.indexOf(data.targetTab);
      if (data.dropType == DropType.AFTER) {
        if (idx + 1 <= targetPane.getTabNumber()) {
          targetPane.add(idx + 1, data.draggedTab);
        } else {
          targetPane.add(data.draggedTab);
        }
      } else {
        targetPane.add(idx, data.draggedTab);
      }
      data.draggedTab.getOwner().select(data.draggedTab);
    }
  }

  private void handleFinished(GenericTab tab) {
    cleanup();
  }

  static void cleanup() {
    if (CURRENT_FEEDBACK != null) {
      CURRENT_FEEDBACK.hide();
      CURRENT_FEEDBACK = null;
    }
  }

  private MarkerFeedback handleMarker(FeedbackData data) {
    PositionMarker marker = null;
    for (Node n : getChildren()) {
      if (n instanceof PositionMarker) {
        marker = (PositionMarker) n;
      }
    }

    if (marker == null) {
      marker = new PositionMarker();
      marker.setManaged(false);
      getChildren().add(marker);
    } else {
      marker.setVisible(true);
    }

    double w = marker.getBoundsInLocal().getWidth();
    double h = marker.getBoundsInLocal().getHeight();

    double ratio = data.bounds.getHeight() / h;
    ratio += 0.1;
    marker.setScaleX(ratio);
    marker.setScaleY(ratio);

    double wDiff = w / 2;
    double hDiff = (h - h * ratio) / 2;

    if (data.dropType == DropType.AFTER) {
      marker.relocate(data.bounds.getMinX() + data.bounds.getWidth() - wDiff,
          data.bounds.getMinY() - hDiff);
    } else {
      marker.relocate(data.bounds.getMinX() - wDiff, data.bounds.getMinY() - hDiff);
    }

    final PositionMarker fmarker = marker;

    return new MarkerFeedback(data) {

      @Override
      public void hide() {
        fmarker.setVisible(false);
      }
    };
  }

  private MarkerFeedback handleOutline(FeedbackData data) {
    TabOutlineMarker marker = null;

    for (Node n : getChildren()) {
      if (n instanceof TabOutlineMarker) {
        marker = (TabOutlineMarker) n;
      }
    }

    if (marker == null) {
      marker =
          new TabOutlineMarker(
              getBoundsInLocal(), new BoundingBox(data.bounds.getMinX(), data.bounds.getMinY(),
                  data.bounds.getWidth(), data.bounds.getHeight()),
              data.dropType == DropType.BEFORE);
      marker.setManaged(false);
      marker.setMouseTransparent(true);
      getChildren().add(marker);
    } else {
      marker
          .updateBounds(
              getBoundsInLocal(), new BoundingBox(data.bounds.getMinX(), data.bounds.getMinY(),
                  data.bounds.getWidth(), data.bounds.getHeight()),
              data.dropType == DropType.BEFORE);
      marker.setVisible(true);
    }

    final TabOutlineMarker fmarker = marker;

    return new MarkerFeedback(data) {

      @Override
      public void hide() {
        fmarker.setVisible(false);
      }
    };
  }

  private abstract static class MarkerFeedback {
    public final FeedbackData data;

    public MarkerFeedback(FeedbackData data) {
      this.data = data;
    }

    public abstract void hide();
  }
}
