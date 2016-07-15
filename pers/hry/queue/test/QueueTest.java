package pers.hry.queue.test;

import java.util.ArrayList;
import java.util.HashMap;

import pers.hry.queue.IQueue;
import pers.hry.queue.impl.FlashQueueByBackup;
import pers.hry.queue.impl.FlashQueueByGrowUp;

public class QueueTest {

	public static IQueue<String> q=new FlashQueueByBackup<String>(String.class,500);
	public static int all=0;
	public static class Ma extends Thread{
		public void run(){
			HashMap h=new HashMap();
			while(true){
				String[] as= q.getQueue();
				for(String a:as){
					if(a!=null){
					//	System.out.println(a);
						if(h.containsKey(a)){
							System.out.println("嶷鹸。。。。。。。。。。。。。。。。。。。。"+a);
						}
						h.put(a, a);
						all++;
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
			for(int i=0;i<10000;i++){
				q.add(key+"-"+i);
				try{
					Thread.sleep(2);
				}catch(Exception e){
					
				}
			}
			
		}
	}
	
	public static void main(String[] arg){
		Ma m=new Ma();
		m.start();
		for(int i=0;i<50;i++){
			Pd p=new Pd("t"+i);
			p.start();
		}
		try{
			Thread.sleep(10000);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+all);
		}catch(Exception e){
			
		}
	}
}
