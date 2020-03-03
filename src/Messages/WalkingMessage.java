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

@Serializable(id=15)
public class WalkingMessage extends AbstractMessage {
    private int id;
    private Vector3f pos;
    private Vector3f velo;
    private float acc;
    private Vector3f view;
    private float tpf;
    WalkingMessage(){}
    public WalkingMessage(int id, Vector3f pos,Vector3f velo,float acc,Vector3f view,float tpf){
        this.id = id;
        this.pos = pos;
        this.velo = velo;
        this.acc = acc;
        this.view = view;
        this.tpf = tpf;
        
    }
    
    public int getid(){
        return this.id;
    }
    public Vector3f getPos(){
        return this.pos;
    }
    public Vector3f getVelo(){
        return this.velo;
    }
    public float getAcc(){
        return this.acc;
    }
    public Vector3f getView(){
        return this.view;
    }
    public float getTime(){
        return this.tpf;
    }
}
