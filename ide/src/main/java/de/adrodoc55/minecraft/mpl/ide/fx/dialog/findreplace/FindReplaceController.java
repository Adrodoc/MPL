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
package de.adrodoc55.minecraft.mpl.ide.fx.dialog.findreplace;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Toolkit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.fx.text.ui.source.SourceViewer;
import org.eclipse.fx.ui.controls.styledtext.StyledTextArea;
import org.eclipse.fx.ui.controls.styledtext.TextSelection;
import org.eclipse.jface.text.IDocument;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

/**
 * @author Adrodoc55
 */
public class FindReplaceController {
  @FXML
  private Label findLabel;
  @FXML
  private Label replaceLabel;
  @FXML
  private ComboBox<String> findComboBox;
  @FXML
  private ComboBox<String> replaceComboBox;
  @FXML
  private CheckBox caseSensitive;
  @FXML
  private CheckBox wrapSearch;
  @FXML
  private CheckBox wholeWord;
  @FXML
  private CheckBox incremental;
  @FXML
  private CheckBox regularExpression;
  @FXML
  private CheckBox extended;

  @FXML
  private void initialize() {
    findLabel.setLabelFor(findComboBox);
    replaceLabel.setLabelFor(replaceComboBox);
  }

  private SourceViewer sourceViewer;

  public void setSourceViewer(SourceViewer sourceViewer) {
    this.sourceViewer = checkNotNull(sourceViewer, "sourceViewer == null!");
  }

  @FXML
  public void find() {
    IDocument document = sourceViewer.getDocument();
    StyledTextArea textWidget = sourceViewer.getTextWidget();

    String findString = findComboBox.getSelectionModel().getSelectedItem();
    if (!regularExpression.isSelected()) {
      findString = Pattern.quote(findString);
      // if(extended.isSelected()) {
      // findString=
      // }
      if (wholeWord.isSelected()) {
        findString = "\\b" + findString + "\\b";
      }
    }
    Pattern pattern;
    if (caseSensitive.isSelected()) {
      pattern = Pattern.compile(findString);
    } else {
      pattern = Pattern.compile(findString, Pattern.CASE_INSENSITIVE);
    }
    DocumentCharSequence text = new DocumentCharSequence(document);
    TextSelection initialSelection = textWidget.getSelection();
    int startIndex = initialSelection.offset + initialSelection.length;
    Matcher matcher = pattern.matcher(text.subSequence(startIndex, document.getLength()));
    if (matcher.find()) {
      int offset = startIndex + matcher.start();
      int length = startIndex + matcher.end() - offset;
      textWidget.setCaretOffset(offset);
      textWidget.setSelectionRange(offset, length);
    } else {
      Toolkit.getDefaultToolkit().beep();
    }
  }

  @FXML
  public void replace() {}

  @FXML
  public void replaceFind() {}

  @FXML
  public void replaceAll() {}
}
