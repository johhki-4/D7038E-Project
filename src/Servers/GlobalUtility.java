/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servers;

import com.jme3.network.serializing.Serializer;
import Messages.*;

/**
 *
 * @author Andreas
 */
public class GlobalUtility {

    public static final String NAME = "Our Cool Game";
    public static final String GAME_SERVER = "85.226.166.180"; //130.240.55.53"; //130.240.55.67"; //192.168.101.163"; //130.240.55.xx
    public static final String AUTH_SERVER = "85.226.166.180"; //130.240.55.53"; //130.240.55.67"; //192.168.101.163";
    public static final int VERSION = 1;
    public static final int AUTH_PORT = 6143;
    public static final int GAME_PORT = 6144;
    public static final int MAX_HEALTH =10;
    public static final int MAX_AMMO = 7;
    public static final float UPDATE_RATE = 0.0f;
    public static final float FALL_DMG_THRESHOLD = 0.7f;
    public static final float SHOOT_CD = 0.4f;
    public static final float DMG_CD = 0.5f;
    public static final float HP_GAIN = 0.5f;

    public static void initSeri() {
        Serializer.registerClass(ConnectionMessageReq.class); //0
        Serializer.registerClass(KeyMessage.class); //1
        Serializer.registerClass(KeyAckMessage.class); //2
        Serializer.registerClass(ConnectionApprovedMessage.class); //3
        Serializer.registerClass(NotAllowedMessage.class); //4 
        Serializer.registerClass(ReconnectMessage.class); //5
        Serializer.registerClass(SpawnPUMessage.class); // 6    
        Serializer.registerClass(WelcomeBackMessage.class); //7 // not in practical use
        Serializer.registerClass(updateMessage.class); // 8
        Serializer.registerClass(PlayerRespawnMessage.class); //9
        Serializer.registerClass(PicktPU.class); //10
        Serializer.registerClass(RemovePUMessage.class); //11
        Serializer.registerClass(StartUpMessage.class); // 12
        Serializer.registerClass(SetupMessage.class); //13
        Serializer.registerClass(PlayerDisconnectedMessage.class); //14 
        Serializer.registerClass(WalkingMessage.class); //15
        Serializer.registerClass(shootMessage.class); //16
        Serializer.registerClass(FallDmgMessage.class);//17
    }
}
