/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.gui.insecurityrefactoring;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import ntnuhtwg.insecurityrefactoring.Framework;
import ntnuhtwg.insecurityrefactoring.base.GlobalSettings;
import ntnuhtwg.insecurityrefactoring.base.RefactoredCode;
import ntnuhtwg.insecurityrefactoring.base.Util;
import ntnuhtwg.insecurityrefactoring.base.exception.TimeoutException;
import ntnuhtwg.insecurityrefactoring.base.tools.CodeFormatter;
import ntnuhtwg.insecurityrefactoring.base.tree.DFATreeNode;
import ntnuhtwg.insecurityrefactoring.base.info.ContextInfo;
import ntnuhtwg.insecurityrefactoring.base.info.DataflowPathInfo;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.DataflowPattern;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.SanitizePattern;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.SourcePattern;
import ntnuhtwg.insecurityrefactoring.gui.DataFlowRefactorPanel;
import org.javatuples.Pair;
import org.javatuples.Triplet;

/**
 *
 * @author blubbomat
 */
public class RefactoringRenderer extends JPanel{
    
    JPanel west = new JPanel();
    
//    PatternStorage patternStorage;
////    Refactoring refactoring;
//    InsecurityRefactoring refactoring;
//    DataflowDSL dsl;
    
    Framework framework;
    
    private String title = "";
    
    private List<SanitizeRefactorPanel> failedSanitizeSelections = new LinkedList<>();
    private List<DataFlowRefactorPanel> dataflowSelections = new LinkedList<>();
    private RefactorPanel secureSources = null;

    private JPanel noSourceCodeMessage = new JPanel();

    private JPanel writingToDiskMessage = new JPanel();

    private JPanel writtenToDiskMessage = new JPanel();

    private JLabel noSourceCodeLabel = new JLabel("Select refactoring options in the left panel", JLabel.CENTER);

    JCheckBox reformatProject = new JCheckBox("Reformat the project code", true);

    String[] timestampsOptions = { "Don't modify", "Set all to today", "Set all randomly", "Decoy - 90% older, 10% today" };

    JComboBox<String> timestampsSelect = new JComboBox<>(timestampsOptions);

    JTabbedPane sourceCodePreview = new JTabbedPane();    
    

    public RefactoringRenderer(Framework framework) {
        this.setLayout(new BorderLayout());
        this.framework = framework;
        west.setBorder(new EmptyBorder(GlobalSettings.basicSpacingLarger, GlobalSettings.basicSpacingLarger, GlobalSettings.basicSpacingLarger, GlobalSettings.basicSpacingLarger));
        west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
        west.setMaximumSize(new Dimension(400, 0));
        west.setPreferredSize(west.getMaximumSize());

        noSourceCodeMessage.setLayout(new GridLayout(1, 1));
        noSourceCodeMessage.setAlignmentX(JLabel.CENTER);
        noSourceCodeMessage.add(noSourceCodeLabel);

        writingToDiskMessage.setLayout(new GridLayout(12, 1));
        for (int i = 0; i < 5; i++) {
            writingToDiskMessage.add(new JLabel(""));
        }
        writingToDiskMessage.setAlignmentX(JLabel.CENTER);
        JLabel writingToDiskText = new JLabel("Writing refactored project to disk...", JLabel.CENTER);
        writingToDiskMessage.add(writingToDiskText);
        JLabel writingToDiskSubtext = new JLabel("This can take up to a few minutes for large projects", JLabel.CENTER);
        writingToDiskSubtext.setFont(new Font(writingToDiskSubtext.getFont().getFamily(), Font.PLAIN, 13));
        writingToDiskMessage.add(writingToDiskSubtext);
        for (int i = 0; i < 5; i++) {
            writingToDiskMessage.add(new JLabel(""));
        }

        writtenToDiskMessage.setLayout(new GridLayout(1, 1));
        writtenToDiskMessage.setAlignmentX(JLabel.CENTER);
        JLabel writtenToDiskText = new JLabel("Project successfully written to disk", JLabel.CENTER);
        writtenToDiskMessage.add(writtenToDiskText);

        this.add(west, BorderLayout.WEST);
        this.renderEmptyMessage();
    }
    
    public String getTitle(){
        return title;
    }
    
    public void refresh(DataflowPathInfo source, List<Pair<SanitizePattern, DFATreeNode>> sanitizeNodes, ContextInfo contextInfo){
//        west.setLayout(new FlowLayout());
        
        west.removeAll();
        failedSanitizeSelections.clear();
        dataflowSelections.clear();
        secureSources = null;

        // TODO: Better title
        title = "Refactor " + source.toString();

        this.renderInputs(source, sanitizeNodes, contextInfo);
        this.renderButtons();
        
        /*JButton push = new JButton("Push to git");
        push.addActionListener((arg0) -> {
            String msg = JOptionPane.showInputDialog(null);
            System.out.println("Msg: " + msg);
            if(msg != null){
                framework.commitAndPush(msg);
            }
        });
        west.add(push);
        west.add(new JLabel(""));*/
        
    }

