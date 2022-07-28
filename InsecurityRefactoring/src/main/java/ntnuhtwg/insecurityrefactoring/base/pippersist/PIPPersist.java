/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ntnuhtwg.insecurityrefactoring.base.pippersist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import ntnuhtwg.insecurityrefactoring.base.Util;
import ntnuhtwg.insecurityrefactoring.base.db.neo4j.Neo4jDB;
import ntnuhtwg.insecurityrefactoring.base.db.neo4j.node.INode;
import ntnuhtwg.insecurityrefactoring.base.exception.TimeoutException;
import ntnuhtwg.insecurityrefactoring.base.patterns.PatternStorage;
import ntnuhtwg.insecurityrefactoring.base.patterns.impl.SinkPattern;
import ntnuhtwg.insecurityrefactoring.base.tree.DFATreeNode;
import ntnuhtwg.insecurityrefactoring.refactor.acid.ACIDTreeCreator;
import org.apache.commons.collections.iterators.EmptyListIterator;


/**
 *
 * @author blubbomat
 */
public class PIPPersist {
    public static String ID = "id";
    public static String SINK_PATTERN = "sink_pattern";

    
    private static String pipDir = "/pip/";
    
    public static void persistToCache(String cachePath, DFATreeNode pip){
        try {
            Long pipId = pip.getObj().id();
            
            File pipCache = new File(cachePath + pipDir + pipId + ".pip");
            pipCache.getParentFile().mkdirs();
            
            Properties properties = new Properties();
            properties.setProperty(ID, pipId.toString());
            properties.setProperty(SINK_PATTERN, pip.getSinkPattern().getName());
            
            FileOutputStream fout = new FileOutputStream(pipCache, false);
            properties.store(fout, null);
            fout.flush();
            fout.close();
            System.out.println("Persisted: " + pipCache);
            
        } catch (Exception ex) {
            Logger.getLogger(PIPPersist.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public static void clearCache(String scanCachePath) {
        try {
            Util.deleteFolder(scanCachePath + pipDir);
        } catch (IOException ex) {
            Logger.getLogger(PIPPersist.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static List<Properties> getPipsProperties(String scanCachePath){
        String path = scanCachePath + "/pip";
        File dirFile = new File(path);
        if (!dirFile.exists()){
            System.out.println("No pip cache folder");
            return Collections.emptyList();
        }
        
        if(!dirFile.isDirectory()){
            System.out.println("Pip cache folder is not a directory " + path);
            return Collections.emptyList();
        }
        
        List<Properties> retval = new LinkedList<>();
        
        for (File file : dirFile.listFiles()){
            if (!file.getName().endsWith(".pip")){
                continue;
            }
            
            try {
                FileInputStream fin = new FileInputStream(file);
                Properties properties = new Properties();
                properties.load(fin);
                retval.add(properties);
            } catch (IOException ex) {
                Logger.getLogger(PIPPersist.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return retval;
    }
    
    public static List<ACIDTreeCreator> getAllPips(String scanCachePath, Neo4jDB db, PatternStorage patternReader, boolean controlFlowCheck){
        List<ACIDTreeCreator> backwardDataflows = new LinkedList<>();
        
        List<Properties> pipsProperties = getPipsProperties(scanCachePath);
        
        for (Properties pipPros : pipsProperties){
            Long id = Long.valueOf(pipPros.getProperty(ID));
            INode sinkNode;
            try {
                sinkNode = db.findNode(id);
            } catch (TimeoutException ex) {
                System.err.println("Load from cache: Cannot find sink node : " + id);
                continue;
            }
            
            String sinkPatternName = pipPros.getProperty(SINK_PATTERN);
            SinkPattern sinkPattern = patternReader.getSinkPattern(sinkPatternName);
            
            
            backwardDataflows.add(new ACIDTreeCreator(db, patternReader, sinkNode, sinkPattern, controlFlowCheck, scanCachePath));
        }
        
        return backwardDataflows;
        
    }
}
