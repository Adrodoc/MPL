package de.adrodoc55.minecraft.mpl.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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
    private JFileChooser chooser;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem openMenuItem;
    private JToolBar toolBar;
    private JButton newButton;
    private JButton openButton;
    private JButton saveButton;
    private JButton compileButton;
    private JTabbedPane tabbedPane;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        MplFrame frame = new MplFrame();
        frame.setVisible(true);
    }

    private JFileChooser getOpenFileChooser() {
        if (chooser == null) {
            chooser = new JFileChooser();
        }
        return chooser;
    }

    private void newFile() {
        MplEditor editor = new MplEditor();
        getTabbedPane().addTab("new.txt", editor);
        getTabbedPane().setSelectedComponent(editor);
    }

    private void openFile() {
        JFileChooser chooser = getOpenFileChooser();
        int userAction = chooser.showOpenDialog(MplFrame.this);
        if (userAction == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file.exists()) {
                try {
                    MplEditor editor = new MplEditor(file);
                    getTabbedPane().addTab(file.getName(), editor);
                    getTabbedPane().setSelectedComponent(editor);
                    // byte[] bytes;
                    // bytes = Files.readAllBytes(file.toPath());
                    // String fileContent = new String(bytes);
                    //
                    // JTextArea textArea = new JTextArea();
                    // textArea.setText(fileContent);
                    // getTabbedPane().addTab(file.getName(), textArea);
                    // getTabbedPane().setSelectedComponent(textArea);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(chooser, "An Exception occured while trying to read '"
                            + file.getPath() + "'. Exception: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(chooser, "The File '" + file.getPath() + "' couldn't be found!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public MplFrame() {
        super("Minecraft Programming Language");
        setIconImage(Toolkit.getDefaultToolkit().getImage(MplFrame.class.getResource("/icons/commandblock_icon.png")));
        init();
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
                openFile();
            });
        }
        return openMenuItem;
    }

    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(getNewButton());
            toolBar.add(getOpenButton());
            toolBar.add(getSaveButton());
            toolBar.add(getCompileButton());
        }
        return toolBar;
    }

    private JButton getNewButton() {
        if (newButton == null) {
            newButton = new JButton();
            newButton.setIcon(new ImageIcon(MplFrame.class.getResource("/icons/new_file_icon_16.png")));
            newButton.addActionListener(e -> {
                newFile();
            });
        }
        return newButton;
    }

    private JButton getOpenButton() {
        if (openButton == null) {
            openButton = new JButton();
            openButton.setIcon(new ImageIcon(MplFrame.class.getResource("/icons/folder_icon_16.png")));
            openButton.addActionListener(e -> {
                openFile();
            });
        }
        return openButton;
    }

    private JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = new JButton();
            saveButton.setIcon(new ImageIcon(MplFrame.class.getResource("/icons/disk_icon_16.png")));
        }
        return saveButton;
    }

    private JButton getCompileButton() {
        if (compileButton == null) {
            compileButton = new JButton();
            compileButton.setIcon(new ImageIcon(MplFrame.class.getResource("/icons/gear_run_16.png")));
        }
        return compileButton;
    }

    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new CloseableTabbedPane();
        }
        return tabbedPane;
    }

}
