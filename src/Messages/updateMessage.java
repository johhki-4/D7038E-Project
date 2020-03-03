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
@Serializable(id=8)
public class updateMessage extends AbstractMessage {
    int id;
    int hp;
    Vector3f loc;
    Vector3f velocity;
    public updateMessage(){
        
    }
    public updateMessage(int id,int hp,Vector3f loc,Vector3f velocity){
        this.hp = hp;
        this.id = id;
        this.loc = loc;
        this.velocity = velocity;
        
    }
    public int getId(){
        return this.id;
    }
    public int getHp(){
        return this.hp;
    }
    public Vector3f getLoc(){
        return this.loc;
    }
    public Vector3f getvelo(){
        return this.velocity;
    }
}
