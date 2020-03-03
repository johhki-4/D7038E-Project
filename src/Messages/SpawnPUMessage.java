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
@Serializable(id=7)
public class SpawnPUMessage extends AbstractMessage{
    int LocId;
    float time;
    public SpawnPUMessage(){}

    public SpawnPUMessage(int LocId, float time) {
        this.LocId = LocId;
        this.time = time;
    }
    
    
    
    public int getLocId(){
        return this.LocId;
    }
    
    public float getTime(){
        return this.time;
    }
}
