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
package de.adrodoc55.minecraft.mpl.ide.fx.editor.marker;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.fx.text.ui.source.ILineRulerAnnotationPresenter;
import org.eclipse.jface.text.source.Annotation;

import com.google.common.collect.Ordering;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

/**
 * @author Adrodoc55
 */
public class MplLineRulerAnnotationPresenter implements ILineRulerAnnotationPresenter {
  private DoubleProperty width = new SimpleDoubleProperty(16);

  @Override
  public boolean isApplicable(Annotation annotation) {
    return annotation instanceof MplAnnotation;
  }

  @Override
  public DoubleProperty getWidth() {
    return width;
  }

  @Override
  public LayoutHint getLayoutHint() {
    return LayoutHint.ALIGN_CENTER;
  }

  @Override
  public int getOrder() {
    return 0;
  }

  @Override
  public Node createNode() {
    ImageView result = new ImageView();
    Tooltip tooltip = new Tooltip();
    Tooltip.install(result, tooltip);
    result.setUserData(tooltip);
    return result;
  }

  @Override
  public void updateNode(Node node, Set<Annotation> annotations) {
    updateImageView((ImageView) node,
        annotations.stream().map(a -> (MplAnnotation) a).collect(Collectors.toSet()));
  }

  private static final Comparator<MplAnnotation> PRIORITY_ORDERING =
      (a, b) -> Ordering.natural().compare(a.getPriority(), b.getPriority());

  private void updateImageView(ImageView node, Set<MplAnnotation> annotations) {
    Optional<MplAnnotation> first = annotations.stream().sorted(PRIORITY_ORDERING).findFirst();
    if (first.isPresent()) {
      MplAnnotation mplAnnotation = first.get();
      node.setImage(mplAnnotation.getAnnotationType().getImage());
      Tooltip tooltip = (Tooltip) node.getUserData();
      tooltip.setText(mplAnnotation.getText());
    }
  }
}
