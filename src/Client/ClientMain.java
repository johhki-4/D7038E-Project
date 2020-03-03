/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import PowerUps.*;
import Messages.*;

import Servers.GlobalUtility;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;

/**
 *
 * @author johhki-4
 */
public class ClientMain extends SimpleApplication implements AnalogListener, AnimEventListener {

    private Client authServer, gameServer;
    private int HP = GlobalUtility.MAX_HEALTH;
    private int assignedId = -1, key = -1;
    private int currentAmmo = GlobalUtility.MAX_AMMO;
    private int kills = 0;
    private int deaths = 0;
    private boolean fallDamage;
    private boolean healthpackInit = false;
    private boolean playerInit = false;
    private boolean isRunning = false;
    private boolean shootAllowed = true;
    private boolean dmgIndicator = false;
    private boolean gainHP = false;
    private boolean mute = false;
    private boolean serverInit = false;
    private boolean attachedMuzzle = false;
    private float muzzleTime = 0.2f;
    private Picture flash;
    private float shootTimer = 0f;
    private float airbornTime = 0f;
    private float updateTimer = 0f;
    private float dmgTimer = 0f;
    private float gainHPTimer = 0f;
    private final float speed = 0.5f;
    ArrayList<PowerUps> PUList;
    ArrayList<Picture> healthbar;
    ArrayList<Vector3f> PULoc;
    ArrayList<String> eventLog;

    BitmapText ammoLeft;
    BitmapText deadScreen;
    BitmapText KD;
    Terrain terrain;
    Node healthPack;
    Node Hitable;
    Node houses;
    Node PlayerNode;
    AudioNode lobby_music;
    AudioNode audio_steps;
    AudioNode audio_gun;
    AudioNode audio_nature;
    AudioNode audio_powerUp;
    CharacterControl player;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CapsuleCollisionShape capsuleShape;
    Vector3f direction = new Vector3f();
    Vector3f oldPos = new Vector3f();

    HashMap<Integer, Player> PlayerList;
    HashMap<Integer, AnimChannel> channelList;
    HashMap<Integer, AnimControl> controlList;

    Random rnd = new Random();
    private final Vector3f walkDirection = new Vector3f();
    private Vector3f velocity = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;

    private final Vector3f camDir = new Vector3f();
    private final Vector3f camLeft = new Vector3f();

    public static void main(String[] args) {
        GlobalUtility.initSeri();
        Logger.getLogger("com.jme3").setLevel(SEVERE);
        ClientMain app = new ClientMain();

        app.start();
    }

