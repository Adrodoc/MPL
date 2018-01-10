package de.adrodoc55.minecraft.mpl.ide.fx;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public interface ExceptionHandler {
  public static final ExceptionHandler JAVA_FX_ALERT_HANDLER = ex -> {
    Alert alert = new Alert(AlertType.ERROR, ex.getMessage());
    alert.setHeaderText(ex.getClass().getSimpleName());
    alert.showAndWait();
  };

  static ExceptionHandler getJavaFxAlertHandler() {
    return JAVA_FX_ALERT_HANDLER;
  }

  void handleException(Exception ex);
}
