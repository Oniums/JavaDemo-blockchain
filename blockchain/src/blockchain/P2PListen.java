package blockchain;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class P2PListen extends Thread {

	public static int port = 8123;
	public static DatagramSocket ds = null;
	public static DatagramPacket dp = null;
	public static byte[] buf = new byte[1024];

	public P2PListen() {
		super();
	}

	static Object ob = "P2PListen";

	@Override
	public void run() {

		try {
			ds = new DatagramSocket(port);
			dp = new DatagramPacket(buf, buf.length);
			System.out.println("Start Listening on Port 8123");
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		while (true) {
			synchronized (ob) {
				while (true) {
					try {
						ds.receive(dp);
						String ReceIP=dp.getAddress().toString().replace("/","");

						int i;
						StringBuffer sbuf = new StringBuffer();
						for (i = 0; i < 1024; i++) {
							if (buf[i] == 0) {
								break;
							}
							sbuf.append((char) buf[i]);
						}
						String BlockMessage=sbuf.toString();
						BlockMessage=BlockMessage.substring(BlockMessage.indexOf("{"),BlockMessage.indexOf("}")+1);
						System.out.println("["+ReceIP+"]:"+BlockMessage);
						Gson gson = new Gson();
						Block block = gson.fromJson(BlockMessage, new TypeToken<Block>(){}.getType());
						if(ReceIP.equals(BlockChain.localip))continue;
						else {BlockChain.addBlock(block);}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
