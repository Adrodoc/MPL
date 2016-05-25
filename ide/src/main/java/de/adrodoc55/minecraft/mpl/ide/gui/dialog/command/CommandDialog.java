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
package de.adrodoc55.minecraft.mpl.ide.gui.dialog.command;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

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

/**
 * The OneCommandDialog is a {@link View} on a {@link CommandDialogPM}.
 *
 * @author Adrodoc55
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class CommandDialog extends JDialog implements View<CommandDialogPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private JScrollPane scrollPane;
  private JPanel commandPanel;

  public CommandDialog() {
    this(null);
  }

  /**
   * Constructs a new <code>OneCommandDialog</code>.
   *
   * @param parent the {@code Window} from which the dialog is displayed or {@code null} if this
   *        dialog has no parent
   */
  public CommandDialog(Window parent) {
    super(parent, "Import Commands");
    init();
    setModal(true);
    setResizable(false);
    setSize(500, 500);
    setLocationRelativeTo(getParent());
  }

  private void init() {
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(getScrollPane(), BorderLayout.CENTER);
    InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = getRootPane().getActionMap();
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    actionMap.put("close", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
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
      localModelProvider.setPresentationModelType(CommandDialogPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public CommandDialogPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(CommandDialogPM pModel) {
    ListListener l = new ListListener() {
      @Override
      public void elementsSelected(ElementsSelectedEvent evt) {}

      @Override
      public void elementsReplaced(ElementsReplacedEvent evt) {
        int beginIndex = evt.getBeginIndex();
        int length = evt.getLength();
        this.remove(beginIndex, length);
        @SuppressWarnings("unchecked")
        IListPM<CommandPM> list = (IListPM<CommandPM>) evt.getSource();
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
        IListPM<CommandPM> list = (IListPM<CommandPM>) evt.getSource();
        this.add(list, beginIndex, length);
      }

      @Override
      public void elementChanged(ElementChangedEvent evt) {}

      private void remove(int beginIndex, int length) {
        for (int i = 0; i < length; i++) {
          int index = beginIndex + i;
          getCommandPanel().remove(index);
        }
        revalidate();
        repaint();
        setDefaultButton();
      }

      private void add(IListPM<CommandPM> list, int beginIndex, int length) {
        for (int i = 0; i < length; i++) {
          int index = beginIndex + i;
          CommandPM commandPm = list.getAt(index);
          this.addCommand(index, commandPm);
        }
        revalidate();
        repaint();
        setDefaultButton();
      }

      private void addCommand(int i, CommandPM commandPm) {
        CommandPanel panel = new CommandPanel();
        panel.setPresentationModel(commandPm);
        getCommandPanel().add(panel, i);
      }

      private void setDefaultButton() {
        try {
          CommandPanel first = (CommandPanel) getCommandPanel().getComponent(0);
          getRootPane().setDefaultButton(first.getBnbtnCopyAndClose());
        } catch (ArrayIndexOutOfBoundsException | ClassCastException ex) {
          // Do nothing
        }
      }

    };
    pModel.commands.addListListener(l);
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

  private JScrollPane getScrollPane() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
      scrollPane.setViewportView(getCommandPanel());
    }
    return scrollPane;
  }

  private JPanel getCommandPanel() {
    if (commandPanel == null) {
      commandPanel = new JPanel();
      commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.Y_AXIS));
    }
    return commandPanel;
  }

}
