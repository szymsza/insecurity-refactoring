/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.gui.insecurityrefactoring;

import ntnuhtwg.insecurityrefactoring.gui.temppattern.TempPatternFrame;

import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import ntnuhtwg.insecurityrefactoring.Framework;
import ntnuhtwg.insecurityrefactoring.base.GlobalSettings;
import ntnuhtwg.insecurityrefactoring.base.SourceLocation;
import ntnuhtwg.insecurityrefactoring.base.Util;
import ntnuhtwg.insecurityrefactoring.base.tree.DFATreeNode;
import ntnuhtwg.insecurityrefactoring.base.info.ACIDTree;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.SanitizePattern;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.SinkPattern;
import ntnuhtwg.insecurityrefactoring.base.info.DataflowPathInfo;
import ntnuhtwg.insecurityrefactoring.gui.DataflowSourceCode;
import ntnuhtwg.insecurityrefactoring.refactor.temppattern.ScanProgress;
import ntnuhtwg.insecurityrefactoring.refactor.temppattern.TempPattern;
import ntnuhtwg.insecurityrefactoring.base.tools.SourceCodeProvider;
import org.javatuples.Pair;

/**
 *
 * @author blubbomat
 */
public class PIPRenderer extends JPanel{
    
    private ACIDViewer acidViewer;
    private final String scanEverything = "Scan everything...";
    
    private JCheckBox skipPreScan = new JCheckBox("Skip pre scan", false);
    private JCheckBox loadPipsFromCache = new JCheckBox(" Load PIPs from cache", false);
    private JButton browseForPath = new JButton("Browse");
    private JButton findPip = new JButton("Find PIPs");
    
//    private JTextField prePath = new JTextField(GlobalSettings.prePath);
    private CustomTextField scanPath = new CustomTextField();
    private JTextField specificPath = new JTextField("");
//    private JFileChooser scanPath = new JFileChooser("/home/blubbomat/Development/simple");

    public static String scanAbsolutePath = "";

    private JButton chooseFile = new JButton("Choose File");
//    private JFileChooser chooseFile = new JFileChooser()
//    private JComboBox<DFATreeNode> results = new JComboBox<>();
    Framework framework;
    
    private DefaultListModel<ACIDTree> listModel = new DefaultListModel();
    private DefaultListModel<DataflowPathInfo> sourceListModel = new DefaultListModel();
    private JList<ACIDTree> results = new JList<>(listModel);
    private JList<DataflowPathInfo> sourceNodes = new JList<>(sourceListModel);

    JScrollPane scrollPane = new JScrollPane();

    private enum ScanStatus {
        NONE, IN_PROGRESS, DONE,
    }

    private Component sourceNodesSpacing = Box.createVerticalStrut(GlobalSettings.basicSpacingSmaller);
    
    
    
    private JCheckBox showPath = new JCheckBox("Show path", false);
    private JCheckBox debugAddAllResults = new JCheckBox("Debug: Add all results", false);
    private JButton insecurityRefactor = new JButton("Start Insecurity Refactoring");
    private JLabel preContext = new JLabel("pre:");
    private JLabel postContext = new JLabel("pos:");
    private RefactoringRenderer actualRefactoringPanel;
    
    private TempPatternFrame tempPatternFrame;
    private JButton showTempPatterns = new JButton("Show temp patterns");
    private JButton rescan = new JButton("Rescan with temp pattern");
    private JButton hideShowSource = new JButton("Hide/show source code");
    private JComboBox<SinkPattern> viewSpecificPattern = new JComboBox<SinkPattern>();
    private SinkPattern selectedPattern = null;
    private JComboBox<String> scanSpecific = new JComboBox<>();
    private JCheckBox requiresSan = new JCheckBox("Requires sanitize");
    private JCheckBox checkControlFunctions = new JCheckBox("Check control functions");
    private DataflowSourceCode dataflowSourceCode;
    private JProgressBar progressBar = new JProgressBar(0, 100);
    

