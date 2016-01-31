package de.adrodoc55.minecraft.mpl.gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
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
import org.beanfabrics.swing.BnMenuItem;

/**
 * The MplFrame is a {@link View} on a {@link MplFramePM}.
 *
 * @created by the Beanfabrics Component Wizard, www.beanfabrics.org
 */
public class MplFrame extends JFrame implements View<MplFramePM>, ModelSubscriber {

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
  private BnMenuItem mntmNew;
  private BnMenuItem mntmOpen;
  private BnMenuItem mntmSave;
  private BnMenuItem mntmSaveUnder;
  private BnMenuItem mntmCompile;
  private BnMenuItem mntmCompileUnder;
  private JToolBar toolBar;
  private BnButton btnNew;
  private BnButton btnOpen;
  private BnButton btnSave;
  private BnButton btnCompile;
  private JTabbedPane tabbedPane;

  /**
   * Constructs a new <code>MplFrame</code>.
   */
  public MplFrame() {
    super("Minecraft Programming Language");
    setIconImage(Toolkit.getDefaultToolkit()
        .getImage(MplFrame.class.getResource("/icons/commandblock_icon.png")));
    init();
    setSize(1000, 500);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        MplFramePM pModel = getPresentationModel();
        if(pModel==null) {
          System.exit(0);
        }
        pModel.terminate();
      }
    });
  }

  private void init() {
    setJMenuBar(getMenuBar_1());
    getContentPane().add(getToolBar(), BorderLayout.NORTH);
    getContentPane().add(getTabbedPane(), BorderLayout.CENTER);
  }

  /**
   * Returns the local {@link ModelProvider} for this class.
   *
   * @return the local <code>ModelProvider</code>
   * @wbp.nonvisual location=20,550
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
    ListListener l = new ListListener() {
      @Override
      public void elementsSelected(ElementsSelectedEvent evt) {
        tabbedPane.setSelectedIndex(evt.getBeginIndex());
      }

      @Override
      public void elementsReplaced(ElementsReplacedEvent evt) {
        int beginIndex = evt.getBeginIndex();
        int length = evt.getLength();
        this.remove(beginIndex, length);
        @SuppressWarnings("unchecked")
        IListPM<MplEditorPM> list = (IListPM<MplEditorPM>) evt.getSource();
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
        IListPM<MplEditorPM> list = (IListPM<MplEditorPM>) evt.getSource();
        this.add(list, beginIndex, length);
      }

      @Override
      public void elementChanged(ElementChangedEvent evt) {}

      private void remove(int beginIndex, int length) {
        for (int i = 0; i < length; i++) {
          int index = beginIndex + i;
          tabbedPane.remove(index);
        }
      }

      private void add(IListPM<MplEditorPM> list, int beginIndex, int length) {
        for (int i = 0; i < length; i++) {
          int index = beginIndex + i;
          MplEditorPM editorPm = list.getAt(index);
          this.addMplEditor(index, editorPm);
        }
      }

      private void addMplEditor(int i, MplEditorPM editorPm) {
        MplEditor editor = new MplEditor();
        editor.setPresentationModel(editorPm);
        tabbedPane.insertTab(editorPm.getTitle(), null, editor, null, i);
        TabCloseComponent tabComponent = new TabCloseComponent();
        tabComponent.setPresentationModel(editorPm);
        tabbedPane.setTabComponentAt(i, tabComponent);
      }
    };
    pModel.editors.addListListener(l);
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

  private BnMenuItem getMntmNew() {
    if (mntmNew == null) {
      mntmNew = new BnMenuItem();
      mntmNew.setPath(new Path("this.newFile"));
      mntmNew.setModelProvider(getLocalModelProvider());
      mntmNew.setText("New");
    }
    return mntmNew;
  }

  private BnMenuItem getMntmOpen() {
    if (mntmOpen == null) {
      mntmOpen = new BnMenuItem();
      mntmOpen.setPath(new Path("this.openFile"));
      mntmOpen.setModelProvider(getLocalModelProvider());
      mntmOpen.setText("Open");
    }
    return mntmOpen;
  }

  private BnMenuItem getMntmSave() {
    if (mntmSave == null) {
      mntmSave = new BnMenuItem();
      mntmSave.setPath(new Path("this.saveFile"));
      mntmSave.setModelProvider(getLocalModelProvider());
      mntmSave.setText("Save");
    }
    return mntmSave;
  }

  private BnMenuItem getMntmSaveUnder() {
    if (mntmSaveUnder == null) {
      mntmSaveUnder = new BnMenuItem();
      mntmSaveUnder.setPath(new Path("this.saveFileUnder"));
      mntmSaveUnder.setModelProvider(getLocalModelProvider());
      mntmSaveUnder.setText("Save under");
    }
    return mntmSaveUnder;
  }

  private BnMenuItem getMntmCompile() {
    if (mntmCompile == null) {
      mntmCompile = new BnMenuItem();
      mntmCompile.setPath(new Path("this.compileFile"));
      mntmCompile.setModelProvider(getLocalModelProvider());
      mntmCompile.setText("Compile");
    }
    return mntmCompile;
  }

  private BnMenuItem getMntmCompileUnder() {
    if (mntmCompileUnder == null) {
      mntmCompileUnder = new BnMenuItem();
      mntmCompileUnder.setPath(new Path("this.compileFileUnder"));
      mntmCompileUnder.setModelProvider(getLocalModelProvider());
      mntmCompileUnder.setText("Compile under");
    }
    return mntmCompileUnder;
  }

  private JToolBar getToolBar() {
    if (toolBar == null) {
      toolBar = new JToolBar();
      toolBar.add(getBtnNew());
      toolBar.add(getBtnOpen());
      toolBar.add(getBtnSave());
      toolBar.add(getBtnCompile());
    }
    return toolBar;
  }

  private BnButton getBtnNew() {
    if (btnNew == null) {
      btnNew = new BnButton();
      btnNew.setIcon(new ImageIcon(MplFrame.class.getResource("/icons/new_file_icon_16.png")));
      btnNew.setModelProvider(getLocalModelProvider());
      btnNew.setPath(new Path("this.newFile"));
    }
    return btnNew;
  }

  private BnButton getBtnOpen() {
    if (btnOpen == null) {
      btnOpen = new BnButton();
      btnOpen.setIcon(new ImageIcon(MplFrame.class.getResource("/icons/folder_icon_16.png")));
      btnOpen.setPath(new Path("this.openFile"));
      btnOpen.setModelProvider(getLocalModelProvider());
    }
    return btnOpen;
  }

  private BnButton getBtnSave() {
    if (btnSave == null) {
      btnSave = new BnButton();
      btnSave.setIcon(new ImageIcon(MplFrame.class.getResource("/icons/disk_icon_16.png")));
      btnSave.setPath(new Path("this.saveFile"));
      btnSave.setModelProvider(getLocalModelProvider());
    }
    return btnSave;
  }

  private BnButton getBtnCompile() {
    if (btnCompile == null) {
      btnCompile = new BnButton();
      btnCompile.setIcon(new ImageIcon(MplFrame.class.getResource("/icons/gear_run_16.png")));
      btnCompile.setPath(new Path("this.compileFile"));
      btnCompile.setModelProvider(getLocalModelProvider());
    }
    return btnCompile;
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
          int selectedIndex = tabbedPane.getSelectedIndex();
          editors.getSelection().setInterval(selectedIndex, selectedIndex);
        }
      });
    }
    return tabbedPane;
  }

}
