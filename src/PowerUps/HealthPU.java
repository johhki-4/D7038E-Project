/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PowerUps;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author Andreas
 */
public class HealthPU extends PowerUps{
    
    public HealthPU(AssetManager assetManager,float time) {
        super(assetManager,time);
        color = ColorRGBA.Green;
    }
    
}
