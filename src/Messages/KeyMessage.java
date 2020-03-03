/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Andreas
 */
@Serializable(id=1)
public class KeyMessage extends AbstractMessage{
    int id;
    int key;
    public KeyMessage(){}
    public KeyMessage(int assignId,int key){
        this.id = assignId;
        this.key = key;
    }
    public int getId(){
        return this.id;
    }
    public int getKey(){
        return this.key;
    }
}
