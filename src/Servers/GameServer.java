/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servers;

import Messages.*;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

/**
 *
 * @author Andreas
 */
public class GameServer extends SimpleApplication implements ConnectionListener {

    private Server server;
    float PUtimer;
    Random rnd = new Random();
    ArrayList<Vector3f> PULoc;
    ArrayList<Vector3f> SpawnLoc;
    ArrayList<PlayerRespawnMessage> respawnList = new ArrayList();
    Vector3f ActivePULoc = new Vector3f();
    float ActivePUTime;
    int ActivePuInt;
    boolean isRunning = false;
    float updateTimer = 0f;
    final HashMap<Integer, InsideKeyPair> map = new HashMap<>();
    final HashMap<Integer, Vector3f> PlayerLoc = new HashMap<>();
    final HashMap<Integer, Integer> PlayerHP = new HashMap<>();
    final HashMap<Integer, Vector3f> PlayerVelo = new HashMap<>();

    public static void main(String[] args) {
        GlobalUtility.initSeri();
        java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
        GameServer app = new GameServer();
        app.start(JmeContext.Type.Headless);
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void simpleInitApp() {
        PULoc = new ArrayList();
        SpawnLoc = new ArrayList();
        SpawnLoc();
        PULoc();
        try {
            server = Network.createServer(GlobalUtility.GAME_PORT);
            initListener();
            server.addConnectionListener(this);
            server.start();

        } catch (IOException ex) {
            ex.printStackTrace();
            destroy();
        }
        System.out.println("GameServer is up and running");

        isRunning = true;
    }

    @Override
    public void destroy() {
        server.close();
        super.destroy();
    }

    @Override
    public void connectionAdded(Server server, HostedConnection conn) {

        System.out.println("A client has connected");
    }

    @Override
    public void connectionRemoved(Server server, HostedConnection conn) {
        System.out.println("A client has disconnected");
    }

    public void SpawnLoc() {
        SpawnLoc.add(new Vector3f(15f, 67f, 2f)); //On top of mountain
        SpawnLoc.add(new Vector3f(-160f, 27f, -80f)); //The houses on the hill
        SpawnLoc.add(new Vector3f(80f, -25f, -130f)); //The boat
        SpawnLoc.add(new Vector3f(-115f, -7f, 140f)); //The Sandpile
        SpawnLoc.add(new Vector3f(160f, 3f, 180f)); //behind the large pit

    }

    public void PULoc() {
        PULoc.add(new Vector3f(20f, 67f, 8f)); //On top of mountain
        PULoc.add(new Vector3f(-180f, 27f, -90f)); //The houses on the hill
        PULoc.add(new Vector3f(80f, -23f, -140f)); //The boat
        PULoc.add(new Vector3f(-115f, -7f, 148f)); //The Sandpile
        PULoc.add(new Vector3f(180f, 3f, 180f)); //behind the large pit
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (isRunning) {
            PUtimer = PUtimer + tpf;
            if (PUtimer >= 30) {
                PUtimer = 0;
                ActivePuInt = rnd.nextInt(PULoc.size());
                ActivePULoc = PULoc.get(ActivePuInt);
                ActivePUTime = 30;
                SpawnPUMessage message = new SpawnPUMessage(ActivePuInt, ActivePUTime);
                server.broadcast(message);
                System.out.println("Sent PU message");

            }

            updateTimer += tpf;
            if (updateTimer >= GlobalUtility.UPDATE_RATE) {
                for (Map.Entry<Integer, InsideKeyPair> entry : map.entrySet()) {
                    //if(map.get(i).inside){
                    updateMessage mess = new updateMessage(entry.getKey(), PlayerHP.get(entry.getKey()), PlayerLoc.get(entry.getKey()), PlayerVelo.get(entry.getKey()));
                    server.broadcast(mess);
                    //}
                }
                updateTimer = 0f;
            }

        }
    }

    public void initListener() {

        server.addMessageListener(new AuthServerListener(this), 
                ConnectionApprovedMessage.class, 
                ConnectionMessageReq.class, 
                KeyAckMessage.class, 
                KeyMessage.class, 
                NotAllowedMessage.class, 
                ReconnectMessage.class, 
                WelcomeBackMessage.class, 
                updateMessage.class, 
                PicktPU.class, 
                SetupMessage.class, 
                WalkingMessage.class, 
                shootMessage.class, 
                PlayerDisconnectedMessage.class, 
                FallDmgMessage.class);
    }

    private boolean authenticated(int id, int key) {
        return map.containsKey(id) && map.get(id).key == key;
    }

    private void deadReconingCalc(int id, Vector3f pos, Vector3f velo, float acc, Vector3f view, float tpf) {
        float posx = (float) (pos.x + velo.x * tpf + (0.5f) * acc * Math.pow(tpf, 2)); //just linear calc for player position
        float posz = (float) (pos.z + velo.z * tpf + (0.5f) * acc * Math.pow(tpf, 2));
        float posy = (float) (pos.y + velo.y * tpf + (0.5f) * acc * Math.pow(tpf, 2));
        PlayerLoc.replace(id, new Vector3f(posx, posy, posz));

    }

    private void checkhit(int id, Vector3f loc) {
        int xbox = 3; // hitbox
        int ybox = 6;
        int zbox = 2;
        int HP = 10;
        boolean hit = false;
         for (Map.Entry<Integer, InsideKeyPair> entry : map.entrySet()) {
            if (entry.getKey() != id) { // check so that you dont hit your self
                if (PlayerLoc.get(entry.getKey()).x + xbox >= loc.x && PlayerLoc.get(entry.getKey()).x - xbox <= loc.x) {
                    if (PlayerLoc.get(entry.getKey()).z + zbox >= loc.z && PlayerLoc.get(entry.getKey()).z - zbox <= loc.z) {
                        if (PlayerLoc.get(entry.getKey()).y + ybox >= loc.y && PlayerLoc.get(entry.getKey()).y - ybox <= loc.y) {
                            hit = true;
                            HP = PlayerHP.get(entry.getKey()) - 1;
                            PlayerHP.remove(entry.getKey());
                        }
                    }
                }

                if (hit) {

                    PlayerHP.put(entry.getKey(), HP);
                    if (PlayerHP.get(entry.getKey()) == 0) {
                        respawnPlayer(entry.getKey(), id);
                    }

                    System.out.println("Player" + id + "hit" + entry.getKey());
                    //System.out.println(PlayerHP);
                    break;
                }
            }
        }

    }

    private void respawnPlayer(int id, int killer) {
        Vector3f startingPos = SpawnLoc.get(rnd.nextInt(SpawnLoc.size()));
        PlayerRespawnMessage RespawnMessage = new PlayerRespawnMessage(id, startingPos, killer);
        PlayerHP.replace(id, 10);
        PlayerLoc.replace(id, startingPos);
        server.broadcast(RespawnMessage);
    }

    private void removePlayer(int id) {
        PlayerLoc.replace(id, new Vector3f(0, 0, 0));
        map.remove(id);
        PlayerLoc.remove(id);
        PlayerHP.remove(id);
        PlayerVelo.remove(id);
    }

    private class InsideKeyPair {

        int key;
        boolean inside = false;

        InsideKeyPair(boolean inside, int key) {
            this.inside = inside;
            this.key = key;
        }
    }

    public class AuthServerListener implements MessageListener<HostedConnection> {

        GameServer GS;

        AuthServerListener(GameServer server) {
            this.GS = server;
        }

        @Override
        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof KeyMessage) {
                System.out.println("keyMess recevied");
                KeyMessage msg = (KeyMessage) m;
                if (map.containsKey(msg.getId())) {
                    System.out.println("Key used twice :O ");
                } else {
                    map.put(msg.getId(), new InsideKeyPair(false, msg.getKey()));

                    Vector3f startingPos = SpawnLoc.get(rnd.nextInt(SpawnLoc.size()));
                    PlayerLoc.put(msg.getId(), startingPos);
                    PlayerHP.put(msg.getId(), 10);
                    PlayerVelo.put(msg.getId(), new Vector3f(0, 0, 0));
                    KeyAckMessage message = new KeyAckMessage(msg.getId(), msg.getKey(), startingPos);
                    source.send(message);

                }
            }
            if (m instanceof ReconnectMessage) {
                ReconnectMessage msg = (ReconnectMessage) m;
                if (authenticated(msg.getId(), msg.getKey())) {
                    if (!map.get(msg.getId()).inside) {
                        source.send(new WelcomeBackMessage());
                        map.put(msg.getId(), new InsideKeyPair(true, map.get(msg.getId()).key));
                    } else {
                        System.out.println("Got mutiple reconnectMessage");
                    }
                } else {
                    NotAllowedMessage message = new NotAllowedMessage();
                    source.send(message);
                }
            }
            if (m instanceof PlayerDisconnectedMessage) {
                PlayerDisconnectedMessage msg = (PlayerDisconnectedMessage) m;
                removePlayer(msg.getId());
                server.broadcast(msg);

            }
            if (m instanceof updateMessage) {

                final updateMessage msg = (updateMessage) m;
                if (map.containsKey(msg.getId())) {
                    this.GS.enqueue(new Runnable() {
                        @Override
                        public void run() {

                            GS.PlayerLoc.replace(msg.getId(), msg.getLoc());
                            if (msg.getHp() == 0) {
                                GS.respawnPlayer(msg.getId(), msg.getId());

                            }
                        }
                    });

                }

            }
            if (m instanceof PicktPU) {
                final PicktPU msg = (PicktPU) m;
                if (authenticated(msg.getId(), msg.getKey())) {
                    server.broadcast(msg);
                    System.out.println(msg.getId() + " pickt up a pu");
                    if (ActivePuInt != -1) {
                        ActivePuInt = -1;
                        PlayerHP.replace(msg.getId(), (PlayerHP.get(msg.getId()) + 3));

                        if (PlayerHP.get(msg.getId()) >= GlobalUtility.MAX_HEALTH) {
                            PlayerHP.replace(msg.getId(), GlobalUtility.MAX_HEALTH);
                        }

                        ActivePULoc = new Vector3f(0, 0, 0);
                        RemovePUMessage message = new RemovePUMessage();
                        server.broadcast(message);

                    }
                }
                //else discard the message

            }
            if (m instanceof SetupMessage) {
                if (authenticated(((SetupMessage) m).getId(), ((SetupMessage) m).getKey())) {

                    for (Map.Entry<Integer, InsideKeyPair> entry : map.entrySet()) {
                        StartUpMessage mess = new StartUpMessage(entry.getKey(), PlayerLoc.get(entry.getKey()));
                        server.broadcast(mess);
                    }
                    if (ActivePuInt != -1) {
                        SpawnPUMessage message1 = new SpawnPUMessage(ActivePuInt, ActivePUTime);
                        server.broadcast(message1);
                    }
                }

            }
            if (m instanceof WalkingMessage) {
                final WalkingMessage msg = (WalkingMessage) m;
                server.broadcast(msg);
                this.GS.enqueue(new Runnable() {
                    @Override
                    public void run() {
                        GS.deadReconingCalc(msg.getid(), msg.getPos(), msg.getVelo(), msg.getAcc(), msg.getView(), msg.getTime());
                        GS.PlayerVelo.put(msg.getid(), msg.getVelo());

                    }
                });
            }
            if (m instanceof shootMessage) {
                final shootMessage msg = (shootMessage) m;
                if (authenticated(msg.getId(), msg.getKey())) { // check if player is allowed to send
                    server.broadcast(msg);
                    if (!msg.getLoc().equals(new Vector3f(0, 0, 0))) {
                        this.GS.enqueue(new Runnable() {
                            @Override
                            public void run() {

                                GS.checkhit(msg.getId(), msg.getLoc());
                            }
                        });
                    } else {

                    }
                }

            }
            if (m instanceof FallDmgMessage) {
                final FallDmgMessage msg = (FallDmgMessage) m;
                GS.PlayerHP.replace(msg.getId(), msg.getHP());
            }
        }
    }
}
