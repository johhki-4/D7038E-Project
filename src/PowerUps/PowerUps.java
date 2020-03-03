/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PowerUps;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.texture.Texture;

/**
 *
 * @author Andreas
 */
public abstract class PowerUps {
    float timeActive;
    Node PUNode;
    Geometry geoCylinder, geoCylinder2;
    Material matCylinder, matCylinder2;
    AssetManager assetManager;
    float time;
    ColorRGBA color;
    float radius = 3;

    public PowerUps(AssetManager assetManager,float Activetime) {
        this.assetManager = assetManager;
        this.timeActive = Activetime;
    }
    
    
    public Node buildPowerUp(Vector3f pos){
        PUNode = new Node();
        
        
        Cylinder cylinder = new Cylinder(15, 15, radius, 6, true); 
        geoCylinder = new Geometry("Cylinder", cylinder);  
        matCylinder = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  
        matCylinder.setColor("Color", new ColorRGBA(0, 1, 0, 0.6f));
        matCylinder.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geoCylinder.setMaterial(matCylinder);
        geoCylinder.setQueueBucket(Bucket.Transparent);
        PUNode.attachChild(geoCylinder);
        
        Cylinder cylinder2 = new Cylinder(15, 15, 3, 6, true); 
        geoCylinder2 = new Geometry("Cylinder2", cylinder2);  
        matCylinder2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matCylinder2.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        Texture hearts = assetManager.loadTexture("Textures/newTry.png");
        matCylinder2.setTexture("ColorMap", hearts);
        geoCylinder2.setMaterial(matCylinder2);
        geoCylinder2.setQueueBucket(Bucket.Transparent);
        PUNode.attachChild(geoCylinder2);
        
        PUNode.scale(0.5f);
        PUNode.rotate(90*FastMath.DEG_TO_RAD, 0, 0);
        PUNode.setLocalTranslation(pos);
        return PUNode;
    }
    public float getRadius(){
        return this.radius;
    }
    public Node getNode(){
        return this.PUNode;
    }
    public float getTime(){
        return this.time;
    }
    public float activeTime(){
        return this.timeActive;
    }
    public void addTime(float tpf){
        this.time = this.time+tpf;
    }

}
    

  
