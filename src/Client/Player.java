/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author Andreas
 */
public class Player {

    private Vector3f velocity, rotation;
    private int id;
    private Node node, player;
    private AudioNode audio, audio_steps, audio_gun, audio_powerUp;
    private final AssetManager assetManager;
    private boolean isActive;
    private boolean moving = false;
    private Vector3f oldPos;

    public Player(AssetManager assetManager) {
        this.assetManager = assetManager;

    }

    public void createPlayer(int id, Camera cam) {
        this.node = new Node();
        this.audio = new AudioNode();
        this.id = id;
        this.player = new Node();
        this.oldPos = new Vector3f(0,0,0);
        DirectionalLight playerLight = new DirectionalLight();
        playerLight.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        this.node.addLight(playerLight);
        DirectionalLight playerLight2 = new DirectionalLight();
        playerLight2.setDirection(new Vector3f(0.1f, 1f, 1).normalizeLocal());
        this.node.addLight(playerLight2);
        this.player = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        this.player.setLocalScale(0.9f);
        this.node.setLocalTranslation(0,0,0);
        this.node.attachChild(this.player);
        this.velocity = new Vector3f(0, 0, 0);
        
        initAudio();
        this.node.attachChild(audio);
        this.isActive = true;
    }

    public Vector3f getVelo() {
        return this.velocity;
    }
    
    public Vector3f getRotation() {
        return this.rotation;
    }
    
    public AudioNode getAudioNode() {
        return audio;
    }
    public Node getPlayer(){
        return this.player;
    }
    public void setOldPos(Vector3f oldpos){
        this.oldPos = oldpos;
    }
    public Vector3f getOldPos(){
        return this.oldPos;
    }
    
    protected void initAudio(){
        //Audio for foot steps
        audio_steps = new AudioNode(assetManager, "Sounds/396014__morganpurkis__rustling-grass-1.wav", false);
        audio_steps.setPositional(true);
        audio_steps.setLooping(false);
        audio_steps.setVolume(2);
        audio.attachChild(audio_steps);

        //Audio for the gun pew pew
        audio_gun = new AudioNode(assetManager, "Sound/Effects/Gun.wav", false);
        audio_gun.setPositional(true);
        audio_gun.setLooping(false);
        audio_gun.setVolume(4);
        audio.attachChild(audio_gun);
        
        //Audio for power up
        audio_powerUp = new AudioNode(assetManager, "Sounds/smb_powerup.wav", false);
        audio_powerUp.setPositional(true);
        audio_powerUp.setLooping(false);
        audio_powerUp.setVolume(3);
        audio.attachChild(audio_powerUp);
    }
    
    public void playGun() {
        audio_gun.stop();
        audio_gun.play();
    }
    
    public void playStep() {
        audio_steps.play();
    }
    
    public void playPowerUp(){
        audio_powerUp.stop();
        audio_powerUp.play();
    }

    public void setVelo(Vector3f velocity) {
        this.velocity = velocity;
    }
    
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Node getNode() {
        return this.node;
    }

    public int getId() {
        return this.id;
    }
    
    public boolean getActive(){
        return this.isActive;
    }
    public void setActive( boolean active){
        this.isActive =active;
    }
    public void setMoving(boolean anim){
        this.moving = anim;
    }
    public boolean getMoving(){
        return this.moving;
    }

}
