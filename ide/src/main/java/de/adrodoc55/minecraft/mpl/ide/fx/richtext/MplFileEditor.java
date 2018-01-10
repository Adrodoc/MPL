package de.adrodoc55.minecraft.mpl.ide.fx.richtext;

import static com.google.common.base.Preconditions.checkNotNull;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;
import static org.fxmisc.wellbehaved.event.InputMap.consume;
import static org.fxmisc.wellbehaved.event.InputMap.sequence;
import static org.fxmisc.wellbehaved.event.Nodes.addInputMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nullable;

import com.google.common.io.CharStreams;

import de.adrodoc55.minecraft.mpl.ide.fx.ExceptionHandler;
import javafx.scene.input.KeyEvent;

public class MplFileEditor extends MplEditor2 {
  private final ExceptionHandler exceptionHandler;
  private final Charset charset;
  private Path path;

  public static @Nullable MplFileEditor open(Path path, Charset charset,
      ExceptionHandler exceptionHandler) {
    try {
      return new MplFileEditor(path, charset, exceptionHandler);
    } catch (IOException ex) {
      exceptionHandler.handleException(ex);
      return null;
    }
  }

  private MplFileEditor(Path path, Charset charset, ExceptionHandler exceptionHandler)
      throws IOException {
    super(CharStreams.toString(new InputStreamReader(Files.newInputStream(path), charset)));
    this.exceptionHandler = checkNotNull(exceptionHandler, "exceptionHandler == null!");
    this.charset = checkNotNull(charset, "charset == null!");
    setPath(path);
  }

  {
    addInputMap(this, sequence(//
        consume(keyPressed(S, SHORTCUT_DOWN), this::save)//
    ));
  }

  private void save(KeyEvent e) {
    try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(path), charset)) {
      writer.write(getText());
    } catch (IOException ex) {
      exceptionHandler.handleException(ex);
    }
  }

  /**
   * @return the value of {@link #path}
   */
  public Path getPath() {
    return path;
  }

  /**
   * @param path the new value for {@link #path}
   */
  public void setPath(Path path) {
    this.path = checkNotNull(path, "path == null!");
  }
}
