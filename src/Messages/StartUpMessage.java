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

@Serializable(id=12) 
public class StartUpMessage extends AbstractMessage{
    int id;
    int key;
    Vector3f spawnLoc;
    
    public StartUpMessage(){}

    public StartUpMessage(int id, Vector3f spawnLoc) {
        this.id = id;
        this.spawnLoc = spawnLoc;
    }
    
    public int getId(){
        return this.id;
    }
    public Vector3f getLoc(){
        return this.spawnLoc;
    }
}
