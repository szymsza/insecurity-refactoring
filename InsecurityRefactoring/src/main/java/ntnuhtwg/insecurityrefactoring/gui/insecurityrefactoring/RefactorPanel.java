/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.gui.insecurityrefactoring;

import java.awt.Dimension;
import java.awt.Label;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

import ntnuhtwg.insecurityrefactoring.base.DataType;
import ntnuhtwg.insecurityrefactoring.base.GlobalSettings;
import ntnuhtwg.insecurityrefactoring.base.SourceLocation;
import ntnuhtwg.insecurityrefactoring.base.Util;
import ntnuhtwg.insecurityrefactoring.base.tree.DFATreeNode;
import ntnuhtwg.insecurityrefactoring.base.db.neo4j.dsl.cypher.DataflowDSL;
import ntnuhtwg.insecurityrefactoring.base.patterns.Pattern;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.DataflowPattern;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.SourcePattern;
import org.javatuples.Pair;

/**
 *
 * @author blubbomat
 */
public class RefactorPanel extends JPanel{
    
    private DFATreeNode node;
    
    private JComboBox<SourcePattern> refactorSource = new JComboBox<>();
    private NoRefactoring none = new NoRefactoring();

    public RefactorPanel(DFATreeNode node, DataflowDSL dsl, List<SourcePattern> replacements) {        
        this.node = node;
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        SourceLocation location = Util.codeLocation(dsl.getDb(), node.getObj());

        JLabel label = new JLabel(Util.relativizePath(location.toString()));
        label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        this.add(label);
        
        refactorSource.addItem(none);
        refactorSource.setMaximumSize(new Dimension(400, 25));
        
        for(SourcePattern dataflowPattern : replacements){
            refactorSource.addItem(dataflowPattern);
        }
        refactorSource.setAlignmentX(JComboBox.LEFT_ALIGNMENT);
        this.add(Box.createVerticalStrut(GlobalSettings.basicSpacingSmaller));
        this.add(refactorSource);
    }

    public Pair<DFATreeNode, SourcePattern> getSelectedDataflowPattern() {
        SourcePattern selectedPattern = (SourcePattern)refactorSource.getSelectedItem();
        
        if(selectedPattern instanceof NoRefactoring){
            return null;
        }
        
        return new Pair<>(node, selectedPattern);
    }
    
    
    
    private class NoRefactoring extends SourcePattern{

        public NoRefactoring() {
            super(null);
        }
        
        

        @Override
        public String toString() {
            return "No refactoring...";
        }
        
    }
}
