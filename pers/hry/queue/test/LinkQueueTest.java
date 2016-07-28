package pers.hry.queue.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import pers.hry.queue.IQueue;
import pers.hry.queue.LinkedBlockingQueueExt;
import pers.hry.queue.impl.FlashQueueByBackup;
import pers.hry.queue.impl.FlashQueueByGrowUp;

public class LinkQueueTest {

	public static LinkedBlockingQueueExt<String>  bq=new LinkedBlockingQueueExt<String>();
	public static int all=0;
	public static class Ma extends Thread{
		public long cnt=0;
		public void run(){
			HashMap h=new HashMap();
			while(true){
				try{
					
					Object[] as=bq.toArrayAndClear2();
					 
					
					for(Object a:as){
						if(a!=null){
						 
							all++;
							 
						}
					}
					/*
					String a=bq.take();
					 
					if(a!=null){
							all++;
					}
				 	*/
				}catch(Exception e){
					e.printStackTrace();
					System.exit(0);
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
				bq.add(key+"-"+i);
				try{
				//	Thread.sleep(2);
				}catch(Exception e){
					
				}
			}
			
		}
	}
	
	public static void main(String[] arg)throws Exception{
		LinkedBlockingQueueExt<String>  bq2=new LinkedBlockingQueueExt<String>();
		bq2.add("1");
		Object[] aa=bq2.toArrayAndClear2();
		System.out.println("bq2 "+bq2.isEmpty()+" aa: "+aa[0]);
		Ma m=new Ma();
		m.start();
	 
		for(int i=0;i<10000;i++){
		//	System.out.println("d "+all);
			Pd p=new Pd("t"+i);
			p.start();
		}
		try{
		//	Thread.sleep(10000);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+(all ));
		}catch(Exception e){
			
		}
	}
}