    @Override
    public void simpleInitApp() {
        reset();
        setDisplayFps(false);

        setDisplayStatView(false);
        Picture pic = new Picture("Lobby");
        pic.setHeight(settings.getHeight());
        pic.setWidth(settings.getWidth());
        pic.setImage(assetManager, "Textures/Lobby/lobby.png", true);
        pic.setPosition(0, 0);
        guiNode.attachChild(pic);
        //Lobby sound
        lobby_music = new AudioNode(assetManager, "Sounds/Elevator Music.wav", true);
        lobby_music.setPositional(false);
        lobby_music.setLooping(true);
        lobby_music.setVolume(10f);
        rootNode.attachChild(lobby_music);
        lobby_music.play();

        inputManager.addMapping("join", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addListener(actionListener, "join");

    }

    public void connectToServer() {

        try {
            ClientListener listener1 = new ClientListener(this);
            authServer = Network.connectToServer(GlobalUtility.AUTH_SERVER, GlobalUtility.AUTH_PORT);
            authServer.addMessageListener(listener1, WelcomeBackMessage.class, ReconnectMessage.class, NotAllowedMessage.class, KeyAckMessage.class, ConnectionMessageReq.class, ConnectionApprovedMessage.class, SpawnPUMessage.class, PlayerRespawnMessage.class, RemovePUMessage.class, StartUpMessage.class, updateMessage.class, PlayerDisconnectedMessage.class, shootMessage.class, WalkingMessage.class, PicktPU.class);
            authServer.addClientStateListener(new AuthConnectionListener());
            authServer.start();

            gameServer = Network.connectToServer(GlobalUtility.GAME_SERVER, GlobalUtility.GAME_PORT);
            gameServer.addClientStateListener(new GameServerConnListener());
            gameServer.addMessageListener(listener1, WelcomeBackMessage.class, ReconnectMessage.class, NotAllowedMessage.class, KeyAckMessage.class, ConnectionMessageReq.class, ConnectionApprovedMessage.class, SpawnPUMessage.class, PlayerRespawnMessage.class, RemovePUMessage.class, StartUpMessage.class, updateMessage.class, PlayerDisconnectedMessage.class, shootMessage.class, WalkingMessage.class, PicktPU.class);
            gameServer.start();
            ConnectionMessageReq message = new ConnectionMessageReq();
            authServer.send(message);
            serverInit = true;

        } catch (IOException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initGame(Vector3f loc) {
        guiNode.detachAllChildren();
        reset();
        stateManager.attach(bulletAppState);
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
        flyCam.setMoveSpeed(speed);
        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);
        cam.update();
        PULoc();
        initKeys();
        initHealthBar();
        initCrossHairs();
        initGUIHand();
        initAmmoBar();
        initKeys();
        initAudio();
        initTerrain(loc);
        initKD();
        lobby_music.stop();
        isRunning = true;
        Hitable.attachChild(PlayerNode);
        SetupMessage mess = new SetupMessage(assignedId, key);
        gameServer.send(mess);
    }

    @Override
    public void simpleUpdate(float tpf) {

        if (isRunning) {
            if (HP <= 0) {
                flyCam.setMoveSpeed(0f);
                player.setEnabled(false);
            }
            if (playerInit) {

                for (Map.Entry<Integer, Player> entry : PlayerList.entrySet()) {
                    movePlayer(entry.getValue(), tpf);

                }

            }

            updateTimer = updateTimer + tpf;
            if (updateTimer >= GlobalUtility.UPDATE_RATE) {
                updateTimer = 0f;

                updateMessage message = new updateMessage(assignedId, HP, player.getPhysicsLocation(), velocity);
                gameServer.send(message);
            }

            if (healthpackInit) {
                healthPack.rotate(0, 0, 2f * tpf);
                for (int i = 0; i < PUList.size(); i++) {
                    PUList.get(0).addTime(tpf);
                    if (checkPickup(i)) {
                        break;
                    }

                    if (PUList.get(i).getTime() >= PUList.get(i).activeTime()) {
                        Hitable.detachChild(healthPack);
                        healthpackInit = false;
                        PUList.remove(i);
                    }
                }
            }

            if (player.isEnabled()) {
                if (!player.onGround()) {
                    airbornTime = airbornTime + tpf;
                    if (airbornTime > GlobalUtility.FALL_DMG_THRESHOLD) {
                        fallDamage = true;
                        airbornTime = 0;
                    }
                } else {
                    airbornTime = 0f;
                }

                if (fallDamage && player.onGround()) {
                    fallDamage = false;

                    HP--;
                    FallDmgMessage message = new FallDmgMessage(assignedId, HP);
                    gameServer.send(message);
                    removeHealth();

                }
                if (!shootAllowed) {
                    shootTimer = shootTimer + tpf;

                }
                if (shootTimer >= GlobalUtility.SHOOT_CD) {
                    shootTimer = 0f;
                    shootAllowed = true;
                }
                if (attachedMuzzle = true) {
                    muzzleTime = muzzleTime - tpf;
                }
                if (muzzleTime <= 0 && attachedMuzzle == true) {
                    guiNode.detachChild(flash);
                    attachedMuzzle = false;
                    muzzleTime = 0.2f;
                }
                camDir.set(cam.getDirection()).multLocal(0.6f);
                camLeft.set(cam.getLeft()).multLocal(0.4f);
                walkDirection.set(0, 0, 0);
                if (left) {
                    walkDirection.addLocal(camLeft.mult(speed));
                    if (!mute) {
                        for (int i = 0; i < 40; i++) {
                            audio_steps.play();
                        }
                    }
                }
                if (right) {
                    walkDirection.addLocal(camLeft.negate().mult(speed));
                    if (!mute) {
                        for (int i = 0; i < 40; i++) {
                            audio_steps.play();
                        }
                    }
                }
                if (up) {
                    walkDirection.addLocal(camDir.mult(speed));
                    if (!mute) {
                        for (int i = 0; i < 40; i++) {
                            audio_steps.play();
                        }
                    }
                }
                if (down) {
                    walkDirection.addLocal(camDir.negate().mult(speed));
                    if (!mute) {
                        for (int i = 0; i < 40; i++) {
                            audio_steps.play();
                        }
                    }
                }
                velocity = walkDirection;
                player.setWalkDirection(walkDirection);
                WalkingMessage msg = new WalkingMessage(assignedId, player.getPhysicsLocation(), velocity, 1f, player.getViewDirection(), tpf);

                gameServer.send(msg);
                cam.setLocation(player.getPhysicsLocation());
                oldPos = player.getPhysicsLocation();
                if (dmgIndicator) {
                    dmgTimer += tpf;
                    if (dmgTimer > GlobalUtility.DMG_CD) {
                        guiNode.detachChildNamed("dmg");
                        dmgTimer = 0f;
                        if (guiNode.getChild("dmg") != null) {

                        } else {
                            dmgIndicator = false;
                        }
                    }
                }
                if (gainHP) {
                    gainHPTimer += tpf;
                    if (gainHPTimer > GlobalUtility.HP_GAIN) {
                        guiNode.detachChildNamed("gainHP");
                        gainHPTimer = 0f;
                        if (guiNode.getChild("gainHP") != null) {

                        } else {
                            gainHP = false;
                        }
                    }
                }
            }

        }

    }

    public void movePlayer(Player player, float tpf) {

        if (player.getActive()) {
            player.setOldPos(player.getNode().getLocalTranslation());
            float posx = player.getNode().getLocalTranslation().x + player.getVelo().x * tpf;
            float posy = player.getNode().getLocalTranslation().y + player.getVelo().y * tpf;
            float posz = player.getNode().getLocalTranslation().z + player.getVelo().z * tpf;
            player.getNode().setLocalTranslation(new Vector3f(posx, posy, posz));
            player.getNode().lookAt(cam.getLocation(), new Vector3f(0, 1, 0));
        }
    }

    public void reset() {
        player = new CharacterControl();
        eventLog = new ArrayList();
        healthbar = new ArrayList();
        PUList = new ArrayList();
        PULoc = new ArrayList();
        Hitable = new Node();
        houses = new Node();
        PlayerNode = new Node();
        bulletAppState = new BulletAppState();
        PlayerList = new HashMap<>();
        channelList = new HashMap<>();
        controlList = new HashMap<>();

    }

    public void initTerrain(Vector3f loc) {
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        terrain = new Terrain(assetManager, viewPort);
        Spatial model = terrain.initTerrain();
        Hitable.attachChild(model);
        Hitable.attachChild(terrain.houses.buildHouse());
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) Hitable);
        landscape = new RigidBodyControl(sceneShape, 0);
        model.addControl(landscape);
        rootNode.attachChild(model);
        bulletAppState.getPhysicsSpace().addAll(model);
        capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);

