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
@Serializable(id=2)
public class KeyAckMessage extends AbstractMessage {
    int assignId;
    int key;
    Vector3f spawnLoc;
    public KeyAckMessage(){}
    public KeyAckMessage(int Id,int key,Vector3f spawnLoc){
        this.assignId = Id;
        this.key = key;
        this.spawnLoc = spawnLoc;
    }
    
    public int getID(){
        return this.assignId;
    }
    public int getKey(){
        return this.key;
    }
    public Vector3f getSpawnLoc(){
        return this.spawnLoc;
    }
}
