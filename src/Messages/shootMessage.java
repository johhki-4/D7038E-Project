/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Messages;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Andreas
 */

@Serializable(id=16)
public class shootMessage extends AbstractMessage {
    int id;
    Vector3f hitAt;
    int key;
    shootMessage(){}
    
    public shootMessage(int id, int key,Vector3f hitAt){
        this.id = id;
        this.key = key;
        this.hitAt = hitAt;
    }
    
    public int getId(){
        return this.id;
    }
    public Vector3f getLoc(){
        return this.hitAt;
    }
    public int getKey(){
        return this.key;
    }
}
