/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author johhki-4
 */
public class BuildHouses {
    AssetManager assetManager;
    public BuildHouses(AssetManager assetmanager){
        this.assetManager = assetmanager;
    }
    public Node buildHouse() {
        Node houseNode = new Node();
        
        //start of big house on hill bottom left 
        Box foundation_01 = new Box(10f, 2f, 10f);
        Material mat_found01 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_found01.setTexture("ColorMap", assetManager.loadTexture("Textures/town/metal-tiles-dirt.jpg"));
        
        Box house_01 = new Box(10f, 15f, 10f);
        Material mat_house01 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_house01.setTexture("ColorMap", assetManager.loadTexture("Textures/town/casaamarela.jpg"));
        
        Box house_02 = new Box(10f, 15f, 10f);
        Material mat_house02 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_house02.setTexture("ColorMap", assetManager.loadTexture("Textures/town/CasaRosa.jpg"));
        
        Box house_03 = new Box(10f, 10f, 10f);
        Material mat_house03 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_house03.setTexture("ColorMap", assetManager.loadTexture("Textures/town/Yellowhouse.jpg"));
        
        Box roof_01 = new Box(10f, 0.1f, 10f);
        Material mat_roof01 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_roof01.setTexture("ColorMap", assetManager.loadTexture("Textures/town/toit.jpg"));
        
        //house on top left of hill in bottom left
        Spatial found01 = new Geometry("Box", foundation_01);
        found01.setMaterial(mat_found01);
        found01.setLocalTranslation(-140.0f, 18.0f, -130.0f);
        houseNode.attachChild(found01);
        
        Spatial wall01 = new Geometry("Box", house_01);
        wall01.setMaterial(mat_house01);
        wall01.setLocalTranslation(-140.0f, 35.0f, -130.0f);
        houseNode.attachChild(wall01);        
        
        Spatial roof01 = new Geometry("Box", roof_01);
        roof01.setMaterial(mat_roof01);
        roof01.setLocalTranslation(-140.0f, 50.1f, -130.0f);
        houseNode.attachChild(roof01);
        //part 2
        Spatial found02 = new Geometry("Box", foundation_01);
        found02.setMaterial(mat_found01);
        found02.setLocalTranslation(-140.0f, 18.0f, -150.0f);
        houseNode.attachChild(found02);

        Spatial house02 = new Geometry("Box", house_01);
        house02.setMaterial(mat_house01);
        house02.setLocalTranslation(-140.0f, 35.0f, -150.0f);
        houseNode.attachChild(house02);

        Spatial roof02 = new Geometry("Box", roof_01);
        roof02.setMaterial(mat_roof01);
        roof02.setLocalTranslation(-140.0f, 50.1f, -150.0f);
        houseNode.attachChild(roof02);
        //end of big house on hill bottom left

        //start of small house on hill bottom left
        Spatial found03 = new Geometry("Box", foundation_01);
        found03.setMaterial(mat_found01);
        found03.setLocalTranslation(-175.0f, 20.0f, -65.0f);
        houseNode.attachChild(found03);

        Spatial house03 = new Geometry("Box", house_02);
        house03.setMaterial(mat_house02);
        house03.setLocalTranslation(-175.0f, 37.0f, -65.0f);
        houseNode.attachChild(house03);

        Spatial roof03 = new Geometry("Box", roof_01);
        roof03.setMaterial(mat_roof01);
        roof03.setLocalTranslation(-175.0f, 52.1f, -65.0f);
        houseNode.attachChild(roof03);
        //end small house on hill bot left;

        //start house @ "bottom"/"Middle"
        Spatial found04 = new Geometry("Box", foundation_01);
        found04.setMaterial(mat_found01);
        found04.setLocalTranslation(-170.0f, -1.0f, 30.0f);
        houseNode.attachChild(found04);
        
        Spatial house04 = new Geometry("Box", house_03);
        house04.setMaterial(mat_house03);
        house04.setLocalTranslation(-170.0f, 11.0f, 30.0f);
        houseNode.attachChild(house04);

        Spatial roof04 = new Geometry("Box", roof_01);
        roof04.setMaterial(mat_roof01);
        roof04.setLocalTranslation(-170.0f, 21.1f, 30.0f);
        houseNode.attachChild(roof04);
        //end

        Spatial found05 = new Geometry("Box", foundation_01);
        found05.setMaterial(mat_found01);
        found05.setLocalTranslation(-150.0f, -1.0f, 30.0f);
        houseNode.attachChild(found05);

        Spatial house05 = new Geometry("Box", house_03);
        house05.setMaterial(mat_house03);
        house05.setLocalTranslation(-150.0f, 11.0f, 30.0f);
        houseNode.attachChild(house05);

        Spatial house06 = new Geometry("Box", house_03);
        house06.setMaterial(mat_house03);
        house06.setLocalTranslation(-150.0f, 31.0f, 30.0f);
        houseNode.attachChild(house06);

        Spatial roof05 = new Geometry("Box", roof_01);
        roof05.setMaterial(mat_roof01);
        roof05.setLocalTranslation(-150.0f, 41.1f, 30.0f);
        houseNode.attachChild(roof05);
        
        return houseNode;
        //end
    }
}
