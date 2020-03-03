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
@Serializable(id=9)
public class PlayerRespawnMessage extends AbstractMessage {
    int id;
    Vector3f spawnLoc;
    int killer;
    //float timeUntilRespawn;
    
    PlayerRespawnMessage(){}

    public PlayerRespawnMessage(int id, Vector3f spawnLoc,int killer ) {
        this.id = id;
        this.spawnLoc = spawnLoc;
        this.killer = killer;
        //this.timeUntilRespawn = timeUntilRespawn;
    }
    
    public Vector3f getLoc(){
        return this.spawnLoc;
    }
    public int getId(){
        return this.id;
    }
    public int getKiller(){
        return this.killer;
    }
  //  public float getTime(){
   //     return this.timeUntilRespawn;
   // }
}
