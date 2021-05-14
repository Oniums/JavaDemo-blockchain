package blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Miner extends Thread {

    public static String host = "192.168.31.255";
    public static int port = 8123;

    public Miner() {
        super();
    }

    static int tick = 20;

    static Object ob = "Miner";

    @Override
    public void run() {
        while (tick > 0) {
            synchronized (ob) {
                try {
                	BlockChain.startMiner();
                    InetAddress adds = InetAddress.getByName(host);
                    DatagramSocket ds = new DatagramSocket();
                    DatagramPacket dp = new DatagramPacket(BlockChain.BroadCastMessage.getBytes(), BlockChain.BroadCastMessage.length(), adds, port);
                    ds.send(dp);
                    ds.close();
                    System.out.println("Send BlockMessage Finish");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                sleep(7500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}