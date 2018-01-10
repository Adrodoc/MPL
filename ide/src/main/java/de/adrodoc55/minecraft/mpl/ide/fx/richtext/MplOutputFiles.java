package de.adrodoc55.minecraft.mpl.ide.fx.richtext;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import javax.annotation.Nullable;

import de.adrodoc55.minecraft.mpl.ide.fx.MplConstants;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;

public class MplOutputFiles {
  public interface Context {
    Window getWindow();

    String getFileNameWithoutExtension();
  }

  private final Context context;

  public MplOutputFiles(Context context) {
    this.context = checkNotNull(context, "context == null!");
  }

  private @Nullable File chooseOutputFile(@Nullable File initialDir,
      ExtensionFilter extensionFilter) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Compile to " + extensionFilter.getDescription());
    fileChooser.setInitialDirectory(initialDir);
    String initialFileName = context.getFileNameWithoutExtension();
    fileChooser.setInitialFileName(initialFileName);
    fileChooser.getExtensionFilters().add(extensionFilter);
    return fileChooser.showSaveDialog(context.getWindow());
  }

  private @Nullable File structureFile;

  public @Nullable File getStructureFile(@Nullable File initialDir, boolean useCachedFile) {
    if (!useCachedFile || structureFile == null) {
      structureFile = chooseOutputFile(initialDir, MplConstants.STRUCTURE_EXTENSION);
    }
    return structureFile;
  }

  private @Nullable File schematicFile;

  public @Nullable File getSchematicFile(@Nullable File initialDir, boolean useCachedFile) {
    if (!useCachedFile || schematicFile == null) {
      schematicFile = chooseOutputFile(initialDir, MplConstants.SCHEMATIC_EXTENSION);
    }
    return schematicFile;
  }

  private @Nullable File mceditFile;

  public @Nullable File getMceditFile(@Nullable File initialDir, boolean useCachedFile) {
    if (!useCachedFile || mceditFile == null) {
      mceditFile = chooseOutputFile(initialDir, MplConstants.MCEDIT_EXTENSION);
    }
    return mceditFile;
  }
}
