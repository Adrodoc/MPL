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
package de.adrodoc55.minecraft.mpl.ide.gui.dialog.autocompletion;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.list.BnList;
import org.beanfabrics.swing.list.CellConfig;

/**
 * The AutoCompletionDialog is a {@link View} on a {@link AutoCompletionDialogPM}.
 *
 * @author Adrodoc55
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class AutoCompletionDialog extends JDialog
    implements View<AutoCompletionDialogPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private BnList bnList;
  private JScrollPane scrollPane;
  private JPanel panel;

  /**
   * Constructs a new <code>AutoCompletionDialog</code>.
   */
  public AutoCompletionDialog() {
    this(null);
  }

  /**
   * Constructs a new <code>AutoCompletionDialog</code>.
   *
   * @param parent the {@code Window} from which the dialog is displayed or {@code null} if this
   *        dialog has no parent
   */
  public AutoCompletionDialog(Window parent) {
    super(parent);
    init();
    setUndecorated(true);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  private void init() {
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(getPanel(), BorderLayout.CENTER);
    InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = getRootPane().getActionMap();

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    actionMap.put("close", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "auto complete");
    actionMap.put("auto complete", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        chooseSelection();
      }
    });
  }

  private JPanel getPanel() {
    if (panel == null) {
      panel = new JPanel();
      panel.setBackground(UIManager.getColor("window"));
      panel.setLayout(new BorderLayout(0, 0));
      panel.add(getScrollPane());
    }
    return panel;
  }

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getBnList());
    }
    return scrollPane;
  }

  public BnList getBnList() {
    if (bnList == null) {
      bnList = new BnList();
      bnList.setVisibleRowCount(5);
      bnList.setPath(new Path("this.options"));
      bnList.setCellConfig(new CellConfig(new Path("this.displayName")));
      bnList.setModelProvider(getLocalModelProvider());

      bnList.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent mouseEvent) {
          if (!bnList.equals(mouseEvent.getSource()))
            return;
          if (mouseEvent.getClickCount() != 2)
            return;
          chooseSelection();
        }
      });
    }
    return bnList;
  }

  private void chooseSelection() {
    AutoCompletionDialogPM pm = getPresentationModel();
    if (pm == null)
      return;
    pm.chooseSelection();
    dispose();
  }

  /**
   * Returns the local {@link ModelProvider} for this class.
   *
   * @return the local <code>ModelProvider</code>
   * @wbp.nonvisual location=10,430
   */
  protected ModelProvider getLocalModelProvider() {
    if (localModelProvider == null) {
      localModelProvider = new ModelProvider(); // @wb:location=10,430
      localModelProvider.setPresentationModelType(AutoCompletionDialogPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public AutoCompletionDialogPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(AutoCompletionDialogPM pModel) {
    getLocalModelProvider().setPresentationModel(pModel);
  }

  /** {@inheritDoc} */
  public IModelProvider getModelProvider() {
    return this.link.getModelProvider();
  }

  /** {@inheritDoc} */
  public void setModelProvider(IModelProvider modelProvider) {
    this.link.setModelProvider(modelProvider);
  }

  /** {@inheritDoc} */
  public Path getPath() {
    return this.link.getPath();
  }

  /** {@inheritDoc} */
  public void setPath(Path path) {
    this.link.setPath(path);
  }

}
