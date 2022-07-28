/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.gui;

import java.awt.*;
import javax.swing.JPanel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author blubbomat
 */
public class DataflowSourceCode extends JPanel{
    
    RSyntaxTextArea refactored;
    RTextScrollPane refactoredPane;

    public DataflowSourceCode() {
        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(100, 100));
        refactored = new RSyntaxTextArea("", 2, 2);
        refactored.setMinimumSize(new Dimension(100, 100));
        refactored.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);
        refactored.setEnabled(false);
        refactoredPane = new RTextScrollPane(refactored);
        
        this.add(refactoredPane);
    }
    
    
    
    
    public void refreshSourceCode(String sourceCode){
        refactored.setText(sourceCode.equals("") ? "" : "<?\n" + sourceCode);
    }
}
