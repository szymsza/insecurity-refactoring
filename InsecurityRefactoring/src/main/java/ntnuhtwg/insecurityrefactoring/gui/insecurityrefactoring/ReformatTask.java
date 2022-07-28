/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.gui.insecurityrefactoring;

import ntnuhtwg.insecurityrefactoring.Framework;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author blubbomat
 */
public class ReformatTask extends SwingWorker<Object, Float> {

    Framework framework;
    RefactoringRenderer renderer;
    Component messageToRemove;
    Component messageToDisplay;
    JButton refactorButton;
    JButton writeToDiskButton;

    public ReformatTask(Framework framework, RefactoringRenderer renderer, Component messageToRemove, Component messageToDisplay, JButton refactorButton, JButton writeToDiskButton) {
        this.framework = framework;
        this.renderer = renderer;
        this.messageToRemove = messageToRemove;
        this.messageToDisplay = messageToDisplay;
        this.refactorButton = refactorButton;
        this.writeToDiskButton = writeToDiskButton;
    }

    @Override
    protected Object doInBackground() {
        framework.formatCode();
        return null;
    }
    
    @Override 
    protected void done(){
        // Without the delay, there could be conflicts in UI updating
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        RefactoringRenderer.writeToDiskFinished(renderer, messageToRemove, messageToDisplay, refactorButton, writeToDiskButton);
    }

}