    public PIPRenderer(Framework framework, JFrame frame){
        this.framework = framework;
        
        this.acidViewer = new ACIDViewer(framework);
        results.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sourceNodes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tempPatternFrame = new TempPatternFrame(frame);
        tempPatternFrame.setVisible(false);
        this.dataflowSourceCode = new DataflowSourceCode();
        progressBar.setStringPainted(true);
        
        specificPath.setMaximumSize(new Dimension(1000, 20));
        
        this.setLayout(new BorderLayout());
        
        JPanel east = new JPanel();
        viewSpecificPattern.setMaximumSize(new Dimension(200, 20));
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
        setLeftBoxText("Begin PIP scan with the form above");
        /*west.add(new JLabel("Specific sink location (path:lineno):"));
        west.add(specificPath);
        west.add(viewSpecificPattern);
        west.add(requiresSan);
        west.add(checkControlFunctions);
        west.add(debugAddAllResults);*/
        east.add(scrollPane);

        east.add(sourceNodesSpacing);
        sourceNodesSpacing.setVisible(false);
        east.add(sourceNodes);
        east.add(Box.createVerticalStrut(GlobalSettings.basicSpacingSmaller));

        insecurityRefactor.setEnabled(false);
        insecurityRefactor.setAlignmentX(Component.CENTER_ALIGNMENT);
        east.add(insecurityRefactor);
        /*west.add(preContext);
        west.add(postContext);
        west.add(showTempPatterns);
        west.add(rescan);*/
       
        
        JPanel northPanel = new JPanel();
        northPanel.setBorder(new EmptyBorder(GlobalSettings.basicSpacingSmaller, GlobalSettings.basicSpacingSmallest, GlobalSettings.basicSpacingSmaller, GlobalSettings.basicSpacingSmallest));
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
        //northPanel.add(skipPreScan);
        scanPath.setPlaceholder("An absolute path inside the Docker container (/home/data/...) or public git repo URL to scan for PIPs");
        northPanel.add(scanPath);
        northPanel.add(browseForPath);
        northPanel.add(Box.createHorizontalStrut(GlobalSettings.basicSpacingSmaller));
        //northPanel.add(scanSpecific);
        northPanel.add(loadPipsFromCache);
        northPanel.add(Box.createHorizontalStrut(GlobalSettings.basicSpacingSmaller));
        findPip.setFont(findPip.getFont().deriveFont(Font.BOLD));
        northPanel.add(findPip);
        //northPanel.add(hideShowSource);

        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(new EmptyBorder(0, GlobalSettings.basicSpacingSmallest, GlobalSettings.basicSpacingSmaller, GlobalSettings.basicSpacingSmallest));
        centerPanel.setLayout(new GridLayout(1, 2));
        east.setBorder(new EmptyBorder(0, 0, 0, GlobalSettings.basicSpacingSmallest));
        centerPanel.add(east);
        centerPanel.add(dataflowSourceCode);

        this.add(northPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        
        //this.add(dataflowSourceCode, BorderLayout.EAST);
        
        JPanel south = new JPanel();
        south.setLayout(new GridLayout(1, 1));
        this.add(south, BorderLayout.SOUTH);
        
        //this.add(acidViewer, BorderLayout.CENTER);
        south.add(progressBar);
//        south.add(prePath);
        
        hideShowSource.addActionListener((arg0) -> {
            dataflowSourceCode.setEnabled(!dataflowSourceCode.isVisible());
        });
        
        
        sourceNodes.addListSelectionListener((arg0) -> {
            ACIDTree acidTree = (ACIDTree)results.getSelectedValue();
            DataflowPathInfo dataflowPath = (DataflowPathInfo)sourceNodes.getSelectedValue();
            if(acidTree != null && dataflowPath != null){
                framework.analyze(acidTree, dataflowPath);
                List<Pair<SanitizePattern, DFATreeNode>> sanitizeNodes = dataflowPath.getSanitizeNodes();
                preContext.setText("pre: " + dataflowPath.getContextInfo().getPre());
                postContext.setText("pos: " + dataflowPath.getContextInfo().getPost());
                actualRefactoringPanel = new RefactoringRenderer(framework);
                actualRefactoringPanel.refresh(dataflowPath, sanitizeNodes, dataflowPath.getContextInfo());
                refreshSourceCode(dataflowPath);
                acidViewer.refreshTree(acidTree);
                insecurityRefactor.setEnabled(true);
            }
            else{
                insecurityRefactor.setEnabled(false);
            }
        });

        browseForPath.addActionListener((arg0) -> {
            JFileChooser chooser = new JFileChooser(GlobalSettings.dataFolder);
            chooser.setDialogTitle("Choose a directory to be scanned for PIPs");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                scanPath.setText(String.valueOf(chooser.getSelectedFile()));
            }
        });

        findPip.addActionListener((arg0) -> {
            startedScan();
            String specific = (String)scanSpecific.getSelectedItem();
            SourceLocation specificLoc = specificPath.getText().isBlank() ? null : new SourceLocation(specificPath.getText());

            String inputPath = scanPath.getText();
            scanAbsolutePath = inputPath;

            ScanTask scanTask = new ScanTask(framework, scanEverything.equals(specific) ? null : specific, skipPreScan.isSelected(), this, this.checkControlFunctions.isSelected(), specificLoc, loadPipsFromCache.isSelected());
            progressBar.setValue(0);
            scanTask.addPropertyChangeListener((PropertyChangeEvent arg1) -> {
                PropertyChangeEvent prop = arg1;
                if(prop.getNewValue() instanceof Integer){
                    progressBar.setValue((int)prop.getNewValue());
                }
//                scanProgress.set
//                System.out.println("Property changed: " + arg1);
            });
            scanTask.execute();
            
        });
        
        rescan.addActionListener((arg0) -> {
            List<TempPattern> tempPattern = tempPatternFrame.getTempPatterns();
            ScanProgress progress = new ScanProgress();
            framework.rescan(tempPattern, progress, checkControlFunctions.isSelected());
            refreshPips();
        });
        
        requiresSan.addActionListener((arg0) -> {
            refreshPips();
        });
        
       
        
        results.addListSelectionListener((arg0) -> {
//            renderTree();
            refreshSourceList();
            if(sourceNodes.isSelectionEmpty() && sourceNodes.getSelectedIndex() != 0){
                sourceNodes.setSelectedIndex(0);
            }
        });
        
        
        
      
        
        insecurityRefactor.addActionListener((arg0) -> {
            // TODO:
            
        });
        
        showTempPatterns.addActionListener((arg0) -> {
            tempPatternFrame.setVisible(!tempPatternFrame.isVisible());
        });
        
        viewSpecificPattern.addActionListener((arg0) -> {
            SinkPattern sinkPattern = (SinkPattern)viewSpecificPattern.getSelectedItem();
            if(sinkPattern != this.selectedPattern){   
                if(sinkPattern instanceof RealPips){
                    this.selectedPattern = null;
                }
                else{
                    this.selectedPattern = sinkPattern;
                }
                refreshPips();
            }
        });
        
        refreshSpecificPatterns();
        refreshPips();
    }
    
