package blockchain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;
public class Http {
	public static void Starthttp()
	{
		try {
			HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080),0);
			File file = new File("D:/blockchain/blocks.json");
			BufferedReader bufferedreader=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			StringBuffer stringbuffer = new StringBuffer();
			String temp = "";
			while((temp=bufferedreader.readLine())!=null){
				stringbuffer.append(temp);
				}
				String string = stringbuffer.toString();
			httpServer.createContext("/",new HttpHandler() {

				@Override
				public void handle(HttpExchange exchange) throws IOException {
					// TODO Auto-generated method stub
					byte[] respContents = string.getBytes("UTF-8");
					
					//设置响应头
					exchange.getResponseHeaders().add("Content-Type","text/html; charset=UTF-8");
				
					//设置响应code和内容长度
					exchange.sendResponseHeaders(200, respContents.length);
					
					//设置响应内容
					exchange.getResponseBody().write(respContents);
					
					//关闭处理器，同时将关闭请求和响应的输入输出流（如果还没关闭）
					exchange.close();
				}
			});
			System.out.print("server launched");
			//launch server
			httpServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
