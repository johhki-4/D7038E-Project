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
 * @author Andreas,Johannes, Oscar
 */
@Serializable(id=0)
public class ConnectionMessageReq extends AbstractMessage {
    
    public ConnectionMessageReq(){
        
    }
    
   
}
