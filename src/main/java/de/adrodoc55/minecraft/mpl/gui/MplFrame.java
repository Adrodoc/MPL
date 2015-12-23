package de.adrodoc55.minecraft.mpl.gui;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MplFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JTabbedPane tabbedPane;
    private JMenu fileMenu;
    private JMenuItem openMenuItem;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        MplFrame frame = new MplFrame();
        frame.setVisible(true);
    }

    public MplFrame() {
        init();
        pack();
        setLocationRelativeTo(null);
    }

    private void init() {
        setJMenuBar(getMenuBar_1());
        getContentPane().add(getToolBar(), BorderLayout.NORTH);
        getContentPane().add(getTabbedPane(), BorderLayout.CENTER);
    }

    private JMenuBar getMenuBar_1() {
        if (menuBar == null) {
            menuBar = new JMenuBar();
            menuBar.add(getFileMenu());
        }
        return menuBar;
    }

    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu("File");
            fileMenu.add(getOpenMenuItem());
        }
        return fileMenu;
    }

    private JMenuItem getOpenMenuItem() {
        if (openMenuItem == null) {
            openMenuItem = new JMenuItem("Open");
            openMenuItem.addActionListener(e -> {
                JFileChooser chooser = getOpenFileChooser();
                int userAction = chooser.showOpenDialog(MplFrame.this);
                if (userAction == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    if (file.exists()) {
                        try {
                            byte[] bytes;
                            bytes = Files.readAllBytes(file.toPath());
                            String fileContent = new String(bytes);

                            TextArea textArea = new TextArea();
                            textArea.setText(fileContent);
                            getTabbedPane().addTab(file.getName(), textArea);
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(chooser,
                                    "An Exception occured while trying to read '"
                                            + file.getPath() + "'. Exception: "
                                            + ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(chooser, "The File '"
                                + file.getPath() + "' couldn't be found!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        return openMenuItem;
    }

    JFileChooser chooser;

    private JFileChooser getOpenFileChooser() {
        if (chooser == null) {
            chooser = new JFileChooser();
        }
        return chooser;
    }

    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
        }
        return toolBar;
    }

    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new CloseableTabbedPane();
        }
        return tabbedPane;
    }
}
