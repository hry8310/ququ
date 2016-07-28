package pers.hry.queue.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import pers.hry.queue.Customer;
import pers.hry.queue.IQueue;
import pers.hry.queue.LinkedBlockingQueueExt;

/**
 * 
 * @author hry
 *
 * @param <T>
 * ��ʵ�ֲ��ó��ö��У�queue��+���ö��У�backUps���ķ�ʽ�������߽���Ϣ��T����ͼ���� ���ö��У������ö�����ʱ���ͷ��ں󱸶���
 * ���ö��л���û�ж��������ö��л��ж��������Ե�Խ���ñ��ö���ʱ���������е�Ч��Խ�ߡ����Կ��Ե�ÿ��10W����Ϣ��
 * 
 */
public class FlashQueueByLinkedBackup<T> implements IQueue<T> {
	private final int qSize;
	private int mSize;
	private Class<T> type;
	private volatile T[] queue; 
	private volatile AtomicInteger qIndex;
	/*����һ�ζ��е��Ѹ�ֵ�±�*/
	private volatile int ing;
	private LinkedBlockingQueueExt<T> backUps=new  LinkedBlockingQueueExt<T>();
	Customer<T> cust;
	public FlashQueueByLinkedBackup(Class<T> _type, int length){  
	   type=_type;
	   qSize=length;
	   mSize=qSize;
	   this.queue = (T[]) Array.newInstance(type, qSize);  
	   qIndex=new AtomicInteger(0);
   }
	
	public void setCust(Customer<T> _cust){
		cust=_cust;
	}
   
   private void setIng(int i){
	  // while(i>ing){
		   ing=i;
	//   }
   }
	
	@Override
   public void add(T t){  
	  
      int i=qIndex.incrementAndGet();
      if(i<mSize){
    	  if(queue[i]==null){
    		  queue[i]=t;
    		  setIng(i);
    	  }else{
    		  putToBackUp(t);
    	  }
      }else{
    	  //����Ѿ����ˣ�����󱸶���
    	  putToBackUp(t);
    	 
    	 
      }
   }  
   
   int qIng;
   @Override
   public    T[] getQueue(){
	   T[] ts=  getQueues();
	   
	    
	   T[] at=getBackUp();
	    
	   
	   
	   int tl=at.length+ts.length;
	   T[] tt= ( T[] )Array.newInstance(type, tl);
	   System.arraycopy(ts, 0, tt, 0, ts.length);
	   System.arraycopy(at,0,tt,ts.length,at.length);
	   return tt;
	 //  return ts;
   }
   
   /**
    * ��ȡ���ö���
    * */
   private  T[]  getQueues(){
	   //��Сsize��ʹ�µ���Ϣ�ŵ����ö���
	   mSize=ing; 
	   qIng=mSize;
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
   private    void putToBackUp(T t){
	 //  System.out.println("ddddddddd");
	   backUps.add(t);
   }
   
   /**
    * ��ȡ�󱸶���
    * */
   private     T[] getBackUp(){
	   try{
		   Object[] ts=backUps.toArrayAndClear2();
		   T[] tmp = (T[]) Array.newInstance(type, ts.length);  
		   for(int i=0;i<ts.length;i++){
			   tmp[i]=(T)ts[i];
		   }
		   return tmp;
	   }catch(Exception e){
		   
	   }
	   return null;
   }
   
	 
}
