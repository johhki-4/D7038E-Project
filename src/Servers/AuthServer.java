/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servers;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import Messages.*;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

/**
 * Handles the Id of player and the connection key handout
 * @author Andreas
 */
public class AuthServer extends SimpleApplication implements ConnectionListener, ClientStateListener {

    private Server server;
    private Client GameServer;
    int issuedIDs = 0;
    AuthListenerClient ClientListener;
    AuthListenerServer ServerListner;
    HashMap<Integer, ClientKeyPair> ClientID = new HashMap<>();

    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        AuthServer app = new AuthServer();
        app.start(JmeContext.Type.Headless);
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void simpleInitApp() {
        GlobalUtility.initSeri();
        ClientListener = new AuthListenerClient();
        ServerListner = new AuthListenerServer();
        try {
            server = Network.createServer(GlobalUtility.AUTH_PORT);
            server.addMessageListener(ClientListener, ConnectionMessageReq.class);
            server.addConnectionListener(this);
            server.start();

        } catch (IOException ex) {
            ex.printStackTrace();
            destroy();
        }

        try {
            GameServer = Network.connectToServer(GlobalUtility.GAME_SERVER, GlobalUtility.GAME_PORT);
            GameServer.addMessageListener(ServerListner, KeyAckMessage.class);
            GameServer.addClientStateListener(this);
            GameServer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            destroy();

        }

        System.out.println("AuthServer is up and running!");
    }

    @Override
    public void destroy() {
        server.close();
        GameServer.close();
        super.destroy();
    }

    @Override
    public void connectionAdded(Server server, HostedConnection conn) {
        System.out.println("A client has connected to AuthServer");
    }

    @Override
    public void connectionRemoved(Server server, HostedConnection conn) {
        System.out.println("A client has disconnected to AuthServer");
        PlayerDisconnectedMessage msg = new PlayerDisconnectedMessage(ClientID.get(conn.getId()).client);
        GameServer.send(msg);
    }

    @Override
    public void clientConnected(Client c) {
        System.out.println("AuthServer properly connected to GameServer.");
    }

    @Override
    public void clientDisconnected(Client c, DisconnectInfo info) {
        System.out.println("AuthServer properly disconnected from GameServer.");
    }
// This part is based on the example given to ous by Håkan Jonsson

    class ClientKeyPair {

        int client, key;

        ClientKeyPair(int client, int key) {
            this.client = client;
            this.key = key;
        }
    }

    public class AuthListenerClient implements MessageListener<HostedConnection> {
        AuthListenerClient() {
       
        }

        @Override
        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof ConnectionMessageReq) {
                int assignedId = issuedIDs++;
                int client = source.getId();
                int key = (int) System.currentTimeMillis();
                ClientID.put(assignedId, new ClientKeyPair(client, key));
                GameServer.send(new KeyMessage(assignedId, key));
            } else {
                throw new RuntimeException("Unknown Message.");
            }
        }
    }

    public class AuthListenerServer implements MessageListener<Client> {

        AuthListenerServer() {
        }

        @Override
        public void messageReceived(Client source, Message m) {
            if (m instanceof KeyAckMessage) {
                KeyAckMessage msg = (KeyAckMessage) m;
                if (ClientID.containsKey(msg.getID())) {
                    int client = ClientID.get(msg.getID()).client;
                    HostedConnection conn = AuthServer.this.server.getConnection(client);
                    conn.send(new ConnectionApprovedMessage(msg.getID(), msg.getKey(), msg.getSpawnLoc()));
                    //ClientID.remove(msg.getID());
                    } else {
                    System.out.println("ID is not valid");
                }
            } else {
                throw new RuntimeException("Unknown Message.");
            }
        }

    }

}
