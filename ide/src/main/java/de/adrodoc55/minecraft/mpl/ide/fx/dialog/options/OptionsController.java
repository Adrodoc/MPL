package de.adrodoc55.minecraft.mpl.ide.fx.dialog.options;

import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DELETE_ON_UNINSTALL;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.FUNCTIONS;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;
import de.adrodoc55.minecraft.mpl.ide.fx.MplOptions;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class OptionsController {
  @FXML
  private Label minecraftVersionLabel;
  @FXML
  private ChoiceBox<MinecraftVersion> minecraftVersion;
  @FXML
  private CheckBox useRedstoneBlocks;
  @FXML
  private CheckBox deleteOnUninstall;
  @FXML
  private CheckBox debug;

  @FXML
  private void initialize() {
    minecraftVersionLabel.setLabelFor(minecraftVersion);
    minecraftVersion.getItems().addAll(MinecraftVersion.getValues());
  }

  public void initialize(MplOptions oldOptions) {
    minecraftVersion.getSelectionModel().select(oldOptions.getMinecraftVersion());
    CompilerOptions compilerOptions = oldOptions.getCompilerOptions();
    useRedstoneBlocks.setSelected(compilerOptions.hasOption(CompilerOption.TRANSMITTER));
    deleteOnUninstall.setSelected(compilerOptions.hasOption(CompilerOption.DELETE_ON_UNINSTALL));
    debug.setSelected(compilerOptions.hasOption(CompilerOption.DEBUG));
  }

  public MplOptions getMplOptions() {
    MinecraftVersion version = minecraftVersion.getSelectionModel().getSelectedItem();
    List<CompilerOption> options = new ArrayList<>(3);
    if (useRedstoneBlocks.isSelected())
      options.add(TRANSMITTER);
    if (deleteOnUninstall.isSelected())
      options.add(DELETE_ON_UNINSTALL);
    if (debug.isSelected())
      options.add(DEBUG);
    options.add(FUNCTIONS);
    CompilerOptions compilerOptions = new CompilerOptions(options);
    return new MplOptions(version, compilerOptions);
  }
}
