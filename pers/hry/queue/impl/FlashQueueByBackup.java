package pers.hry.queue.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import pers.hry.queue.IQueue;

/**
 * 
 * @author hry
 *
 * @param <T>
 * ��ʵ�ֲ��ó��ö��У�queue��+���ö��У�backUps���ķ�ʽ�������߽���Ϣ��T����ͼ���� ���ö��У������ö�����ʱ���ͷ��ں󱸶���
 * ���ö��л���û�ж��������ö��л��ж��������Ե�Խ���ñ��ö���ʱ���������е�Ч��Խ�ߡ����Կ��Ե�ÿ��10W����Ϣ��
 * 
 */
public class FlashQueueByBackup<T> implements IQueue<T> {
	private final int qSize;
	private int mSize;
	private Class<T> type;
	private volatile T[] queue; 
	private volatile AtomicInteger qIndex;
	/*����һ�ζ��е��Ѹ�ֵ�±�*/
	private volatile int ing;
	private ArrayList<T> backUps=new  ArrayList<T>();
	public FlashQueueByBackup(Class<T> _type, int length){  
	   type=_type;
	   qSize=length;
	   mSize=qSize;
	   this.queue = (T[]) Array.newInstance(type, qSize);  
	   qIndex=new AtomicInteger(0);
   }
   
	@Override
   public void add(T t){  
	  
      int i=qIndex.incrementAndGet();
      if(i<mSize){
    	  if(queue[i]==null){
    		  queue[i]=t;
    		  ing=i;
    	  }else{
    		  add(t);
    	  }
      }else{
    	  //����Ѿ����ˣ�����󱸶���
    	  putToBackUp(t);
      }
   }  
   
	@Override
   public    T[] getQueue(){
	   T[] ts=  getQueues();
	   ArrayList<T> at=getBackUp();
	   for(T t:ts){
		   if(t!=null){
			   at.add(t);
		   }
	   }
	   return ( T[] )at.toArray( (T[]) Array.newInstance(type, at.size()));
   }
   
   /**
    * ��ȡ���ö���
    * */
   private  T[]  getQueues(){
	   //��Сsize��ʹ�µ���Ϣ�ŵ����ö���
	   mSize=ing; 
	   ing=0;
	   T[] oqueue=queue;
	 
	   T[] tmp = (T[]) Array.newInstance(type, qSize);  
	   while(ing!=0){
		   ing=0;
	   }
	   queue=tmp;
	 
	   qIndex=new AtomicInteger(0);
	   mSize=qSize;
	   return  oqueue;
   }
   /**
    * ����󱸶���
    * */
   private synchronized  void putToBackUp(T t){
	
	   backUps.add(t);
   }
   
   /**
    * ��ȡ�󱸶���
    * */
   private synchronized   ArrayList<T> getBackUp(){
	   ArrayList<T> btmp =backUps;
	   backUps=new  ArrayList<T>();
	   return btmp;
   }
   
	 
}
