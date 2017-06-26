package de.adrodoc55.minecraft.mpl.ide.fx.editor;

import java.io.File;

import javax.annotation.Nullable;

import de.adrodoc55.minecraft.mpl.compilation.MplCompilationResult;
import de.adrodoc55.minecraft.mpl.ide.fx.MplOptions;

public interface MplEditorContext {
  MplOptions getMplOptions();

  @Nullable
  MplCompilationResult compile(File file, boolean silent);
}