    private void renderInputs(DataflowPathInfo source, List<Pair<SanitizePattern, DFATreeNode>> sanitizeNodes, ContextInfo contextInfo) {
        JPanel westInputsPanel = new JPanel();
        westInputsPanel.setLayout(new BoxLayout(westInputsPanel, BoxLayout.Y_AXIS));

        DFATreeNode node = source.getSource();

        // Data sources
        JLabel secureSourcesLabel = new JLabel("Data sources");

        Font headingFont = new Font(secureSourcesLabel.getFont().getFamily(), Font.PLAIN, 20);

        secureSourcesLabel.setFont(headingFont);
        secureSourcesLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        westInputsPanel.add(secureSourcesLabel);
        westInputsPanel.add(Box.createVerticalStrut(GlobalSettings.basicSpacing));
        if(source.getSource().getSourcePattern().isSourceSufficient(contextInfo)){
            List<SourcePattern> insecurePattern = framework.getPatternStorage().getInsecureSources(source.getSource().getSourcePattern(), node, contextInfo);
            RefactorPanel refactorPanel = new RefactorPanel(node, framework.getDSL(), insecurePattern);
            refactorPanel.setAlignmentX(RefactorPanel.LEFT_ALIGNMENT);
            westInputsPanel.add(refactorPanel);
            secureSources = refactorPanel;
        }
        westInputsPanel.add(Box.createVerticalStrut((int) (GlobalSettings.basicSpacingLarger * 1.25)));

        // Dataflow patterns
        JLabel dataflowPatternsLabel = new JLabel("Dataflow patterns");
        dataflowPatternsLabel.setFont(headingFont);
        dataflowPatternsLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        westInputsPanel.add(dataflowPatternsLabel);
        westInputsPanel.add(Box.createVerticalStrut(GlobalSettings.basicSpacing));
        while(node != null){
            if(!node.getPossibleDataflowReplacements().isEmpty()){
                DataFlowRefactorPanel dataFlowRefactorPanel = new DataFlowRefactorPanel(node, framework.getDSL());
                dataFlowRefactorPanel.setAlignmentX(DataFlowRefactorPanel.LEFT_ALIGNMENT);
                westInputsPanel.add(dataFlowRefactorPanel);
                dataflowSelections.add(dataFlowRefactorPanel);
            }

            node = node.getParent_();
        }
        westInputsPanel.add(Box.createVerticalStrut((int) (GlobalSettings.basicSpacingLarger * 1.25)));

        // Sanitization
        if (sanitizeNodes.size() > 0) {
            JLabel sanitizePatternsLabel = new JLabel("Sanitization");
            sanitizePatternsLabel.setFont(headingFont);
            sanitizePatternsLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
            westInputsPanel.add(sanitizePatternsLabel);
            westInputsPanel.add(Box.createVerticalStrut(GlobalSettings.basicSpacing));

            for (Pair<SanitizePattern, DFATreeNode> sanitizeNodePair : sanitizeNodes) {
                List<SanitizePattern> possiblePatterns = framework.getPatternStorage().getPossibleFailedSanitizePatterns(sanitizeNodePair.getValue0(), sanitizeNodePair.getValue1(), contextInfo);
                SanitizeRefactorPanel sanitizeRefactorPanel = new SanitizeRefactorPanel(sanitizeNodePair, possiblePatterns, framework.getDSL());

                sanitizeRefactorPanel.setAlignmentX(SanitizeRefactorPanel.LEFT_ALIGNMENT);
                failedSanitizeSelections.add(sanitizeRefactorPanel);
                westInputsPanel.add(sanitizeRefactorPanel);
            }
            westInputsPanel.add(Box.createVerticalStrut((int) (GlobalSettings.basicSpacingLarger * 1.25)));
        }

        westInputsPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        west.add(westInputsPanel);
    }

