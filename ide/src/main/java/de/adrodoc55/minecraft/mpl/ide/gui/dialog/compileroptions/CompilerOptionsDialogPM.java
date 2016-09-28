/*
 * Minecraft Programming Language (MPL): A language for easy development of command block
 * applications including an IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL.
 *
 * MPL is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MPL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MPL. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 *
 *
 * Minecraft Programming Language (MPL): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, inklusive einer IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL.
 *
 * MPL ist freie Software: Sie können diese unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.ide.gui.dialog.compileroptions;

import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DEBUG;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.DELETE_ON_UNINSTALL;
import static de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption.TRANSMITTER;

import java.util.ArrayList;
import java.util.List;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.BooleanPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.Options;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.Operation;

import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions;
import de.adrodoc55.minecraft.mpl.compilation.CompilerOptions.CompilerOption;
import de.adrodoc55.minecraft.mpl.version.MinecraftVersion;

/**
 * @author Adrodoc55
 */
public class CompilerOptionsDialogPM extends AbstractPM {
  final BooleanPM debug = new BooleanPM();
  final BooleanPM deleteOnUninstall = new BooleanPM();
  final BooleanPM transmitter = new BooleanPM();
  final TextPM version = new TextPM();
  final OperationPM ok = new OperationPM();

  private MinecraftVersion savedVersion = MinecraftVersion.getDefault();
  private CompilerOptions savedOptions = new CompilerOptions(DELETE_ON_UNINSTALL, TRANSMITTER);

  public CompilerOptionsDialogPM() {
    final Options<MinecraftVersion> versions = new Options<>();
    for (MinecraftVersion mv : MinecraftVersion.getValues()) {
      versions.put(mv, mv.toString());
    }
    version.setOptions(versions);
    version.setRestrictedToOptions(true);
    resetProperties();
    PMManager.setup(this);
  }

  public void resetProperties() {
    version.setText(savedVersion.toString());
    debug.setBoolean(savedOptions.hasOption(DEBUG));
    deleteOnUninstall.setBoolean(savedOptions.hasOption(CompilerOption.DELETE_ON_UNINSTALL));
    transmitter.setBoolean(savedOptions.hasOption(CompilerOption.TRANSMITTER));
  }

  public void save() {
    savedVersion = MinecraftVersion.getVersion(version.getText());
    savedOptions = toCompilerOptions();
  }

  private CompilerOptions toCompilerOptions() {
    List<CompilerOption> options = new ArrayList<>(3);
    if (debug.getBoolean())
      options.add(DEBUG);
    if (deleteOnUninstall.getBoolean())
      options.add(DELETE_ON_UNINSTALL);
    if (transmitter.getBoolean())
      options.add(TRANSMITTER);
    return new CompilerOptions(options);
  }

  public MinecraftVersion getSavedVersion() {
    return savedVersion;
  }

  public CompilerOptions getSavedOptions() {
    return savedOptions;
  }

  @Operation
  public void ok() {
    ok.check();
    save();
  }

}
