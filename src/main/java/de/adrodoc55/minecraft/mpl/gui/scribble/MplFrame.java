package de.adrodoc55.minecraft.mpl.gui.scribble;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import org.beanfabrics.model.ListPM;
import org.beanfabrics.swing.BnButton;

/**
 * The MplFrame is a {@link View} on a {@link MplFramePM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class MplFrame extends JFrame implements View<MplFramePM>,
        ModelSubscriber {

    private static final long serialVersionUID = 1L;

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        MplFrame frame = new MplFrame();
        MplFramePM pModel = new MplFramePM();
        frame.setPresentationModel(pModel);
        frame.setVisible(true);
    }

    private final Link link = new Link(this);
    private ModelProvider localModelProvider;
    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenuItem mntmNew;
    private JMenuItem mntmOpen;
    private JMenuItem mntmSave;
    private JMenuItem mntmSaveUnder;
    private JMenuItem mntmCompile;
    private JMenuItem mntmCompileUnder;
    private JToolBar toolBar;
    private BnButton btnNew;
    private BnButton btnOpen;
    private BnButton btnSave;
    private BnButton btnSaveUnder;
    private BnButton btnCompile;
    private BnButton btnCompileUnder;
    private JTabbedPane tabbedPane;

    /**
     * Constructs a new <code>MplFrame</code>.
     */
    public MplFrame() {
        super();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getToolBar(), BorderLayout.NORTH);
        getContentPane().add(getTabbedPane(), BorderLayout.CENTER);
        setJMenuBar(getMenuBar_1());
        //
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
            localModelProvider.setPresentationModelType(MplFramePM.class);
        }
        return localModelProvider;
    }

    /** {@inheritDoc} */
    public MplFramePM getPresentationModel() {
        return getLocalModelProvider().getPresentationModel();
    }

    /** {@inheritDoc} */
    public void setPresentationModel(MplFramePM pModel) {
        pModel.editors.addListListener(new ListListener() {
            @Override
            public void elementsSelected(ElementsSelectedEvent evt) {
                tabbedPane.setSelectedIndex(evt.getBeginIndex());
            }

            @Override
            public void elementsReplaced(ElementsReplacedEvent evt) {
                int beginIndex = evt.getBeginIndex();
                int length = evt.getLength();
                for (int i = 0; i < length; i++) {
                    tabbedPane.remove(i + beginIndex);
                }
                @SuppressWarnings("unchecked")
                IListPM<MplEditorPM> list = (IListPM<MplEditorPM>) evt
                        .getSource();
                for (int i = 0; i < length; i++) {
                    MplEditorPM editorPm = list.getAt(i);
                    tabbedPane.insertTab(editorPm.getTitle(), null,
                            editorPm.getView(), null, i);
                }
            }

            @Override
            public void elementsRemoved(ElementsRemovedEvent evt) {
                int beginIndex = evt.getBeginIndex();
                for (int i = 0; i < evt.getLength(); i++) {
                    tabbedPane.remove(i + beginIndex);
                }
            }

            @Override
            public void elementsDeselected(ElementsDeselectedEvent evt) {
            }

            @Override
            public void elementsAdded(ElementsAddedEvent evt) {
                int length = evt.getLength();
                @SuppressWarnings("unchecked")
                IListPM<MplEditorPM> list = (IListPM<MplEditorPM>) evt
                        .getSource();
                for (int i = 0; i < length; i++) {
                    MplEditorPM editorPm = list.getAt(i);
                    tabbedPane.insertTab(editorPm.getTitle(), null,
                            editorPm.getView(), null, i);
                }
            }

            @Override
            public void elementChanged(ElementChangedEvent evt) {
            }
        });
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

    private JMenuBar getMenuBar_1() {
        if (menuBar == null) {
            menuBar = new JMenuBar();
            menuBar.add(getMnFile());
        }
        return menuBar;
    }

    private JMenu getMnFile() {
        if (mnFile == null) {
            mnFile = new JMenu("File");
            mnFile.add(getMntmNew());
            mnFile.add(getMntmOpen());
            mnFile.add(getMntmSave());
            mnFile.add(getMntmSaveUnder());
            mnFile.add(getMntmCompile());
            mnFile.add(getMntmCompileUnder());
        }
        return mnFile;
    }

    private JMenuItem getMntmNew() {
        if (mntmNew == null) {
            mntmNew = new JMenuItem("New");
        }
        return mntmNew;
    }

    private JMenuItem getMntmOpen() {
        if (mntmOpen == null) {
            mntmOpen = new JMenuItem("Open");
        }
        return mntmOpen;
    }

    private JMenuItem getMntmSave() {
        if (mntmSave == null) {
            mntmSave = new JMenuItem("Save");
        }
        return mntmSave;
    }

    private JMenuItem getMntmSaveUnder() {
        if (mntmSaveUnder == null) {
            mntmSaveUnder = new JMenuItem("Save under");
        }
        return mntmSaveUnder;
    }

    private JMenuItem getMntmCompile() {
        if (mntmCompile == null) {
            mntmCompile = new JMenuItem("Compile");
        }
        return mntmCompile;
    }

    private JMenuItem getMntmCompileUnder() {
        if (mntmCompileUnder == null) {
            mntmCompileUnder = new JMenuItem("Compile under");
        }
        return mntmCompileUnder;
    }

    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.add(getBtnNew());
            toolBar.add(getBtnOpen());
            toolBar.add(getBtnSave());
            toolBar.add(getBtnSaveUnder());
            toolBar.add(getBtnCompile());
            toolBar.add(getBtnCompileUnder());
        }
        return toolBar;
    }

    private BnButton getBtnNew() {
        if (btnNew == null) {
            btnNew = new BnButton();
            btnNew.setModelProvider(getLocalModelProvider());
            btnNew.setPath(new Path("this.newFile"));
            btnNew.setText("new");
        }
        return btnNew;
    }

    private BnButton getBtnOpen() {
        if (btnOpen == null) {
            btnOpen = new BnButton();
            btnOpen.setText("open");
        }
        return btnOpen;
    }

    private BnButton getBtnSave() {
        if (btnSave == null) {
            btnSave = new BnButton();
            btnSave.setText("save");
        }
        return btnSave;
    }

    private BnButton getBtnSaveUnder() {
        if (btnSaveUnder == null) {
            btnSaveUnder = new BnButton();
            btnSaveUnder.setText("save under");
        }
        return btnSaveUnder;
    }

    private BnButton getBtnCompile() {
        if (btnCompile == null) {
            btnCompile = new BnButton();
            btnCompile.setText("compile");
        }
        return btnCompile;
    }

    private BnButton getBtnCompileUnder() {
        if (btnCompileUnder == null) {
            btnCompileUnder = new BnButton();
            btnCompileUnder.setText("compile under");
        }
        return btnCompileUnder;
    }

    private JTabbedPane getTabbedPane() {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane(JTabbedPane.TOP);
            tabbedPane.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    MplFramePM presentationModel = getPresentationModel();
                    if (presentationModel == null) {
                        return;
                    }
                    ListPM<MplEditorPM> editors = presentationModel.editors;
                    if(editors.size() == tabbedPane.getComponentCount()) {
                        return;
                    }
                    editors.clear();
                    int count = tabbedPane.getComponentCount();
                    ArrayList<MplEditorPM> components = new ArrayList<MplEditorPM>(
                            count);
                    for (int i = 0; i < count; i++) {
                        Component component = tabbedPane.getComponent(i);
                        MplEditor editor = (MplEditor) component;
                        components.add(editor.getPresentationModel());
                    }
                    editors.addAll(components);
                    int selectedIndex = tabbedPane.getSelectedIndex();
                    editors.getSelection().setInterval(selectedIndex,
                            selectedIndex);
                }
            });
        }
        return tabbedPane;
    }

}
