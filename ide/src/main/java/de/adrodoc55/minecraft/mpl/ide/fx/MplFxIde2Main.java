package de.adrodoc55.minecraft.mpl.ide.fx;

import java.io.IOException;

import org.eclipse.fx.ui.controls.filesystem.ResourceTreeView;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MplFxIde2Main extends Application {
  @FXML
  private ResourceTreeView packageExporer;

  @Override
  public void start(Stage stage) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("/mpl-ide.fxml"));


    Scene scene = new Scene(root, 1000, 500);
    scene.getStylesheets().add(getClass().getResource("/mpl-ide.css").toExternalForm());
    scene.getStylesheets()
        .add(getClass().getResource("/syntax/highlighting/mpl.css").toExternalForm());


    stage.setTitle("Minecraft Programming Language - local build");
    stage.setScene(scene);
    stage.show();
  }


  public static void main(String[] args) {
    launch(args);
  }
}
