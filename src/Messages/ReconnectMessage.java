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
@Serializable(id=4)
public class ReconnectMessage  extends AbstractMessage{
    int key;
    int id;
    ReconnectMessage(){
        
    }
    
    ReconnectMessage(int key, int id){
        this.id = id;
        this.key = key;
    }
    
    public int getId(){
        return this.id;
    }
    public int getKey(){
        return this.key;
    }
}
