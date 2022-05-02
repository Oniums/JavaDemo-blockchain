package blockchain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
public class BlockChain {

	public static String bits="0x1e500000";

	public static String localip=GetIP();
	
	public static int nonce=0;
	public static ArrayList<Block> blockchains = new ArrayList<Block>();
	
	public static String BroadCastMessage =null;
	// 获取创世区块:
	public static Block getGenesisBlock() {
		return new Block(1,"0",1465154705,"my genesis block!","816534932c2b7154836da6afc367695e6337db8a921823784c14378abed4f7d7", "0x1e500000",0,"192.168.31.2");
	}
	
	// 创建创世区块:
	public static void writeGenesisBlock() {
		File file = new File("D:/blockchain/blocks.json");
		if(!(file.exists() && file.isFile())){}
		 try {
				file.createNewFile();
				Block GenesisBlock = getGenesisBlock();
				String contents="[{\"index\":"+GenesisBlock.getIndex()+",\"previousHash\":\""+GenesisBlock.getPreviousHash()+"\",\"timestamp\":"+GenesisBlock.getTimestamp()+",\"data\":\""+GenesisBlock.getData()+"\",\"hash\":\""+GenesisBlock.getHash()+"\",\"bits\":\""+GenesisBlock.getBits()+"\",\"nonce\":"+GenesisBlock.getNonce()+",\"minerip\":\""+GenesisBlock.getMinerip()+"\"}]";
				RandomAccessFile rw = new RandomAccessFile(file,"rw");
				rw.write(contents.getBytes());
				rw.close();
				System.out.println("创世区块已生成");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return;
	}

	// 添加区块并写入文件:
	public static void addBlock(Block newBlock) {
		if(isValidNewBlock(newBlock, getLastestBlock())){
			blockchains.add(newBlock);
			writejson(newBlock);
		}
	}

	// 检查一个区块是否合法:
	public static boolean isValidNewBlock(Block newBlock,Block preiviousBlock){
		if(preiviousBlock.getIndex()+1!=newBlock.getIndex()){
			System.out.print("invalid index");
			return false;
		}else if(!preiviousBlock.getHash().equals(newBlock.getPreviousHash())){
			System.out.print("invalid previoushash: "+preiviousBlock.getHash()+"\t"+newBlock.getPreviousHash());
			return false;
		}else if(!calculateHashForBlock(newBlock).equals(newBlock.getHash())){
			System.out.print("invalid hash: "+calculateHashForBlock(newBlock)+"\t"+newBlock.getHash());
			return false;
		}
		return true;
	}

	// 获取上一个区块:
	public static Block getLastestBlock() {
		return blockchains.get(blockchains.size()-1);
	}

	// 产生下一个区块:
	public static Block generateNextBlock(String blockdata,int nonce) {
		Block previousBlock = getLastestBlock();
		int nextIndex = previousBlock.getIndex()+1;
		double nextTimestamp = new Date().getTime()/1000;
		
		String nextHash = calculateHash(nextIndex,previousBlock.getHash(),nextTimestamp,blockdata,bits,nonce);
		return new Block(nextIndex,previousBlock.getHash(),nextTimestamp,blockdata,nextHash,bits,nonce,localip);
	}

//	 替换区块链
//	public static void replaceChain(Block newBlocks) {
//		 if(isValidChain(newBlocks))
//	}

//	 检测区块链是否合法
//	public static void isValidChain() {
//	}

	// 根据区块计算区块的哈希:
	public static String calculateHashForBlock(Block block) {
		return Hash256.getSHA256StrJava(Integer.toString(block.getIndex())+block.getPreviousHash()+Double.toString(block.getTimestamp())+block.getData()+block.getBits()+Integer.toString(block.getNonce()));
	}

	// 计算区块的哈希:
	public static String calculateHash(int index,String previousHash,Double timestamp,String data,String bits,int nonce) {
		return Hash256.getSHA256StrJava(Integer.toString(index)+previousHash+Double.toString(timestamp)+data+bits+nonce);
	}
	
	// 获取矿机地址:
	public static String GetIP(){
		try {
	        Enumeration<NetworkInterface> faces = NetworkInterface.getNetworkInterfaces();
	        while (faces.hasMoreElements()) { // 遍历网络接口
	            NetworkInterface face = faces.nextElement();
	            if (face.isLoopback() || face.isVirtual() || !face.isUp()) {
	                continue;
	            }
//	            System.out.print("网络接口名：" + face.getDisplayName() + "，地址：");
	            Enumeration<InetAddress> address = face.getInetAddresses();
	            while (address.hasMoreElements()) { // 遍历网络地址
	                InetAddress addr = address.nextElement();
//	                System.out.println(addr.getHostAddress());
	                if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress() && !addr.isAnyLocalAddress()) {
//	                    System.out.print(addr.getHostAddress() + " ");
//	                	if(addr.getHostAddress().contains("172"))
	                		return addr.getHostAddress();
	                }
	            }
	            System.out.println("");
	        }
	    } catch (SocketException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	// 读取区块json文件:
	public static ArrayList<Block> readjson() {
		File file = new File("D:/blockchain/blocks.json");
		if(!(file.exists() && file.isFile())){
			writeGenesisBlock();
		}
		ArrayList<Block> blocks = null;
		try {
			BufferedReader bufferedreader=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			StringBuffer stringbuffer = new StringBuffer();
			String temp = "";
			try {
				while((temp=bufferedreader.readLine())!=null){
					stringbuffer.append(temp);
					}
				String string = stringbuffer.toString();
				Gson gson = new Gson();
				blocks = gson.fromJson(string, new TypeToken<ArrayList<Block>>(){}.getType());
				System.out.println("Read Json:");
				for(Block tem:blocks) {
					System.out.println(tem.getIndex()+"\t"+tem.getPreviousHash()+"\t"+tem.getTimestamp()+"\t"+tem.getData()+"\t"+tem.getHash()+"\t"+tem.getBits()+"\t"+tem.getNonce()+"\t"+tem.getMinerip()+"\t");
				}
				}catch(IOException e) {
					e.printStackTrace();
				}
			return blocks;
			}catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
	
	// 写入新区块至json文件:
	public static void writejson(Block block) {
		File file = new File("D:/blockchain/blocks.json");
		if(!(file.exists() && file.isFile())){
            System.out.println("文件不存在  ~ ");
        }
		BroadCastMessage=",{\"index\":"+block.getIndex()+",\"previousHash\":\""+block.getPreviousHash()+"\",\"timestamp\":"+block.getTimestamp()+",\"data\":\""+block.getData()+"\",\"hash\":\""+block.getHash()+"\",\"bits\":\""+block.getBits()+"\",\"nonce\":"+block.getNonce()+",\"minerip\":\""+block.getMinerip()+"\"}]";
		
		try {
			RandomAccessFile rw = new RandomAccessFile(file,"rw");
			rw.seek(rw.length()-1);
			rw.write(BroadCastMessage.getBytes());
			rw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// POW函数:
	public static boolean proof_of_work() {
		Block previousBlock = getLastestBlock();
		int nextIndex = previousBlock.getIndex() + 1;
		double nextTimestamp = new Date().getTime() / 1000;
		String data = "1840707120-zdx "+Math.random()+nonce++;
		
		String nextHash = calculateHash(nextIndex, previousBlock.getHash(), nextTimestamp,data,bits,nonce);
		nonce++;
		String coefficient = bits.substring(4,bits.length());
		String exponent = bits.substring(2,4);
		double target = Integer.parseInt(coefficient,16) * Math.pow(2,(8*(Integer.parseInt(exponent,16)-3)));
		if((HextoDouble(nextHash)-target)<0) {
			System.out.println("["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"]:"+"End Mining");
			addBlock(generateNextBlock(data,nonce));
			nonce=0;
			return false;
		}
		return true;
	}
	
	// 挖矿:
	public static void startMiner() {
		System.out.println("["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"]:"+"Start Mining");
		while(proof_of_work());
	}
	
	// 十六进制数转Double类型
	public static double HextoDouble(String str) {
	    double result=0;
	    for(int i=0;i<str.length();i++) {
	    	result=(result+Math.pow(16, i)*Integer.parseInt(str.substring(str.length()-i-1,str.length()-i), 16));
	    }
	    return result;
	}

}
