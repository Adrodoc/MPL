package de.adrodoc55.minecraft.mpl.ide.fx;

import java.nio.file.Path;

import javax.annotation.Nonnull;

import org.eclipse.fx.code.editor.fx.TextEditor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString(of = "path")
public class MplEditorData {
  private @Nonnull final Path path;
  private @Nonnull final TextEditor editor;
}
