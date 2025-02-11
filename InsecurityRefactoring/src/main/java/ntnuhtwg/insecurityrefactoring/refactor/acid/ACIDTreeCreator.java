/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.refactor.acid;

import ntnuhtwg.insecurityrefactoring.refactor.acid.rules.ACIDTreeRules;
import ntnuhtwg.insecurityrefactoring.refactor.temppattern.VarName;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import ntnuhtwg.insecurityrefactoring.base.ASTNodeTypes;
import ntnuhtwg.insecurityrefactoring.base.GlobalSettings;
import ntnuhtwg.insecurityrefactoring.base.Util;
import ntnuhtwg.insecurityrefactoring.base.tree.DFATreeNode;
import ntnuhtwg.insecurityrefactoring.base.tree.LabeledTreeNode;
import ntnuhtwg.insecurityrefactoring.base.db.neo4j.dsl.cypher.DataflowDSL;
import ntnuhtwg.insecurityrefactoring.base.patterns.Pattern;
import ntnuhtwg.insecurityrefactoring.base.patterns.PatternStorage;
import ntnuhtwg.insecurityrefactoring.base.SourceLocation;
import ntnuhtwg.insecurityrefactoring.base.ast.AnyNode;
import ntnuhtwg.insecurityrefactoring.base.ast.BaseNode;
import ntnuhtwg.insecurityrefactoring.base.ast.ConditionNode;
import ntnuhtwg.insecurityrefactoring.base.ast.FixedNode;
import ntnuhtwg.insecurityrefactoring.base.ast.TimeoutNode;
import ntnuhtwg.insecurityrefactoring.base.exception.ResultTreeToLarge;
import ntnuhtwg.insecurityrefactoring.base.exception.TimeoutException;
import ntnuhtwg.insecurityrefactoring.base.db.neo4j.Neo4JConnector;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.SourcePattern;
import org.neo4j.driver.internal.InternalNode;
import ntnuhtwg.insecurityrefactoring.base.db.neo4j.Neo4jDB;
import ntnuhtwg.insecurityrefactoring.base.db.neo4j.node.INode;
import ntnuhtwg.insecurityrefactoring.base.db.neo4j.node.NodeConnected;
import ntnuhtwg.insecurityrefactoring.base.info.DataflowPathInfo;
import ntnuhtwg.insecurityrefactoring.base.patterns.PassthroughPattern;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.ConcatPattern;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.SinkPattern;
import ntnuhtwg.insecurityrefactoring.base.pippersist.PIPPersist;
import org.neo4j.kernel.impl.index.schema.CollectingIndexUpdater;

/**
 *
 * @author blubbomat
 */
public class ACIDTreeCreator implements Runnable {

    private Neo4jDB db;
    private PatternStorage patternReader;
    private DataflowDSL dsl;
    private INode sinkNode;
    private SinkPattern pattern;
    private boolean isPip = false;
    private int replaceIndex = -1;
    private final boolean controlFlowCheck;

    private DFATreeNode resultTree;
    private String scanCachePath;

//    private boolean checkMissingCalls = true;
//    private boolean debugAddAll = true;
    private int stopOnDepth = 1000;
    private int stopOnWidth = 2000;


    public ACIDTreeCreator(Neo4jDB db, PatternStorage patternReader, INode sinkNode, SinkPattern pattern, boolean controlFlowCheck, String scanCachePath) {
        this.db = db;
        this.patternReader = patternReader;
        this.dsl = new DataflowDSL(db);
        this.sinkNode = sinkNode;
        this.pattern = pattern;
        this.controlFlowCheck = controlFlowCheck;
        this.scanCachePath = scanCachePath;
    }

    public int getReplaceIndex() {
        return replaceIndex;
    }

    public void setReplaceIndex(int replaceIndex) {
        this.replaceIndex = replaceIndex;
    }

    public static List<DataflowPathInfo> getSourceNodes(DFATreeNode tree, PatternStorage patternStorage, Neo4jDB db) throws TimeoutException {
        List<DataflowPathInfo> sourceNodes = new LinkedList<>();

        for (LabeledTreeNode<INode> leafLabel : tree.getAllLeafs()) {
            DFATreeNode leaf = (DFATreeNode) leafLabel;
            for (SourcePattern sourcePattern : patternStorage.getSources()) {
                if (sourcePattern.equalsPattern(leaf.getObj(), db)) {
                    sourceNodes.add(new DataflowPathInfo(leaf, false));
                }
            }
        }

        return sourceNodes;
    }
    

    public boolean isPip() {
        return isPip;
    }

    public DFATreeNode getResultTree() {
        return resultTree;
    }
    
    private void startDataflowAnalysis(boolean conditionCheck){
//        this.isPresearch = true;
        
        resultTree = new DFATreeNode(sinkNode);
        resultTree.setSinkPattern(pattern);
        
        try {
            List<INode> inputNodes = pattern.findNode(dsl, sinkNode, "%input");
            int i = 0;
            for(INode inputNode : inputNodes){
                DFATreeNode child = new DFATreeNode(inputNode);
                resultTree.addChild("sink" + i++, child);
                
                new ACIDTreeRules(patternReader, dsl, conditionCheck).resolveExpression(child, new HashSet<>());                
            }
            if (!getSourceNodes(resultTree, patternReader, db).isEmpty()) {
                System.out.println("Found a pip!");
                for(DataflowPathInfo sourceInfo : getSourceNodes(resultTree, patternReader, db)){
                    System.out.println("\n############################################### Dataflow path ###############################################");
                    Util.printSourceCodeRec(db, sourceInfo.getSource(), null);
                    System.out.println(  "#############################################################################################################");
                    
                }
                isPip = true;
                
                PIPPersist.persistToCache(scanCachePath, resultTree);
            }
//                        System.out.println("Finished.");
        } catch (ResultTreeToLarge ex) {
            System.out.println("Results too large...");
        } catch (TimeoutException ex) {
            ex.printErrorMessage("Unexpected timeout exception");
        }
    }

    @Override
    public void run() {
        try{
        startDataflowAnalysis(false);
        
        if(isPip && controlFlowCheck){
            System.out.println("Do a more specific search!");
            startDataflowAnalysis(true);
        }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
    