    public RefactoringRenderer getActualRefactoringPanel(){
        return this.actualRefactoringPanel;
    }
    
    private void refreshPips(){
        boolean reqSan = requiresSan.isSelected();
        if(selectedPattern == null){
            List<ACIDTree> resultTrees = framework.getPipInformation();
            listModel.removeAllElements();
            for(ACIDTree result : resultTrees){
                result.getSink().setSourceLocation(Util.codeLocation(framework.getDb(), result.getSink().getObj()));
                listModel.addElement(result);
            }
            refreshSourceList();
        }
        else{
            List<ACIDTree> resultTrees = framework.getPipInformation();
            listModel.removeAllElements();
            for(ACIDTree result : resultTrees){
                //TODO: check if it is required anymore
                result.getSink().setSourceLocation(Util.codeLocation(framework.getDb(), result.getSink().getObj()));
                listModel.addElement(result);
            }
            refreshSourceList();
        }
    }
    
    public void addStartInsecurityRefactoringActionListener(ActionListener l){
        this.insecurityRefactor.addActionListener(l);
    }

    private ActionListener startScanActionListener;
    public void addStartScanActionListener(ActionListener l){
        this.startScanActionListener = l;
    }
    
    
    private void refreshSinks(){
        viewSpecificPattern.removeAllItems();
        viewSpecificPattern.addItem(new RealPips());
        for(Entry<SinkPattern, Integer> entry : framework.getSinkCount()){
            if(entry.getValue() > 0){
                viewSpecificPattern.addItem(entry.getKey());
            }
        }
        
    }
    
