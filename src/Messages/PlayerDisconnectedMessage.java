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

@Serializable(id=14)
public class PlayerDisconnectedMessage extends AbstractMessage{
    int id;
    
    PlayerDisconnectedMessage(){}

    public PlayerDisconnectedMessage(int id) {
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
}
