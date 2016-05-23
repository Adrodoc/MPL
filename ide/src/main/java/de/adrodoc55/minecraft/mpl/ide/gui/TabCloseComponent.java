/*
 * MPL (Minecraft Programming Language): A language for easy development of commandblock
 * applications including and IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL (Minecraft Programming Language).
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
 * MPL (Minecraft Programming Language): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, beinhaltet eine IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL (Minecraft Programming Language).
 *
 * MPL ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie
 * von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.beanfabrics.BnModelObserver;
import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnLabel;

/**
 * The TabCloseComponent is a {@link View} on a {@link MplEditorPM}.
 *
 * @author Adrodoc55
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
@SuppressWarnings("serial")
public class TabCloseComponent extends JComponent implements View<MplEditorPM>, ModelSubscriber {
  private final Link link = new Link(this);
  private ModelProvider localModelProvider;
  private JLabel savedLabel;
  private BnButton closeButton;
  private BnLabel titleLabel;
  private BnModelObserver bnModelObserver;

  /**
   * Constructs a new <code>TabCloseComponent</code>.
   */
  public TabCloseComponent() {
    super();
    init();
    getBnModelObserver().addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        MplEditorPM pModel = getPresentationModel();
        if (pModel == null || pModel.unsavedChanges.getBoolean()) {
          getSavedLabel()
              .setIcon(new ImageIcon(TabCloseComponent.class.getResource("/icons/unsaved.gif")));
        } else {
          getSavedLabel()
              .setIcon(new ImageIcon(TabCloseComponent.class.getResource("/icons/saved.gif")));
        }
      }
    });
  }

  private void init() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0, 0};
    gridBagLayout.rowHeights = new int[] {0};
    gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0};
    gridBagLayout.rowWeights = new double[] {0.0};
    setLayout(gridBagLayout);
    GridBagConstraints gbc_savedLabel = new GridBagConstraints();
    gbc_savedLabel.insets = new Insets(0, 0, 0, 5);
    gbc_savedLabel.gridx = 0;
    gbc_savedLabel.gridy = 0;
    add(getSavedLabel(), gbc_savedLabel);
    GridBagConstraints gbc_titleLabel = new GridBagConstraints();
    gbc_titleLabel.insets = new Insets(0, 0, 0, 5);
    gbc_titleLabel.gridx = 1;
    gbc_titleLabel.gridy = 0;
    add(getTitleLabel(), gbc_titleLabel);
    GridBagConstraints gbc_closeButton = new GridBagConstraints();
    gbc_closeButton.gridx = 2;
    gbc_closeButton.gridy = 0;
    add(getCloseButton(), gbc_closeButton);
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
      localModelProvider.setPresentationModelType(MplEditorPM.class);
    }
    return localModelProvider;
  }

  /** {@inheritDoc} */
  public MplEditorPM getPresentationModel() {
    return getLocalModelProvider().getPresentationModel();
  }

  /** {@inheritDoc} */
  public void setPresentationModel(MplEditorPM pModel) {
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

  private JLabel getSavedLabel() {
    if (savedLabel == null) {
      savedLabel = new JLabel();
    }
    return savedLabel;
  }

  private BnButton getCloseButton() {
    if (closeButton == null) {
      closeButton = new BnButton();
      closeButton.setPath(new Path("this.close"));
      closeButton.setModelProvider(getLocalModelProvider());
      closeButton.setIcon(new ImageIcon(TabCloseComponent.class.getResource("/icons/close.jpg")));
      closeButton.setBorderPainted(false);
      closeButton.setContentAreaFilled(false);
      closeButton.setPreferredSize(new Dimension(16, 16));
    }
    return closeButton;
  }

  private BnLabel getTitleLabel() {
    if (titleLabel == null) {
      titleLabel = new BnLabel();
      titleLabel.setPath(new Path("title"));
      titleLabel.setModelProvider(getLocalModelProvider());
    }
    return titleLabel;
  }

  /**
   * @wbp.nonvisual location=9,469
   */
  private BnModelObserver getBnModelObserver() {
    if (bnModelObserver == null) {
      bnModelObserver = new BnModelObserver();
      bnModelObserver.setPath(new Path("this.unsavedChanges"));
      bnModelObserver.setModelProvider(getLocalModelProvider());
    }
    return bnModelObserver;
  }
}
