/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.base.ast;

/**
 *
 * @author blubbomat
 */
public class ConditionNode extends BaseNode{
    private final boolean startCondition;

    public ConditionNode(boolean startCondition) {
        this.startCondition = startCondition;
    }
    
    
}
