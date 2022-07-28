/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.gui;

import java.awt.Dimension;
import java.awt.Label;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;

import ntnuhtwg.insecurityrefactoring.base.DataType;
import ntnuhtwg.insecurityrefactoring.base.GlobalSettings;
import ntnuhtwg.insecurityrefactoring.base.SourceLocation;
import ntnuhtwg.insecurityrefactoring.base.Util;
import ntnuhtwg.insecurityrefactoring.base.tree.DFATreeNode;
import ntnuhtwg.insecurityrefactoring.base.db.neo4j.dsl.cypher.DataflowDSL;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.DataflowPattern;
import org.javatuples.Pair;

/**
 *
 * @author blubbomat
 */
public class DataFlowRefactorPanel extends JPanel{
    
    private DFATreeNode node;
    
    private JComboBox<DataflowPattern> selectedRefactorPattern = new JComboBox<>();
    private NoneDataFlowPattern none = new NoneDataFlowPattern();

    public DataFlowRefactorPanel(DFATreeNode node, DataflowDSL dsl) {        
        this.node = node;
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        SourceLocation location = Util.codeLocation(dsl.getDb(), node.getObj());
        JLabel label = new JLabel(Util.relativizePath(location.toString()));
        label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        this.add(label);
        
        selectedRefactorPattern.addItem(none);
        selectedRefactorPattern.setMaximumSize(new Dimension(400, 25));
        
        for(DataflowPattern dataflowPattern : node.getPossibleDataflowReplacements()){
            selectedRefactorPattern.addItem(dataflowPattern);
        }
        selectedRefactorPattern.setAlignmentX(JComboBox.LEFT_ALIGNMENT);
        this.add(Box.createVerticalStrut(GlobalSettings.basicSpacingSmaller));
        this.add(selectedRefactorPattern);
    }

    public Pair<DFATreeNode, DataflowPattern> getSelectedDataflowPattern() {
        DataflowPattern selectedPattern = (DataflowPattern)selectedRefactorPattern.getSelectedItem();
        
        if(selectedPattern instanceof NoneDataFlowPattern){
            return null;
        }
        
        return new Pair<>(node, selectedPattern);
    }
    
    
    
    private class NoneDataFlowPattern extends DataflowPattern{

        public NoneDataFlowPattern() {
            super(false, null, null, null, 0.0, 0.0, 0.0);
        }
        
        

        @Override
        public String toString() {
            return "No refactoring...";
        }
        
    }
}
