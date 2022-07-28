/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.base.db.joern;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import ntnuhtwg.insecurityrefactoring.base.GlobalSettings;
import ntnuhtwg.insecurityrefactoring.base.Util;
import ntnuhtwg.insecurityrefactoring.refactor.temppattern.ScanProgress;
import static org.neo4j.cypher.internal.plandescription.Root.line;

/**
 *
 * @author blubbomat
 */
public class Prepare {
    
    
    
    
    public boolean prepareDatabase(String pathToScan, ScanProgress scanProgress) throws IOException, InterruptedException{
        
  
        Util.runCommand("mkdir .scan_cache", new File(pathToScan));
        
        File scan_cache = new File(pathToScan + "/.scan_cache");

        File scriptDir = new File(GlobalSettings.rootFolder);

	System.out.println(scriptDir.getAbsolutePath());
        
        System.out.println(pathToScan);
        
        String analyse = "sh " + scriptDir.getCanonicalPath()+ "/analyse.sh " + pathToScan;
        
        System.out.println("" + analyse);
        Util.runCommand(analyse, scan_cache);
        scanProgress.joernScanned();
        
        String content = Util.readLineByLineJava8(scan_cache.getCanonicalPath()+"/cpg_edges.csv");
        // checks if CPG creation worked
        if(!content.startsWith(":START_ID")){
            return false;
        }
        
        
        String importCommand = "sh " + scriptDir.getCanonicalPath() + "/import_neo4j.sh";
        System.out.println("" + importCommand);
        Util.runCommand(importCommand, scan_cache);
        scanProgress.joernImported();
        
        return true;
    }
    
    public void startDB() throws IOException, InterruptedException{
        Util.runCommand("./start_neo4j.sh", new File(GlobalSettings.rootFolder));
    }
}
