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
@Serializable(id=17)
public class FallDmgMessage extends AbstractMessage {
    
    private int id;
    private int HP;
    public FallDmgMessage(){}
    public FallDmgMessage(int id, int HP) {
        this.id = id;
        this.HP = HP;
    }
    
    public int getId(){
        return this.id;
    }
    public int getHP(){
        return this.HP;
    }
}