    private void renderButtons() {
        JPanel westButtonsPanel = new JPanel();
        westButtonsPanel.setLayout(new GridLayout(0, 1, 0, 0));

        // Refactor
        JButton refactor = new JButton("Refactor");
        refactor.addActionListener((arg0) -> {
            refactor();
        });
        westButtonsPanel.add(refactor);

        westButtonsPanel.add(Box.createVerticalStrut(1));
        westButtonsPanel.add(new JSeparator());

        reformatProject.setBounds(0,0, 50,50);
        reformatProject.setToolTipText("Reformatting can take a few minutes for large projects");
        westButtonsPanel.add(reformatProject);
        westButtonsPanel.add(Box.createVerticalStrut(0));

        JLabel modifyTimestampsLabel = new JLabel("Modify file timestamps:");
        Font modifyTimestampsFont = new Font(modifyTimestampsLabel.getFont().getFamily(), Font.PLAIN, 13);
        modifyTimestampsLabel.setFont(modifyTimestampsFont);
        westButtonsPanel.add(modifyTimestampsLabel);

        westButtonsPanel.add(timestampsSelect);
        westButtonsPanel.add(Box.createVerticalStrut(1));

        // Write to disk
        JButton writeFiles = new JButton("Write to disk");
        writeFiles.addActionListener((arg0) -> {
            this.writeToDiskStarted(refactor, writeFiles);

            boolean backupFiles = false;
            framework.writeToDisk(backupFiles);

            /*
            int dialogResult = JOptionPane.showConfirmDialog(null, "New scanning will be required after obfuscating the code. Do you wish to continue?");
            if (dialogResult != JOptionPane.YES_OPTION)
                return;
            */

            // Saving code here
            switch (timestampsSelect.getSelectedIndex()) {
                case 0: CodeFormatter.changeTimestamps = CodeFormatter.ChangeTimestamps.TIMESTAMPS_KEEP; break;
                case 1: CodeFormatter.changeTimestamps = CodeFormatter.ChangeTimestamps.TIMESTAMPS_NOW; break;
                case 2: CodeFormatter.changeTimestamps = CodeFormatter.ChangeTimestamps.TIMESTAMPS_RANDOM; break;
                default: CodeFormatter.changeTimestamps = CodeFormatter.ChangeTimestamps.TIMESTAMPS_DECOY;
            }

            CodeFormatter.reformatCode = reformatProject.isSelected();

            ReformatTask task = new ReformatTask(framework, this, writingToDiskMessage, writtenToDiskMessage, refactor, writeFiles);
            task.execute();
        });
        westButtonsPanel.add(writeFiles);
        Dimension buttonsDimension = new Dimension(250, 220);

        westButtonsPanel.setMaximumSize(buttonsDimension);
        westButtonsPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        west.add(westButtonsPanel);
    }

    private void renderEmptyMessage() {
        this.remove(sourceCodePreview);
        this.remove(writingToDiskMessage);
        this.remove(writtenToDiskMessage);
        this.add(noSourceCodeMessage, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void writeToDiskStarted(JButton refactorButton, JButton writeToDiskButton) {
        this.remove(sourceCodePreview);
        this.remove(noSourceCodeMessage);
        this.remove(writtenToDiskMessage);
        this.add(writingToDiskMessage, BorderLayout.CENTER);

        this.reformatProject.setEnabled(false);
        this.timestampsSelect.setEnabled(false);
        refactorButton.setEnabled(false);
        writeToDiskButton.setEnabled(false);
        this.revalidate();
        this.repaint();
    }

    public static void writeToDiskFinished(RefactoringRenderer that, Component writingToDiskMessage, Component writtenToDiskMessage, JButton refactorButton, JButton writeToDiskButton) {
        that.remove(writingToDiskMessage);
        that.add(writtenToDiskMessage, BorderLayout.CENTER);

        that.reformatProject.setEnabled(true);
        that.timestampsSelect.setEnabled(true);
        refactorButton.setEnabled(true);
        writeToDiskButton.setEnabled(true);
        that.revalidate();
        that.repaint();
    }
    
    private void refactor(){
        List<Triplet<DFATreeNode, SanitizePattern, SanitizePattern>> refactoringData = new LinkedList<>();
        for(SanitizeRefactorPanel sanitizePanel : failedSanitizeSelections){
            if(sanitizePanel.getRefactorData() != null){
                refactoringData.add(sanitizePanel.getRefactorData());
            }
        }

        List<Pair<DFATreeNode, DataflowPattern>> dataflowRefactoring = new LinkedList<>();

        for(DataFlowRefactorPanel dataflowPanel : dataflowSelections){
            Pair<DFATreeNode, DataflowPattern> dataflowPattern = dataflowPanel.getSelectedDataflowPattern();
            if(dataflowPattern != null){
                dataflowRefactoring.add(dataflowPattern);
            }
        }

        System.out.println("Start refactoring");
        Pair<DFATreeNode, SourcePattern> secureSourcePattern = null;
        System.out.println("secure sources " + secureSourcePattern);
        if(secureSources != null){
            
            secureSourcePattern = secureSources.getSelectedDataflowPattern();
        }
        List<RefactoredCode> refactoredCodes;
        try {
            framework.selectedRefactoring(refactoringData, dataflowRefactoring, secureSourcePattern);
            refactoredCodes = framework.getRefactoredCode();
        } catch (TimeoutException ex) {
            Logger.getLogger(RefactoringRenderer.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        
        sourceCodePreview.removeAll();

        if (refactoredCodes.size() == 0) {
            this.renderEmptyMessage();
        } else {
            this.remove(noSourceCodeMessage);
            this.remove(writingToDiskMessage);
            this.remove(writtenToDiskMessage);
            this.add(sourceCodePreview, BorderLayout.CENTER);
            for (RefactoredCode refactoredCode : refactoredCodes) {
                RefactoringComparison refactoringComparison = new RefactoringComparison(refactoredCode.getSourceLocation(), refactoredCode.getCode(), refactoredCode.getModifiedLines());
                sourceCodePreview.addTab(Util.relativizePath(refactoredCode.getSourceLocation().getPath()), refactoringComparison);
            }
        }
    }
    
}
