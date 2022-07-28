/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.base.patterns;

import java.io.Serializable;

/**
 *
 * @author blubbomat
 */
public class GenerateFile implements Serializable{
    private  String path;
    private String fileContent;

    public GenerateFile(String path, String fileContent) {
        this.path = path;
        this.fileContent = fileContent;
    }

    public String getPath() {
        return path;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
    
    
    
    
    
}
