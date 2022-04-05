package com.g3g4x5x6.panels.ssh.editor;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchDialog extends JDialog implements ActionListener {
    private JToolBar toolBar;
    private JTextField searchField;
    private JToggleButton regexCB;
    private JToggleButton matchCaseCB;
    private JToggleButton wholeWord;
    private RSyntaxTextArea textArea;

    public SearchDialog(RSyntaxTextArea textArea) {
        this.setTitle("查找替换");
        this.setLayout(new BorderLayout());
        this.textArea = textArea;

        // Create a toolbar with searching options.
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        searchField = new JTextField(25);
        toolBar.add(searchField);
        // nextOccurence.svg
        final JButton nextButton = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/nextOccurence.svg"));
        nextButton.setToolTipText("下一个");
        nextButton.setActionCommand("FindNext");
        nextButton.addActionListener(this);
        toolBar.add(nextButton);
        // TODO 知识点
        searchField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nextButton.doClick(0);
            }
        });
        // previousOccurence.svg
        JButton prevButton = new JButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/previousOccurence.svg"));
        prevButton.setToolTipText("上一个");
        prevButton.setActionCommand("FindPrev");
        prevButton.addActionListener(this);
        toolBar.add(prevButton);
        // regex.svg  regexSelected.svg
        regexCB = new JToggleButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/regex.svg"));
        regexCB.setToolTipText("正则匹配");
        regexCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (regexCB.isSelected()) {
                    regexCB.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/regexSelected.svg"));
                } else {
                    regexCB.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/regex.svg"));
                }
            }
        });
        toolBar.add(regexCB);
        // matchCase.svg matchCaseSelected.svg
        matchCaseCB = new JToggleButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/matchCase.svg"));
        matchCaseCB.setToolTipText("区分大小写");
        matchCaseCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (matchCaseCB.isSelected()) {
                    matchCaseCB.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/matchCaseSelected.svg"));
                } else {
                    matchCaseCB.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/matchCase.svg"));
                }
            }
        });
        toolBar.add(matchCaseCB);
        // words.svg  wordsSelected.svg WholeWord
        wholeWord = new JToggleButton(new FlatSVGIcon("com/g3g4x5x6/ui/icons/words.svg"));
        wholeWord.setToolTipText("整词匹配）");
        wholeWord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (wholeWord.isSelected()) {
                    wholeWord.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/wordsSelected.svg"));
                } else {
                    wholeWord.setIcon(new FlatSVGIcon("com/g3g4x5x6/ui/icons/words.svg"));
                }
            }
        });
        toolBar.add(wholeWord);

        this.add(toolBar, BorderLayout.NORTH);
        this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // "FindNext" => search forward, "FindPrev" => search backward
        String command = e.getActionCommand();
        boolean forward = "FindNext".equals(command);

        // Create an object defining our search parameters.
        SearchContext context = new SearchContext();
        String text = searchField.getText();
        if (text.length() == 0) {
            return;
        }
        context.setSearchFor(text);
        context.setMatchCase(matchCaseCB.isSelected());
        context.setRegularExpression(regexCB.isSelected());
        context.setWholeWord(wholeWord.isSelected());
        context.setSearchForward(forward);
        boolean found = SearchEngine.find(textArea, context).wasFound();
        if (!found) {
            JOptionPane.showMessageDialog(this, "未找到，换个方向试试吧");
        }
    }
}