    private void refreshSourceList(){
        sourceListModel.removeAllElements();
        ACIDTree dfaRootNode = (ACIDTree)results.getSelectedValue();
        if(dfaRootNode != null){
            List<DataflowPathInfo> sourceNodes = framework.getSourceNodes(dfaRootNode.getSink());         
            System.out.println("Got sources: " + sourceNodes);
            sourceListModel.addAll(sourceNodes);
        }

        sourceNodesSpacing.setVisible(sourceListModel.size() != 0);
    }

    
    
    
  
    

    private void refreshSpecificPatterns() {
        this.scanSpecific.removeAllItems();
        this.scanSpecific.addItem(scanEverything);
        
        for(SinkPattern sinkPattern : framework.getPatternStorage().getSinks()){
            scanSpecific.addItem(sinkPattern.getName());
        }
    }

    private void refreshSourceCode(DataflowPathInfo dataflowPathInfo) {
        DFATreeNode dfaSourceNode = dataflowPathInfo.getSource();
        List<SourceLocation> locations = getSourceCodeSnippetsRec(dfaSourceNode);
        Collections.reverse(locations);
        String code = "";
//        StringJoiner stringJoiner = new StringJoiner("...\n");
        for(SourceLocation loc : locations){
            code += loc.codeSnippet() + "\n...\n";
        }
        
        System.out.println("CODE" + code);
        dataflowSourceCode.refreshSourceCode(code);
    }
    
    private LinkedList<SourceLocation> getSourceCodeSnippetsRec(DFATreeNode dfanode){
        SourceLocation loc = Util.codeLocation(framework.getDb(), dfanode.getObj());
        
        if(dfanode.getParent() == null){            
            LinkedList<SourceLocation> locList = new LinkedList<>();
            locList.add(loc);
            return locList;
        }
        
        LinkedList<SourceLocation> listBefore = getSourceCodeSnippetsRec(dfanode.getParent_());
        
        if(loc != null && !listBefore.isEmpty() && !listBefore.getLast().equals(loc)){
            listBefore.add(loc);
        }
        return listBefore;        
    }

    void setLeftBoxText(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        scrollPane.setViewportView(label);
    }

    void startedScan() {
        this.browseForPath.setEnabled(false);
        this.findPip.setEnabled(false);
        this.scanPath.setEnabled(false);
        sourceListModel.removeAllElements();
        sourceNodesSpacing.setVisible(false);

        setScanningInProgressText();
        dataflowSourceCode.refreshSourceCode("");

        progressBar.setStringPainted(true);
        Color blue = new Color(0, 115, 255);
        progressBar.setForeground(blue);

        this.startScanActionListener.actionPerformed(null);
    }

    public void setScanningInProgressText() {
        setLeftBoxText("Scanning in progress...");
    }

    public void setClonningRepoText() {
        setLeftBoxText("Clonning the repository...");
    }

    void finishedScan() {
        tempPatternFrame.refresh(framework.getMissingCalls());
        refreshPips();            
        refreshSinks();
        progressBar.setValue(100);

        Color green = new Color(0, 175, 0);
        progressBar.setForeground(green);

        if (results.getModel().getSize() > 0)
            scrollPane.setViewportView(results);
        else
            setLeftBoxText("No PIPs found!");

        this.browseForPath.setEnabled(true);
        this.findPip.setEnabled(true);
        this.scanPath.setEnabled(true);
    }
    
    
    
    
    private class RealPips extends SinkPattern{

        @Override
        public String toString() {
            return "show pips...";
        }
        
    }
}
