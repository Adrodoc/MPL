package de.adrodoc55.minecraft.mpl.ide.fx;

import javax.annotation.Nonnull;

import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;
import lombok.Data;

@Data
public class MplOptions {
  private final @Nonnull MinecraftVersion minecraftVersion;
  private final @Nonnull CompilerOptions compilerOptions;
}
