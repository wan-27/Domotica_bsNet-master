package soulissclient;

/**
 * Created by jctejero on 16/09/2017.
 *
 * data imported from Constants.java --> it.angelic, souliss app
 */

public class Constants {

    public static final String TAG = "ScanNetworkApp";

    public static final int MAX_USER_IDX = 0x64;
    public static final int MAX_NODE_IDX = 0xFE;

    public static final String CUSTOM_INTENT_SOULISS_TIMEOUT = "SOULISS.RAW_TIMEOUT";
    public static final String CUSTOM_INTENT_SOULISS_RAWDATA = "SOULISS.RAW_MACACO_DATA";
    public static final String CUSTOM_INTENT_SOULISS_SUBSCRIBE_RESP = "SOULISS.SUBSCRIBE_RESP";

    /**
     * Network constants
     *
     * @author Ale
     */
    public static class Net {
        public static final int DEFAULT_SOULISS_PORT = 230;
        public static final int SERVERPORT = 23000;
        public static final int WEB_SERVER_PORT = 8080;

        public static final String BROADCASTADDR = "255.255.255.255";

        public static final byte Souliss_UDP_function_force_register    = 0x14;
        public static final byte Souliss_UDP_function_force_back_register    = 0x13;
        public static final byte Souliss_UDP_function_force             = 51;   //0x33
        public static final byte Souliss_UDP_function_force_massive     = 52;   //0x34
        public static final byte Souliss_UDP_subscribe_data_request     = 0x05;     //used to get data when events
        public static final byte Souliss_UDP_subscribe_data_answer      = 0x15;
        public static final byte Souliss_UDP_function_subscribe         = 0x21;
        public static final byte Souliss_UDP_function_poll              = 0x27;
        public static final byte Souliss_UDP_function_poll_resp         = 0x37;
        public static final byte Souliss_UDP_function_subscribe_resp    = 0x31;
        public static final byte Souliss_UDP_function_subscribe_data    = 0x15;
        public static final byte Souliss_UDP_function_typreq            = 0x22;
        public static final byte Souliss_UDP_function_typreq_resp       = 0x32;
        public static final byte Souliss_UDP_function_healthReq         = 0x25;
        public static final byte Souliss_UDP_function_health_resp       = 0x35;

        public static final byte Souliss_UDP_function_ping                          = 0x8;
        public static final byte Souliss_UDP_function_ping_resp                     = 0x18;
        public static final byte Souliss_UDP_function_broadcast_configure           = 0x2D;
        public static final byte Souliss_UDP_function_broadcast_configure_wifissid  = 0x2E;
        public static final byte Souliss_UDP_function_broadcast_configure_wifipass  = 0x2F;
        //public static final byte Souliss_UDP_function_notify_err = 0x83;

        public static final byte Souliss_UDP_function_ping_bcast                    = 0x28;
        public static final byte Souliss_UDP_function_ping_bcast_resp               = 0x38;

        public static final byte Souliss_UDP_function_db_struct                     = 0x26;
        public static final byte Souliss_UDP_function_db_struct_resp                = 0x36;

        public static final Byte[] PING_PAYLOAD = {Souliss_UDP_function_ping, 0, 0, 0, 0};
        public static final Byte[] PING_BCAST_PAYLOAD = {Souliss_UDP_function_ping_bcast, 0, 0, 0, 0};
        public static final Byte[] DBSTRUCT_PAYLOAD = {Souliss_UDP_function_db_struct, 0, 0, 0, 0};

        //WEBSERVER
        public static final String PREF_SERVER_PORT = "prefServerPort";

    }

    /**
     * Preference constants
     *
     * @author jctejero
     */
    public static class prefs {
        public static final String UDP_PORT             = "udpport";
        public static final String UDP_PORT_KEY         = "udpportIC";
        public static final String USER_INDEX           = "userindex";
        public static final String USER_INDEX_KEY       = "userindexIC";
        public static final String NODE_INDEX           = "nodeindex";
        public static final String NODE_INDEX_KEY       = "nodeindexIC";
        public static final String SERVICE_INTERVAL_KEY = "serviceInterval";
        public static final String DATA_SERVICE_KEY     = "checkboxDataService";
        public static final String EYE_ACTBAR_STATE     = "eyeactionbarstate";
    }

    /**
     * Soulis protocols responses
     *
     * @author jctejero
     */
    public static class reponses {
        //Discover a Gateway node
        public static final byte    ADDRESS_GATEWAY     = 5;

        //Database Structure
        //The answer has a fixed structure and contains the number of nodes configured in the gateway,
        // the maximum number of allowed nodes, the number of slots for each node and
        // the number of maximum subscription allowed by the gateway.
        public static final byte    NODES               = 5;
        public static final byte    MAX_NODES           = 6;
        public static final byte    SLOTS_BY_NODES      = 7;
        public static final byte    MAX_SUBSCRIPTION    = 8;

        //Typicals
        public static final byte    TYPICALS            = 5;

    }

    /**
     * Soulis protocols responses
     *
     * @author jctejero
     */
    public static class typicals {
        public static final int     MOTOR_DEVICE        =   0x21;
        public static final int     MOTOR_DEVICE_MIDDLE =   0x22;

        //T22 -- Motorized devices with limit switches and middle position

        // General defines for T2n
        public static final short Souliss_T2n_CloseCmd          = 0x01;
        public static final short Souliss_T2n_OpenCmd           = 0x02;
        public static final short Souliss_T2n_StopCmd           = 0x04;
        public static final short Souliss_T2n_ToogleCmd         = 0x08;
        public static final short Souliss_T2n_RstCmd            = 0x00;
        public static final short Souliss_T2n_Timer_Val         = 0x1F;
        public static final short Souliss_T2n_Timer_Off         = 0x10;
        public static final short Souliss_T2n_LimSwitch_Close   = 0x08;
        public static final short Souliss_T2n_LimSwitch_Open    = 0x10;
        public static final short Souliss_T2n_NoLimSwitch       = 0x20;
        public static final short Souliss_T2n_CloseCmd_SW        = 0x01;
        public static final short Souliss_T2n_OpenCmd_SW         = 0x02;
        public static final short Souliss_T2n_Coil_Stop         = 0x03;
        public static final short Souliss_T2n_Coil_Off          = 0x00;
    }
}
