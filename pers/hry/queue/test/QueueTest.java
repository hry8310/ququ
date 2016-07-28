package pers.hry.queue.test;

import java.util.ArrayList;
import java.util.HashMap;

import pers.hry.queue.Customer;
import pers.hry.queue.IQueue;
import pers.hry.queue.impl.FlashLinkedQueue;
import pers.hry.queue.impl.FlashQueueByBackup;
import pers.hry.queue.impl.FlashQueueByGrowUp;
import pers.hry.queue.impl.FlashQueueByLinkedBackup;

public class QueueTest {

	public static IQueue<String> q=new FlashLinkedQueue<String>(String.class);
	public static int all=0;
	public static class Ma extends Thread{
		public void run(){
			HashMap h=new HashMap();
			while(true){
				String[] as= q.getQueue();
				for(String a:as){
					if(a!=null){
					
						all++;
					}else{
						//break;
					}
				}
				try{
					Thread.sleep(10);
				}catch(Exception e){
					
				}
			}
		}
	}
	
	
	public static class Pd extends Thread{
		String key;
		public Pd(String k){
			key=k;
		}
		public void run(){
			try{ 
				//Thread.sleep(1000);
			}catch(Exception e){
				
			}
			for(int i=0;i<10000;i++){
				q.add(key+"-"+i);
				
			}
			
		}
	}
	
	public static class BackUpCust extends Customer<String>{
		public void use(String[] as){
			for(String a:as){
				if(a!=null){
				 
					all++;
				}
			}
		}
	}
	
	public static void main(String[] arg){
		Ma m=new Ma();
		
		m.start();
		for(int i=0;i<10000;i++){
		//	System.out.println("d"+all);
			Pd p=new Pd("t"+i);
			p.start();
		}
		try{
			//Thread.sleep(5000);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+all);
		}catch(Exception e){
			
		}
	}
}
