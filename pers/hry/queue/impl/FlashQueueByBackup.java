package pers.hry.queue.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import pers.hry.queue.Customer;
import pers.hry.queue.IQueue;

/**
 * 
 * @author hry
 *
 * @param <T>
 * 本实现采用常用队列（queue）+备用队列（backUps）的方式。生产者将信息（T）试图放入 常用队列，当常用队列满时，就放在后备队列
 * 常用队列基本没有堵塞，备用队列会有堵塞。所以当越少用备用队列时，整个队列的效率越高。测试可以到每秒10W个消息量
 * 
 */
public class FlashQueueByBackup<T> implements IQueue<T> {
	private final int qSize;
	private int mSize;
	private Class<T> type;
	private volatile T[] queue; 
	private volatile AtomicInteger qIndex;
	/*其中一次队列的已赋值下标*/
	private volatile int ing;
	private ArrayList<T> backUps=new  ArrayList<T>();
	Customer<T> cust;
	public FlashQueueByBackup(Class<T> _type, int length){  
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
	   while(i>ing){
		   ing=i;
	   }
   }
	
	@Override
   public void add(T t){  
	  
      int i=qIndex.incrementAndGet();
      if(i<mSize){
    	  if(queue[i]==null){
    		  queue[i]=t;
    		  setIng(i);
    	  }else{
    		  add(t);
    	  }
      }else{
    	  //如果已经满了，放入后备队列
    	  putToBackUp(t);
    	 
    	 
      }
   }  
   
	@Override
   public    T[] getQueue(){
	   T[] ts=  getQueues();
	    
	   ArrayList<T> at=getBackUp();
	   if(at.isEmpty()){
		   return Arrays.copyOf(ts, mSize);
	   }
	   for(int i=0;i<mSize;i++){
		   T t=ts[i];
		   if(t!=null){
			   at.add(t);
		   }
	   }
	   return ( T[] )at.toArray( (T[]) Array.newInstance(type, at.size()));
	   
	 //  return ts;
   }
   
   /**
    * 获取常用队列
    * */
   private  T[]  getQueues(){
	   //缩小size，使新的消息放到备用队列
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
    * 放入后备队列
    * */
   private synchronized  void putToBackUp(T t){
	   System.out.println("ddddddddd");
	   backUps.add(t);
   }
   
   /**
    * 获取后备队列
    * */
   private synchronized   ArrayList<T> getBackUp(){
	   ArrayList<T> btmp =backUps;
	   backUps=new  ArrayList<T>();
	   return btmp;
   }
   
	 
}
