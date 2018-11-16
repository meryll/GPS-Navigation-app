package tech.hypermiles.hypermiles.Bluetooth;

import java.nio.ByteBuffer;

/**
 * Created by Asia on 2016-12-21.
 */
//TODO
public class Message {

    public static int indicator = 0xDEADBEEF;
    public static int duration = 4000;
    public static byte power = 125;
    public static byte repeats = 100;
    public static int interval = 500;
    public static int checksum = 0xDEADBEEF;
    private static byte[] message;

    public static byte[] get() {
        if (message == null)
        {
            message = new byte[18];

            putToArray(0xEF,0);
            putToArray(0xBE,1);
            putToArray(0xAD,2);
            putToArray(0xDE,3);
            putToArray(0x64,4);
            putToArray(0x00,5);
            putToArray(0x00,6);
            putToArray(0x00,7);
            putToArray(0xFF,8);
            putToArray(0x02,9);
            putToArray(0x64,10);
            putToArray(0x00,11);
            putToArray(0x00,12);
            putToArray(0x00,13);
            putToArray(0x11,14); // dwa razy 13 jest
            putToArray(0x22,15);
            putToArray(0x33,16);
            putToArray(0x44,17);


//            putToArray(indicator, 0);
//            putToArray(duration, 4);
//            putToArray(power, 8);
//            putToArray(repeats, 9);
//            putToArray(interval, 10);
//            putToArray(checksum, 14);
        }
        return message;
    }

    private static void putToArray(int value, int index) {
        byte[] smt = ByteBuffer.allocate(4).putInt(value).array();
        byte val = smt[3];
        message[index] = val;

//        byte[] smt = ByteBuffer.allocate(4).putInt(value).array();
//
//        for (int i = 0; i < 4; i++) {
//            message[index + i] = smt[i];
//        }
    }

    private static void putToArray(byte value, int index) {

        message[index] = value;
    }
}