        bulletAppState.getPhysicsSpace().add(landscape);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(30);
        player.setFallSpeed(50);
        player.setGravity(60);
        player.setPhysicsLocation(loc);
        bulletAppState.getPhysicsSpace().add(player);
        rootNode.attachChild(Hitable);
    }

    public boolean checkPickup(int i) {
        double C1C2 = Math.sqrt(Math.pow((player.getPhysicsLocation().x - PUList.get(i).getNode().getLocalTranslation().x), 2) + Math.pow((player.getPhysicsLocation().z - PUList.get(i).getNode().getLocalTranslation().z), 2));
        if ((C1C2 == 2 + PUList.get(i).getRadius()) || (C1C2 < 2 + PUList.get(i).getRadius())) {
            HP++;
            if (HP >= 10) {
                HP = 10;
            }
            addHealth();
            HP++;
            if (HP >= 10) {
                HP = 10;
            }
            addHealth();
            HP++;
            if (HP >= 10) {
                HP = 10;
            }
            addHealth();
            PicktPU message = new PicktPU(assignedId, key);
            if (!mute) {
                audio_powerUp.play();
            }
            gameServer.send(message);
            return true;
        }
        return false;
    }

    public void removePU(int i) {
        if (healthpackInit) {
            Hitable.detachChild(healthPack);
            healthpackInit = false;
            PUList.remove(i);
        }
    }

    public void PULoc() {
        PULoc.add(new Vector3f(20f, 67f, 8f)); //On top of mountain
        PULoc.add(new Vector3f(-180f, 27f, -90f)); //The houses on the hill
        PULoc.add(new Vector3f(80f, -23f, -140f)); //The boat
        PULoc.add(new Vector3f(-115f, -7f, 148f)); //The Sandpile
        PULoc.add(new Vector3f(180f, 3f, 180f)); //behind the large pit
    }

    public void updateAmmo() {
        guiNode.detachChild(ammoLeft);
        ammoLeft = new BitmapText(guiFont, false);
        ammoLeft.setSize(60);
        ammoLeft.setColor(ColorRGBA.Red);
        ammoLeft.setText(currentAmmo + "/" + GlobalUtility.MAX_AMMO);
        ammoLeft.setLocalTranslation(1770, 100, 0);
        guiNode.attachChild(ammoLeft);
    }

    private void updateKD() {
        guiNode.detachChild(KD);
        KD = new BitmapText(guiFont, false);
        KD.setSize(60);
        KD.setColor(ColorRGBA.Red);
        KD.setText("Kills: " + kills + " " + "Deaths: " + deaths);
        KD.setLocalTranslation(1450, settings.getHeight(), 0);
        guiNode.attachChild(KD);

    }

    private void initKeys() {
        inputManager.deleteMapping("join");
        inputManager.addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("reload", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Mute", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Up");
        inputManager.addListener(actionListener, "Down");
        inputManager.addListener(actionListener, "Jump");
        inputManager.addListener(actionListener, "shoot", "reload", "Mute");

    }

    protected void initAudio() {

        //Audio for foot steps
        audio_steps = new AudioNode(assetManager, "Sounds/396014__morganpurkis__rustling-grass-1.wav", false);
        audio_steps.setPositional(true);
        audio_steps.setLooping(false);
        audio_steps.setVolume(10f);
        rootNode.attachChild(audio_steps);

        //Audio for the gun pew pew
        audio_gun = new AudioNode(assetManager, "Sound/Effects/Gun.wav", false);
        audio_gun.setPositional(true);
        audio_gun.setLooping(false);
        audio_gun.setVolume(10);
        guiNode.attachChild(audio_gun);

        audio_nature = new AudioNode(assetManager, "Sound/Environment/Nature.ogg", true);
        audio_nature.setLooping(true);  // activate continuous playing
        audio_nature.setPositional(false);
        audio_nature.setVolume(7);
        rootNode.attachChild(audio_nature);
        bgMusic();

        //Audio for power up
        audio_powerUp = new AudioNode(assetManager, "Sounds/smb_powerup.wav", false);
        audio_powerUp.setPositional(true);
        audio_powerUp.setLooping(false);
        audio_powerUp.setVolume(20);
        guiNode.attachChild(audio_powerUp);

    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("shoot") && isPressed) {
                if (player.isEnabled()) {
                    if (shootAllowed) {
                        if (currentAmmo > 0) {
                            audio_gun.stop();
                            currentAmmo = currentAmmo - 1;

                            shoot();
                            updateAmmo();
                            try {
                                guiNode.attachChild(flash);
                                attachedMuzzle = true;
                            } catch (NullPointerException e) {

                            }

                            if (!mute) {
                                audio_gun.play();
                            }
                            shootAllowed = false;

                        }
                    }
                }
            }
            if (name.equals("reload") && isPressed) {
                shootAllowed = true;
                shootTimer = 0f;
                currentAmmo = GlobalUtility.MAX_AMMO;
                updateAmmo();
            }
            if (name.equals("Mute") && isPressed) {
                mute = !mute;
                bgMusic();
            }
            if (name.equals("join") && isPressed) {
                guiNode.detachChildNamed("Lobby");
                Picture pic = new Picture("LobbyInit");
                pic.setHeight(settings.getHeight());
                pic.setWidth(settings.getWidth());
                pic.setImage(assetManager, "Textures/Lobby/lobbyInit.png", true);
                pic.setPosition(0, 0);
                guiNode.attachChild(pic);

                connectToServer();
            }

            switch (name) {
                case "Left":
                    left = isPressed;
                    break;
                case "Right":
                    right = isPressed;
                    break;
                case "Up":
                    up = isPressed;
                    break;
                case "Down":
                    down = isPressed;
                    break;
                case "Jump":
                    if (isPressed) {
                        player.jump();
                    }
                    break;
                default:
                    break;
            }

        }
    };

    protected void initGUIHand() {
        Picture pic = new Picture("HUD Arm");
        pic.setImage(assetManager, "Textures/guihands/hands2.png", true);
        pic.setWidth(settings.getWidth());
        pic.setHeight(settings.getHeight());
        pic.setPosition(0, 0);
        guiNode.attachChild(pic);
        flash = new Picture("muzzleFlash");
        flash.setImage(assetManager, "Textures/guihands/shoothands.png", true);
        flash.setWidth(settings.getWidth());
        flash.setHeight(settings.getHeight());
        flash.setPosition(0, 0);
    }

    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 4);
        ch.setText("+");
        ch.setLocalTranslation(settings.getWidth() / 2 - ch.getLineWidth() / 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
    }

    protected void initHealthBar() {
        int k = 0;
        for (int i = 0; i < GlobalUtility.MAX_HEALTH; i++) {
            Picture pic = new Picture("HealthBar");
            pic.setImage(assetManager, "Textures/fullHeart.png", true);
            pic.setWidth(30);
            pic.setHeight(30);
            pic.setPosition(k, settings.getHeight() - 30);
            healthbar.add(pic);
            guiNode.attachChild(pic);
            k = k + 50;
        }
    }

    private void bgMusic() {
        if (mute) {
            audio_nature.stop();
        } else {
            audio_nature.play();
        }
    }

    protected void initAmmoBar() {
        ammoLeft = new BitmapText(guiFont, false);
        ammoLeft.setSize(60);
        ammoLeft.setColor(ColorRGBA.Red);
        ammoLeft.setText(currentAmmo + "/" + GlobalUtility.MAX_AMMO);
        ammoLeft.setLocalTranslation(1770, 100, 0);
        guiNode.attachChild(ammoLeft);

    }

    protected void removeHealth() {
        if (HP >= 0) {
            if (HP <= GlobalUtility.MAX_HEALTH - 1) {
                healthbar.get(HP).setImage(assetManager, "Textures/emptyHeart.png", true);
                //HP--;
                updateTimer = 0f;
                updateMessage message = new updateMessage(assignedId, HP, player.getPhysicsLocation(), velocity);
                gameServer.send(message);

            } else if (HP <= GlobalUtility.MAX_HEALTH && HP > 0) {
                healthbar.get(HP - 1).setImage(assetManager, "Textures/emptyHeart.png", true);
                //HP--;
                updateTimer = 0f;
                updateMessage message = new updateMessage(assignedId, HP, player.getPhysicsLocation(), velocity);
                gameServer.send(message);

            } else {
                healthbar.get(healthbar.size() - 1).setImage(assetManager, "Textures/emptyHeart.png", true);
                HP = 0;
                updateTimer = 0f;
                updateMessage message = new updateMessage(assignedId, HP, player.getPhysicsLocation(), velocity);
                gameServer.send(message);
            }

        } else {
            HP = 0;
        }

        Picture pic = new Picture("dmg");
        pic.setHeight(settings.getHeight());
        pic.setWidth(settings.getWidth());
        pic.setImage(assetManager, "Textures/dmgindicator.png", true);
        pic.setPosition(0, 0);
        guiNode.attachChild(pic);
        dmgIndicator = true;
    }

    protected void addHealth() {
        if (HP <= GlobalUtility.MAX_HEALTH && HP > 0) {
            healthbar.get(HP - 1).setImage(assetManager, "Textures/fullHeart.png", true);
            updateTimer = 0f;
            updateMessage message = new updateMessage(assignedId, HP, player.getPhysicsLocation(), velocity);
            gameServer.send(message);

        } else {
            healthbar.get(healthbar.size()).setImage(assetManager, "Textures/fullHeart.png", true);
            HP = 0;
            updateTimer = 0f;
            updateMessage message = new updateMessage(assignedId, HP, player.getPhysicsLocation(), velocity);
            gameServer.send(message);
        }
        Picture pic = new Picture("gainHP");
        pic.setHeight(settings.getHeight());
        pic.setWidth(settings.getWidth());
        pic.setImage(assetManager, "Textures/gainHP.png", true);
        pic.setPosition(0, 0);
        guiNode.attachChild(pic);
        gainHP = true;

    }

    private void initKD() {
        KD = new BitmapText(guiFont, false);
        KD.setSize(60);
        KD.setColor(ColorRGBA.Red);
        KD.setText("Kills: " + kills + " " + "Deaths: " + deaths);
        KD.setLocalTranslation(1450, settings.getHeight(), 0);
        guiNode.attachChild(KD);
    }

    protected void shoot() {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        if (Hitable.collideWith(ray, results) < 1) {
            shootMessage msg = new shootMessage(assignedId, key, new Vector3f(0, 0, 0));
            gameServer.send(msg);
            return;
        }

        Vector3f temp = results.getClosestCollision().getContactPoint();
        Node parent = results.getClosestCollision().getGeometry().getParent();
        Vector3f local = new Vector3f();
        parent.worldToLocal(temp, local);
        shootMessage msg = new shootMessage(assignedId, key, temp);
        gameServer.send(msg);

    }

    private void checkConvergence(Vector3f calcPos, int id) {
        if (id == assignedId) {
            boolean close;
            if (oldPos.x + 1 >= calcPos.x && oldPos.x - 1 <= calcPos.x) {
                close = oldPos.z + 1 >= calcPos.z && oldPos.z - 1 <= calcPos.z;

            } else {
                close = false;

            }
            if (!close) { // if the calculated pos seems to diffrent from the client position
                beizerCurve(calcPos, id); // converge to the correct pos using linear beizerCurve mehtod
            }
        } else {
            beizerCurve(calcPos, id);
        }

    }

    private void beizerCurve(Vector3f calcPos, int id) {
        try {
            if (id == assignedId) {
                //moves the player to correct pos using small linear steps
                float t = 0;
                while (t <= 1f) {
                    float x = oldPos.x + t * (calcPos.x - oldPos.x);
                    float y = oldPos.y + t * (calcPos.y - oldPos.y);
                    float z = oldPos.z + t * (calcPos.z - oldPos.z);
                    player.setPhysicsLocation(new Vector3f(x, y, z));
                    t = t + 0.01f;
                }
            } else {
                if (playerInit) {
                    float t = 0;
                    while (t <= 1f) {
                        Vector3f oldPos2 = PlayerList.get(id).getNode().getLocalTranslation();
                        float x = oldPos2.x + t * (calcPos.x - oldPos2.x);
                        float y = oldPos2.y + t * (calcPos.y - oldPos2.y);
                        float z = oldPos2.z + t * (calcPos.z - oldPos2.z);
                        PlayerList.get(id).getNode().setLocalTranslation(new Vector3f(x, y, z));
                        t = t + 0.01f;
                    }

                }
            }
        } catch (NullPointerException e) {
        }

    }

    protected void healthPack(Vector3f pos, float time) {
        if (!healthpackInit) {
            healthPack = new Node();
            HealthPU PU = new HealthPU(assetManager, time);
            healthPack = PU.buildPowerUp(pos);
            PUList.add(PU);
            Hitable.attachChild(healthPack);
            rootNode.attachChild(Hitable);
            healthpackInit = true;
            System.out.println("spawnd healtpack at" + pos);
        }
    }

    private void removePlayer(int id) {

        PlayerNode.detachAllChildren();
        controlList.remove(id);
        channelList.remove(id);
        PlayerList.remove(id);
        try {
            for (Map.Entry<Integer, Player> entry : PlayerList.entrySet()) {
                if (entry.getKey() != id) {

                    SpawnPlayer(entry.getValue().getNode().getLocalTranslation(), entry.getKey());
                }
            }
        } catch (ConcurrentModificationException e) {
        }
    }

    private void SpawnPlayer(Vector3f spawnLoc, int id) {

        if (id == assignedId) {
            //System.out.println("spawn player at " + spawnLoc);
            player.setEnabled(true);
            player.setPhysicsLocation(spawnLoc);
            flyCam.setMoveSpeed(speed);
            currentAmmo = GlobalUtility.MAX_AMMO;
            updateAmmo();
            for (int i = 0; i < healthbar.size(); i++) {
                healthbar.get(i).setImage(assetManager, "Textures/fullHeart.png", true);

            }
            HP = GlobalUtility.MAX_HEALTH;

        } else {
            PlayerList.remove(id);
            //System.out.println("spawn opponent at " + spawnLoc);
            Player player = new Player(assetManager);
            player.createPlayer(id, cam);
            player.getNode().setLocalTranslation(spawnLoc);
            PlayerList.put(id, player);
            controlList.put(id, PlayerList.get(id).getPlayer().getControl(AnimControl.class));
            controlList.get(id).addListener(this);
            channelList.put(id, controlList.get(id).createChannel());
            channelList.get(id).setAnim("stand");
            PlayerNode.attachChild(player.getNode());
            playerInit = true;
        }
    }

    public void respawnPlayer(int id, Vector3f loc) {

        if (id == assignedId) {
            deaths++;
            updateKD();
            SpawnPlayer(loc, id);
        } else {
            PlayerNode.detachAllChildren();
            try {
                for (Map.Entry<Integer, Player> entry : PlayerList.entrySet()) {
                    if (entry.getKey() != id) {

                        SpawnPlayer(entry.getValue().getNode().getLocalTranslation(), entry.getKey());
                    }
                }
            } catch (ConcurrentModificationException e) {
            }

            SpawnPlayer(loc, id);

        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {

    }

    public void onAction(String binding, boolean isPressed, float tpf) {

    }

    @Override
    public void destroy() {
        if (serverInit) {
            authServer.close();
            gameServer.close();

        }
        super.destroy();
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if (animName.equals("Walk")) {
            channel.setAnim("stand", 0.50f);
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setSpeed(1f);
        }

    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        //Unused :(
    }

    public class AuthConnectionListener implements ClientStateListener {

        @Override
        public void clientConnected(Client c) {
            System.out.println("Connected To Authserver");
        }

        @Override
        public void clientDisconnected(Client c, DisconnectInfo info) {
            System.out.println("Disconnected from AuthServer");
        }

    }

    public class GameServerConnListener implements ClientStateListener {

        @Override
        public void clientConnected(Client c) {
            System.out.println("Connected to gameServer");
        }

        @Override
        public void clientDisconnected(Client c, DisconnectInfo info) {
            System.out.println("Disconnected from gameServer");
        }

    }

    public class ClientListener implements MessageListener<Client> {

        private final ClientMain client;

        public ClientListener(ClientMain client) {
            this.client = client;
        }

        @Override
        public void messageReceived(Client source, Message m) {
            if (m instanceof ConnectionApprovedMessage) {
                System.out.println("ConnectionApprecevied");
                final ConnectionApprovedMessage msg = (ConnectionApprovedMessage) m;
                assignedId = msg.getId();
                System.out.println("your id is " + assignedId);
                key = msg.getKey();

                System.out.println("Your key is" + key);
                this.client.enqueue(new Runnable() {
                    @Override
                    public void run() {

                        client.initGame(msg.getSpawnLoc());
                    }
                });

            } else if (m instanceof NotAllowedMessage) {
                NotAllowedMessage msg = (NotAllowedMessage) m;
                System.out.println("Client was not allowed to enter the game.");
                ConnectionMessageReq message = new ConnectionMessageReq();
                authServer.send(message);

            }
            if (m instanceof SpawnPUMessage) {
                final SpawnPUMessage msg = (SpawnPUMessage) m;
                this.client.enqueue(new Runnable() {
                    @Override
                    public void run() {

                        healthPack(PULoc.get(msg.getLocId()), msg.getTime());
                    }
                });
            }
            if (m instanceof PlayerRespawnMessage) {
                final PlayerRespawnMessage msg = (PlayerRespawnMessage) m;
                this.client.enqueue(new Runnable() {
                    @Override
                    public void run() {
                        if (msg.getKiller() == client.assignedId && msg.getId() != client.assignedId) {
                            client.kills++;
                            updateKD();
                        }
                        respawnPlayer(msg.getId(), msg.getLoc());
                    }
                });
            }
            if (m instanceof RemovePUMessage) {
                this.client.enqueue(new Runnable() {
                    @Override
                    public void run() {

                        removePU(0);

                    }
                });
            }
            if (m instanceof StartUpMessage) {
                final StartUpMessage msg = (StartUpMessage) m;
                if (msg.getId() != client.assignedId) {
                    this.client.enqueue(new Runnable() {
                        @Override
                        public void run() {
                            respawnPlayer(msg.getId(), msg.getLoc());
                        }
                    });
                } else {
                }

            }
            if (m instanceof updateMessage) {
                final updateMessage msg = (updateMessage) m;
                if (msg.getId() == client.assignedId) {
                    if (msg.getHp() < client.HP) {
                        client.HP = msg.getHp();
                        this.client.enqueue(new Runnable() {
                            @Override
                            public void run() {
                                removeHealth();
                            }
                        });
                    }
                    this.client.enqueue(new Runnable() {
                        @Override
                        public void run() {
                            checkConvergence(msg.getLoc(), msg.getId()); // check if calc is close to pos
                        }
                    });
                } else {
                    if (client.playerInit) {
                        this.client.enqueue(new Runnable() {
                            @Override
                            public void run() {
                                checkConvergence(msg.getLoc(), msg.getId());
                            }
                        });

                    }
                }

            }
            if (m instanceof PlayerDisconnectedMessage) {
                final PlayerDisconnectedMessage msg = (PlayerDisconnectedMessage) m;
                this.client.enqueue(new Runnable() {
                    @Override
                    public void run() {
                        removePlayer(msg.getId());
                    }
                });
            }

            if (m instanceof WalkingMessage) {
                final WalkingMessage msg = (WalkingMessage) m;
                if (msg.getid() != assignedId) {

                    this.client.enqueue(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (playerInit) {
                                    client.PlayerList.get(msg.getid()).setVelo(msg.getVelo());
                                    client.PlayerList.get(msg.getid()).setRotation(msg.getView());
                                    if (!mute) {
                                        client.PlayerList.get(msg.getid()).playStep();

                                    }
                                    if (!channelList.get(msg.getid()).getAnimationName().equals("Walk")) {
                                        channelList.get(msg.getid()).setAnim("Walk", 0.50f);
                                        channelList.get(msg.getid()).setLoopMode(LoopMode.Loop);
                                        // System.out.println("walking");
                                    }
                                }
                            } catch (NullPointerException e) {
                            }
                        }
                    });

                }
            }

            if (m instanceof shootMessage) {
                final shootMessage msg = (shootMessage) m;
                if (msg.getId() != assignedId) {
                    this.client.enqueue(new Runnable() {
                        @Override
                        public void run() {
                            if (playerInit && !mute) {
                                client.PlayerList.get(msg.getId()).playGun();

                            }
                        }
                    });
                }

            }
            if (m instanceof PicktPU) {
                final PicktPU msg = (PicktPU) m;
                if (msg.getId() != assignedId) {
                    this.client.enqueue(new Runnable() {
                        @Override
                        public void run() {
                            if (playerInit && !mute) {

                                client.PlayerList.get(msg.getId()).playPowerUp();

                            }
                        }
                    });
                }
            }
        }
    }

}
