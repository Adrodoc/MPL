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
package de.adrodoc55.minecraft.mpl.ide.gui;

import static de.adrodoc55.minecraft.mpl.ide.gui.MplFramePM.COMPILE_TO_COMMAND;
import static de.adrodoc55.minecraft.mpl.ide.gui.MplFramePM.COMPILE_TO_FILTER;
import static de.adrodoc55.minecraft.mpl.ide.gui.MplFramePM.COMPILE_TO_SCHEMATIC;
import static de.adrodoc55.minecraft.mpl.ide.gui.MplFramePM.COMPILE_TO_STRUCTURE;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.event.ElementChangedEvent;
import org.beanfabrics.event.ElementsAddedEvent;
import org.beanfabrics.event.ElementsDeselectedEvent;
import org.beanfabrics.event.ElementsRemovedEvent;
import org.beanfabrics.event.ElementsReplacedEvent;
import org.beanfabrics.event.ElementsSelectedEvent;
import org.beanfabrics.event.ListListener;
import org.beanfabrics.model.IListPM;
import org.beanfabrics.model.ListPM;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnMenuItem;

/**
 * The MplFrame is a {@link View} on a {@link MplFramePM}.
 *
 * @author Adrodoc55
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class MplFrame extends JFrame implements View<MplFramePM>, ModelSubscriber {

  private static final long serialVersionUID = 1L;

  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private JMenuBar menuBar;
  private JMenu mnFile;
  private BnMenuItem bnmntmNew;
  private BnMenuItem bnmntmOpen;
  private BnMenuItem bnmntmSave;
  private BnMenuItem bnmntmSaveUnder;
  private BnMenuItem bnmntmCompileToCommand;
  private BnMenuItem bnmntmCompileToStructure;
  private BnMenuItem bnmntmCompileToStructureUnder;
  private BnMenuItem bnmntmCompileToSchematic;
  private BnMenuItem bnmntmCompileToSchematicUnder;
  private BnMenuItem bnmntmCompileToFilter;
  private BnMenuItem bnmntmCompileToFilterUnder;
  private JMenu mnOptions;
  private BnMenuItem bnmntmCompilerOptions;
  private JToolBar toolBar;
  private BnButton bnbtnNew;
  private BnButton bnbtnOpen;
  private BnButton bnbtnSave;
  private BnButton bnbtnCompileToCommand;
  private BnButton bnbtnCompilerToStructure;
  private BnButton bnbtnCompileToSchematic;
  private BnButton bnbtnCompileToFilter;
  private JTabbedPane tabbedPane;

  /**
   * Constructs a new <code>MplFrame</code>.
   */
  public MplFrame() {
    super("Minecraft Programming Language - " + getVersion());
    setIconImage(Toolkit.getDefaultToolkit()
        .getImage(MplFrame.class.getResource("/icons/command_block_icon.png")));
    init();
    setSize(1000, 500);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        MplFramePM pModel = getPresentationModel();
        if (pModel == null) {
          System.exit(0);
        }
        pModel.terminate();
      }
    });
    addWindowFocusListener(new WindowAdapter() {
      @Override
      public void windowGainedFocus(WindowEvent e) {
        MplFramePM pModel = getPresentationModel();
        if (pModel != null) {
          pModel.checkFiles();
        }
      }
    });
  }

  private static String getVersion() {
    String version = MplFrame.class.getPackage().getImplementationVersion();
    if (version != null) {
      return version;
    } else {
      return "local build";
    }
  }

  private void init() {
    setJMenuBar(getMenuBar_1());
    getContentPane().add(getToolBar(), BorderLayout.NORTH);
    getContentPane().add(getTabbedPane(), BorderLayout.CENTER);
  }

  /**
   * Returns the local {@link ModelProvider} for this class.
   *
   * @return the local <code>ModelProvider</code>
   * @wbp.nonvisual location=20,550
   */
  protected ModelProvider getLocalModelProvider() {
    if (localModelProvider == null) {
      localModelProvider = new ModelProvider(); // @wb:location=10,430
      localModelProvider.setPresentationModelType(MplFramePM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  @Override
  public MplFramePM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  @Override
  public void setPresentationModel(MplFramePM pModel) {
    ListListener l = new ListListener() {
      @Override
      public void elementsSelected(ElementsSelectedEvent evt) {
        getTabbedPane().setSelectedIndex(evt.getBeginIndex());
      }

      @Override
      public void elementsReplaced(ElementsReplacedEvent evt) {
        int beginIndex = evt.getBeginIndex();
        int length = evt.getLength();
        this.remove(beginIndex, length);
        @SuppressWarnings("unchecked")
        IListPM<MplEditorPM> list = (IListPM<MplEditorPM>) evt.getSource();
        this.add(list, beginIndex, length);
      }

      @Override
      public void elementsRemoved(ElementsRemovedEvent evt) {
        int beginIndex = evt.getBeginIndex();
        int length = evt.getLength();
        this.remove(beginIndex, length);
      }

      @Override
      public void elementsDeselected(ElementsDeselectedEvent evt) {}

      @Override
      public void elementsAdded(ElementsAddedEvent evt) {
        int beginIndex = evt.getBeginIndex();
        int length = evt.getLength();
        @SuppressWarnings("unchecked")
        IListPM<MplEditorPM> list = (IListPM<MplEditorPM>) evt.getSource();
        this.add(list, beginIndex, length);
      }

      @Override
      public void elementChanged(ElementChangedEvent evt) {}

      private void remove(int beginIndex, int length) {
        for (int i = 0; i < length; i++) {
          int index = beginIndex + i;
          getTabbedPane().remove(index);
        }
      }

      private void add(IListPM<MplEditorPM> list, int beginIndex, int length) {
        for (int i = 0; i < length; i++) {
          int index = beginIndex + i;
          MplEditorPM editorPm = list.getAt(index);
          this.addMplEditor(index, editorPm);
        }
      }

      private void addMplEditor(int i, MplEditorPM editorPm) {
        MplEditor editor = new MplEditor();
        editor.setPresentationModel(editorPm);
        editor.getTextPane().getCaret().setDot(0);
        editor.discardAllEdits();
        editorPm.setView(editor);
        getTabbedPane().insertTab(editorPm.getTitle(), null, editor, null, i);
        TabCloseComponent tabComponent = new TabCloseComponent();
        tabComponent.setPresentationModel(editorPm);
        getTabbedPane().setTabComponentAt(i, tabComponent);
      }
    };
    pModel.editors.addListListener(l);
    getLocalModelProvider().setPresentationModel(pModel);
  }

  /** {@inheritDoc} */
  @Override
  public IModelProvider getModelProvider() {
    return this.link.getModelProvider();
  }

  /** {@inheritDoc} */
  @Override
  public void setModelProvider(IModelProvider modelProvider) {
    this.link.setModelProvider(modelProvider);
  }

  /** {@inheritDoc} */
  @Override
  public Path getPath() {
    return this.link.getPath();
  }

  /** {@inheritDoc} */
  @Override
  public void setPath(Path path) {
    this.link.setPath(path);
  }

  private JMenuBar getMenuBar_1() {
    if (menuBar == null) {
      menuBar = new JMenuBar();
      menuBar.add(getMnFile());
      menuBar.add(getMnOptions());
    }
    return menuBar;
  }

  private JMenu getMnFile() {
    if (mnFile == null) {
      mnFile = new JMenu("File");
      mnFile.add(getBnmntmNew());
      mnFile.add(getBnmntmOpen());
      mnFile.add(getBnmntmSave());
      mnFile.add(getBnmntmSaveUnder());
      mnFile.add(getBnmntmCompileToCommand());
      mnFile.add(getBnmntmCompileToStructure());
      mnFile.add(getBnmntmCompileToStructureUnder());
      mnFile.add(getBnmntmCompileToSchematic());
      mnFile.add(getBnmntmCompileToSchematicUnder());
      mnFile.add(getBnmntmCompileToFilter());
      mnFile.add(getBnmntmCompileToFilterUnder());
    }
    return mnFile;
  }

  private BnMenuItem getBnmntmNew() {
    if (bnmntmNew == null) {
      bnmntmNew = new BnMenuItem();
      bnmntmNew.setPath(new Path("this.newFile"));
      bnmntmNew.setModelProvider(getLocalModelProvider());
      bnmntmNew.setText("New");
    }
    return bnmntmNew;
  }

  private BnMenuItem getBnmntmOpen() {
    if (bnmntmOpen == null) {
      bnmntmOpen = new BnMenuItem();
      bnmntmOpen.setPath(new Path("this.openFile"));
      bnmntmOpen.setModelProvider(getLocalModelProvider());
      bnmntmOpen.setText("Open");
    }
    return bnmntmOpen;
  }

  private BnMenuItem getBnmntmSave() {
    if (bnmntmSave == null) {
      bnmntmSave = new BnMenuItem();
      bnmntmSave.setPath(new Path("this.saveFile"));
      bnmntmSave.setModelProvider(getLocalModelProvider());
      bnmntmSave.setText("Save");
    }
    return bnmntmSave;
  }

  private BnMenuItem getBnmntmSaveUnder() {
    if (bnmntmSaveUnder == null) {
      bnmntmSaveUnder = new BnMenuItem();
      bnmntmSaveUnder.setPath(new Path("this.saveFileUnder"));
      bnmntmSaveUnder.setModelProvider(getLocalModelProvider());
      bnmntmSaveUnder.setText("Save under...");
    }
    return bnmntmSaveUnder;
  }

  private BnMenuItem getBnmntmCompileToCommand() {
    if (bnmntmCompileToCommand == null) {
      bnmntmCompileToCommand = new BnMenuItem();
      bnmntmCompileToCommand.setPath(new Path("this.compileToCommand"));
      bnmntmCompileToCommand.setModelProvider(getLocalModelProvider());
      bnmntmCompileToCommand.setText(COMPILE_TO_COMMAND);
    }
    return bnmntmCompileToCommand;
  }

  private BnMenuItem getBnmntmCompileToStructure() {
    if (bnmntmCompileToStructure == null) {
      bnmntmCompileToStructure = new BnMenuItem();
      bnmntmCompileToStructure.setPath(new Path("this.compileToStructure"));
      bnmntmCompileToStructure.setModelProvider(getLocalModelProvider());
      bnmntmCompileToStructure.setText(COMPILE_TO_STRUCTURE);
    }
    return bnmntmCompileToStructure;
  }

  private BnMenuItem getBnmntmCompileToStructureUnder() {
    if (bnmntmCompileToStructureUnder == null) {
      bnmntmCompileToStructureUnder = new BnMenuItem();
      bnmntmCompileToStructureUnder.setPath(new Path("this.compileToStructureUnder"));
      bnmntmCompileToStructureUnder.setModelProvider(getLocalModelProvider());
      bnmntmCompileToStructureUnder.setText(COMPILE_TO_STRUCTURE + " under ...");
    }
    return bnmntmCompileToStructureUnder;
  }

  private BnMenuItem getBnmntmCompileToSchematic() {
    if (bnmntmCompileToSchematic == null) {
      bnmntmCompileToSchematic = new BnMenuItem();
      bnmntmCompileToSchematic.setPath(new Path("this.compileToSchematic"));
      bnmntmCompileToSchematic.setModelProvider(getLocalModelProvider());
      bnmntmCompileToSchematic.setText(COMPILE_TO_SCHEMATIC);
    }
    return bnmntmCompileToSchematic;
  }

  private BnMenuItem getBnmntmCompileToSchematicUnder() {
    if (bnmntmCompileToSchematicUnder == null) {
      bnmntmCompileToSchematicUnder = new BnMenuItem();
      bnmntmCompileToSchematicUnder.setPath(new Path("this.compileToSchematicUnder"));
      bnmntmCompileToSchematicUnder.setModelProvider(getLocalModelProvider());
      bnmntmCompileToSchematicUnder.setText(COMPILE_TO_SCHEMATIC + " under ...");
    }
    return bnmntmCompileToSchematicUnder;
  }

  private BnMenuItem getBnmntmCompileToFilter() {
    if (bnmntmCompileToFilter == null) {
      bnmntmCompileToFilter = new BnMenuItem();
      bnmntmCompileToFilter.setPath(new Path("this.compileToFilter"));
      bnmntmCompileToFilter.setModelProvider(getLocalModelProvider());
      bnmntmCompileToFilter.setText(COMPILE_TO_FILTER);
    }
    return bnmntmCompileToFilter;
  }

  private BnMenuItem getBnmntmCompileToFilterUnder() {
    if (bnmntmCompileToFilterUnder == null) {
      bnmntmCompileToFilterUnder = new BnMenuItem();
      bnmntmCompileToFilterUnder.setPath(new Path("this.compileToFilterUnder"));
      bnmntmCompileToFilterUnder.setModelProvider(getLocalModelProvider());
      bnmntmCompileToFilterUnder.setText(COMPILE_TO_FILTER + " under ...");
    }
    return bnmntmCompileToFilterUnder;
  }

  private JMenu getMnOptions() {
    if (mnOptions == null) {
      mnOptions = new JMenu("Options");
      mnOptions.add(getBnmntmCompilerOptions());
    }
    return mnOptions;
  }

  private BnMenuItem getBnmntmCompilerOptions() {
    if (bnmntmCompilerOptions == null) {
      bnmntmCompilerOptions = new BnMenuItem();
      bnmntmCompilerOptions.setPath(new Path("this.openOptionsDialog"));
      bnmntmCompilerOptions.setModelProvider(getLocalModelProvider());
      bnmntmCompilerOptions.setText("Compiler Options");
    }
    return bnmntmCompilerOptions;
  }

  private JToolBar getToolBar() {
    if (toolBar == null) {
      toolBar = new JToolBar();
      toolBar.add(getBnbtnNew());
      toolBar.add(getBnbtnOpen());
      toolBar.add(getBnbtnSave());
      toolBar.add(getBnbtnCompileToCommand());
      toolBar.add(getBnbtnCompilerToStructure());
      toolBar.add(getBnbtnCompileToSchematic());
      toolBar.add(getBnbtnCompileToFilter());
    }
    return toolBar;
  }

  private BnButton getBnbtnNew() {
    if (bnbtnNew == null) {
      bnbtnNew = new BnButton();
      bnbtnNew.setIcon(new ImageIcon(MplFrame.class.getResource("/icons/new_file_icon_16.png")));
      bnbtnNew.setModelProvider(getLocalModelProvider());
      bnbtnNew.setPath(new Path("this.newFile"));
    }
    return bnbtnNew;
  }

  private BnButton getBnbtnOpen() {
    if (bnbtnOpen == null) {
      bnbtnOpen = new BnButton();
      bnbtnOpen.setIcon(new ImageIcon(MplFrame.class.getResource("/icons/folder_icon_16.png")));
      bnbtnOpen.setPath(new Path("this.openFile"));
      bnbtnOpen.setModelProvider(getLocalModelProvider());
    }
    return bnbtnOpen;
  }

  private BnButton getBnbtnSave() {
    if (bnbtnSave == null) {
      bnbtnSave = new BnButton();
      bnbtnSave.setIcon(new ImageIcon(MplFrame.class.getResource("/icons/disk_icon_16.png")));
      bnbtnSave.setPath(new Path("this.saveFile"));
      bnbtnSave.setModelProvider(getLocalModelProvider());
    }
    return bnbtnSave;
  }

  private BnButton getBnbtnCompileToCommand() {
    if (bnbtnCompileToCommand == null) {
      bnbtnCompileToCommand = new BnButton();
      bnbtnCompileToCommand
          .setIcon(new ImageIcon(MplFrame.class.getResource("/icons/command_block_icon_16.png")));
      bnbtnCompileToCommand.setPath(new Path("this.compileToCommand"));
      bnbtnCompileToCommand.setModelProvider(getLocalModelProvider());
    }
    return bnbtnCompileToCommand;
  }

  private BnButton getBnbtnCompilerToStructure() {
    if (bnbtnCompilerToStructure == null) {
      bnbtnCompilerToStructure = new BnButton();
      bnbtnCompilerToStructure
          .setIcon(new ImageIcon(MplFrame.class.getResource("/icons/structure_block_load_16.png")));
      bnbtnCompilerToStructure.setPath(new Path("this.compileToStructure"));
      bnbtnCompilerToStructure.setModelProvider(getLocalModelProvider());
    }
    return bnbtnCompilerToStructure;
  }

  private BnButton getBnbtnCompileToSchematic() {
    if (bnbtnCompileToSchematic == null) {
      bnbtnCompileToSchematic = new BnButton();
      bnbtnCompileToSchematic
          .setIcon(new ImageIcon(MplFrame.class.getResource("/icons/schematic_16.png")));
      bnbtnCompileToSchematic.setPath(new Path("this.compileToSchematic"));
      bnbtnCompileToSchematic.setModelProvider(getLocalModelProvider());
    }
    return bnbtnCompileToSchematic;
  }

  private BnButton getBnbtnCompileToFilter() {
    if (bnbtnCompileToFilter == null) {
      bnbtnCompileToFilter = new BnButton();
      bnbtnCompileToFilter
          .setIcon(new ImageIcon(MplFrame.class.getResource("/icons/mcedit_16.png")));
      bnbtnCompileToFilter.setPath(new Path("this.compileToFilter"));
      bnbtnCompileToFilter.setModelProvider(getLocalModelProvider());
    }
    return bnbtnCompileToFilter;
  }

  private JTabbedPane getTabbedPane() {
    if (tabbedPane == null) {
      tabbedPane = new JTabbedPane(JTabbedPane.TOP);
      tabbedPane.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          MplFramePM presentationModel = getPresentationModel();
          if (presentationModel == null) {
            return;
          }
          ListPM<MplEditorPM> editors = presentationModel.editors;
          int selectedIndex = tabbedPane.getSelectedIndex();
          editors.getSelection().setInterval(selectedIndex, selectedIndex);
        }
      });
    }
    return tabbedPane;
  }
}
