/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.gui.insecurityrefactoring;

import java.awt.*;
import javax.swing.*;

import ntnuhtwg.insecurityrefactoring.Framework;

/**
 *
 * @author blubbomat
 */
public class InsecurityRefactoring extends JPanel{
    
    JTabbedPane tabPane = new JTabbedPane();
    
    PIPRenderer pipFinder;

    public InsecurityRefactoring(Framework framework, JFrame frame) {
        this.setLayout(new BorderLayout());
        
        this.add(tabPane);
        
        pipFinder = new PIPRenderer(framework, frame);
        
        pipFinder.addStartInsecurityRefactoringActionListener((arg0) -> {
            RefactoringRenderer refactoringRenderer = pipFinder.getActualRefactoringPanel();
            if(refactoringRenderer != null){
                addTabPane(refactoringRenderer.getTitle(), refactoringRenderer);
            }
        });

        pipFinder.addStartScanActionListener((arg0) -> {
            this.clearTabs();
        });

        addTabPane("Main screen", pipFinder, false);
    }

    private void addTabPane(String title, Component content) {
        addTabPane(title, content, true);
    }

    // Source: https://stackoverflow.com/a/11553266 & https://stackoverflow.com/a/22639054
    private void addTabPane(String title, Component content, boolean closable) {
        String realTitle = title + (closable ? " " : "");
        tabPane.addTab(realTitle, content);
        int index = tabPane.indexOfTab(realTitle);
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        JLabel lblTitle = new JLabel(realTitle);
        pnlTab.add(lblTitle, gbc);

        // Closable -> add close button
        if (closable) {
            JButton btnClose = new JButton("x");
            btnClose.setFont(new Font("monospace", Font.PLAIN, 14));
            btnClose.setBorderPainted(false);

            Color buttonDefaultColor = new Color(0, 0, 0, 0);
            Color buttonHoverColor = new Color(0, 0, 0, 75);
            btnClose.setBackground(buttonDefaultColor);

            btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btnClose.setBackground(buttonHoverColor);
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btnClose.setBackground(buttonDefaultColor);
                }
            });

            gbc.gridx++;
            gbc.weightx = 0;
            pnlTab.add(btnClose, gbc);

            btnClose.addActionListener((arg0) -> {
                int tabIndex = tabPane.indexOfTab(realTitle);
                if (tabIndex >= 0)
                    tabPane.removeTabAt(tabIndex);
            });
        }

        tabPane.setTabComponentAt(index, pnlTab);
    }

    // Remove all tabs except for the first one
    private void clearTabs() {
        int tabCount = this.tabPane.getTabCount();

        while(tabCount-- > 1) {
            this.tabPane.removeTabAt(tabCount);
        }
    }
    
}
