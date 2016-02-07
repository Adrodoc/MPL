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
package de.adrodoc55.minecraft.mpl.gui.bntextpane;

import javax.swing.JTextPane;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.model.ITextPM;


/**
 * The <code>BnTextPane</code> is a {@link JTextPane} that can subscribe to an {@link ITextPM}.
 *
 * @author Adrodoc55
 * @beaninfo
 */
public class BnTextPane extends TextPMTextPane implements ModelSubscriber {
  private static final long serialVersionUID = 1L;

  private final Link link = new Link(this);

  /**
   * Constructs a <code>BnTextPane</code>.
   */
  public BnTextPane() {}

  /**
   * Constructs a <code>BnTextPane</code> and binds it to the specified model.
   *
   * @param pModel the model
   */
  public BnTextPane(ITextPM pModel) {
    super(pModel);
  }

  /**
   * Constructs a <code>BnTextPane</code> and subscribes it for the model at the specified Path
   * provided by the given provider.
   *
   * @param provider
   * @param path
   */
  public BnTextPane(ModelProvider provider, Path path) {
    this.setModelProvider(provider);
    this.setPath(path);
  }

  /**
   * Constructs a <code>BnTextPane</code> and subscribes it for the model at the root level provided
   * by the given provider.
   *
   * @param provider
   */
  public BnTextPane(ModelProvider provider) {
    this.setModelProvider(provider);
    this.setPath(new Path());
  }

  /** {@inheritDoc} */
  public IModelProvider getModelProvider() {
    return link.getModelProvider();
  }

  /** {@inheritDoc} */
  public void setModelProvider(IModelProvider provider) {
    this.link.setModelProvider(provider);
  }

  /** {@inheritDoc} */
  public Path getPath() {
    return link.getPath();
  }

  /** {@inheritDoc} */
  public void setPath(Path path) {
    this.link.setPath(path);
  }
}
