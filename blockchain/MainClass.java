package blockchain;


public class MainClass extends Thread {  
	  public static void main(String[] args) {
		  Http.Starthttp();
		  BlockChain.blockchains=BlockChain.readjson();
		  P2PListen p2plisten = new P2PListen();
		  Miner miner=new Miner();
		  p2plisten.start();
		  miner.start();
	   }
   
}  