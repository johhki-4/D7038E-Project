/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.PssmShadowRenderer;

/**
 *
 * @author Andreas
 */
public class Terrain {
    BuildHouses houses;
    AssetManager assetManager;
    Node terrainNode;
    ViewPort viewPort;
   public Terrain(AssetManager assetmanager,ViewPort viewPort){
       this.viewPort = viewPort;
       this.assetManager = assetmanager;
   }

    
        
    
    public Spatial initTerrain() {
        houses = new BuildHouses(assetManager);
        Spatial model = assetManager.loadModel("Scenes/ShootyBang2.j3o");
        model.setLocalScale(1f);
       // terrainNode.attachChild(houses.buildHouse());
        setupLight(model);
        
        return model;
    }
    public void setupLight(Spatial scene){
        DirectionalLight sun = new DirectionalLight();
        Vector3f lightDir = new Vector3f(-0.12f, -0.3729129f, -0.94847335f); //-0.1f, -20f, -0.9f
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(1.2f));
        scene.addLight(sun);
        
        
        PssmShadowRenderer pssmRenderer = new PssmShadowRenderer(assetManager,1024,4);
        pssmRenderer.setDirection(lightDir);
        pssmRenderer.setShadowIntensity(0.85f);
        
        DirectionalLight fakesun = new DirectionalLight();
        Vector3f lightDir2 = new Vector3f(-0.1f, -20f, 0.9f); //-0.1f, -20f, -0.9f
        fakesun.setDirection(lightDir2);
        fakesun.setColor(ColorRGBA.White.clone().multLocal(1f));
        scene.addLight(fakesun);
        
        
        PssmShadowRenderer pssmRenderer2 = new PssmShadowRenderer(assetManager,1024,4);
        pssmRenderer2.setDirection(lightDir2);
        pssmRenderer2.setShadowIntensity(0.55f);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

        Vector3f lightPos = lightDir.multLocal(-3000);
        LightScatteringFilter filter = new LightScatteringFilter(lightPos);
        fpp.addFilter(filter);

        //viewPort.addProcessor(fpp);
    }
}
