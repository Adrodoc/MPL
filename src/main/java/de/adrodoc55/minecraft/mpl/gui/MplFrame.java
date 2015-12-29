package de.adrodoc55.minecraft.mpl.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
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
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.adrodoc55.commons.FileUtils;
import de.adrodoc55.minecraft.mpl.Main;

public class MplFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JFileChooser chooser;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem saveUnderMenuItem;
    private JMenuItem compileMenuItem;
    private JToolBar toolBar;
    private JButton newButton;
    private JButton openButton;
    private JButton saveButton;
    private JButton compileButton;
    private JTabbedPane tabbedPane;

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

    private JFileChooser getFileChooser() {
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
        JFileChooser chooser = getFileChooser();
        int userAction = chooser.showOpenDialog(this);
        if (userAction != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (file.exists()) {
            try {
                MplEditor editor = new MplEditor(file);
                getTabbedPane().addTab(file.getName(), editor);
                getTabbedPane().setSelectedComponent(editor);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        chooser,
                        "An Exception occured while trying to open '"
                                + file.getPath() + "'. Exception: "
                                + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                openFile();
            }
        } else {
            JOptionPane.showMessageDialog(chooser,
                    "The File '" + file.getPath() + "' couldn't be found!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            openFile();
        }

    }

    private void saveFile() {
        Component selected = getTabbedPane().getSelectedComponent();
        if (selected == null || !(selected instanceof MplEditor)) {
            return;
        }
        MplEditor editor = (MplEditor) selected;
        try {
            boolean saved = editor.save();
            if (!saved) {
                saveFileUnder();
            }
        } catch (IOException ex) {
            File file = editor.getFile();
            String path = file != null ? file.getPath() : null;
            JOptionPane.showMessageDialog(chooser,
                    "An Exception occured while trying to open '" + path
                            + "'. Exception: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFileUnder() {
        Component selected = getTabbedPane().getSelectedComponent();
        if (selected == null || !(selected instanceof MplEditor)) {
            return;
        }
        JFileChooser chooser = getFileChooser();
        int userAction = chooser.showSaveDialog(this);
        if (userAction != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (file.exists()) {
            int overwrite = JOptionPane.showOptionDialog(chooser, "The File '"
                    + file.getName()
                    + "' already exists and will be overwritten.", "Save...",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, null, null);
            if (overwrite != JOptionPane.OK_OPTION) {
                saveFileUnder();
                return;
            }
        }
        try {
            MplEditor editor = (MplEditor) selected;
            editor.setFile(file);
            editor.save();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    chooser,
                    "An Exception occured while trying to open '"
                            + file.getPath() + "'. Exception: "
                            + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void compileFile() {
        Component selected = getTabbedPane().getSelectedComponent();
        if (selected == null || !(selected instanceof MplEditor)) {
            return; // TODO: Informiere User, dass er speichern muss
        }
        MplEditor editor = (MplEditor) selected;
        File file = editor.getFile();
        try {
            String targetFileName = FileUtils.getFilenameWithoutExtension(file)
                    + ".py";
            Main.main(file, new File(
                    "C:/Users/Adrian/Documents/MCEdit/Filters", targetFileName));
        } catch (IOException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    public MplFrame() {
        super("Minecraft Programming Language");
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                MplFrame.class.getResource("/icons/commandblock_icon.png")));
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
            fileMenu.add(getNewMenuItem());
            fileMenu.add(getOpenMenuItem());
            fileMenu.add(getSaveMenuItem());
            fileMenu.add(getSaveUnderMenuItem());
            fileMenu.add(getCompileMenuItem());
        }
        return fileMenu;
    }

    private JMenuItem getNewMenuItem() {
        if (newMenuItem == null) {
            newMenuItem = new JMenuItem("New");
            newMenuItem.addActionListener(e -> {
                newFile();
            });
        }
        return newMenuItem;
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

    private JMenuItem getSaveMenuItem() {
        if (saveMenuItem == null) {
            saveMenuItem = new JMenuItem("Save");
            saveMenuItem.addActionListener(e -> {
                saveFile();
            });
        }
        return saveMenuItem;
    }

    private JMenuItem getSaveUnderMenuItem() {
        if (saveUnderMenuItem == null) {
            saveUnderMenuItem = new JMenuItem("Save under");
            saveUnderMenuItem.addActionListener(e -> {
                saveFileUnder();
            });
        }
        return saveUnderMenuItem;
    }

    private JMenuItem getCompileMenuItem() {
        if (compileMenuItem == null) {
            compileMenuItem = new JMenuItem("Compile");
            compileMenuItem.addActionListener(e -> {
                compileFile();
            });
        }
        return compileMenuItem;
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
            newButton.setIcon(new ImageIcon(MplFrame.class
                    .getResource("/icons/new_file_icon_16.png")));
            newButton.addActionListener(e -> {
                newFile();
            });
        }
        return newButton;
    }

    private JButton getOpenButton() {
        if (openButton == null) {
            openButton = new JButton();
            openButton.setIcon(new ImageIcon(MplFrame.class
                    .getResource("/icons/folder_icon_16.png")));
            openButton.addActionListener(e -> {
                openFile();
            });
        }
        return openButton;
    }

    private JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = new JButton();
            saveButton.setIcon(new ImageIcon(MplFrame.class
                    .getResource("/icons/disk_icon_16.png")));
            saveButton.addActionListener(e -> {
                saveFile();
            });
        }
        return saveButton;
    }

    private JButton getCompileButton() {
        if (compileButton == null) {
            compileButton = new JButton();
            compileButton.setIcon(new ImageIcon(MplFrame.class
                    .getResource("/icons/gear_run_16.png")));
            compileButton.addActionListener(e -> {
                compileFile();
            });
        }
        return compileButton;
    }

    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new CloseableTabbedPane();
            tabbedPane.getInputMap().put(
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit
                            .getDefaultToolkit().getMenuShortcutKeyMask()),
                    "save");
            tabbedPane.getActionMap().put("save", new AbstractAction() {
                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    saveFile();
                }
            });
        }
        return tabbedPane;
    }
}
