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
 * 本实现采用常用队列（queue）增长的方式。生产者将信息（T）试图放入 队列，当队列不足时，会增加队列的长度
 * 1、当队列需要增长到一定长度时，一般就会停止
 * 2、增长队列的长度是一个耗时工作，所以队列增长的越少，效率越高
 * 3、队列增长后，就不会缩短
 */
public class FlashQueueByGrowUp<T> implements IQueue<T> {
 
	private int mSize;
	private Class<T> type;
	private volatile T[] queue; 
	private volatile AtomicInteger qIndex; 
	private volatile int ing;
	private volatile int backup=1; 
	private volatile int step;
 
   public FlashQueueByGrowUp(Class<T> _type, int length){  
	   type=_type; 
	   mSize=length; 
	   step=mSize/2 ;
	   this.queue = (T[]) Array.newInstance(type, mSize);   
	   qIndex=new AtomicInteger(0); 
   }
   
   /**
    * 获取队列
    */
   
   public  T[]  getQueue(){
	   
	   ing=0;
	   
	   T[] oqueue=queue;
 
	   T[] tmp = (T[]) Array.newInstance(type, mSize);  
	   while(ing!=0){
		   ing=0;
	   }
	   queue=tmp;
	 
	   qIndex=new AtomicInteger(0);
	
	   return  oqueue;
   }
   
   /**
    * 增加
    */
   public  void add(T t){
	   if(backup==1){
		   int i=qIndex.incrementAndGet();
		   if(i>mSize-1){
			   grawup(); 
		   }
		   if(queue[i]==null){
			   if(backup==1){
				   queue[i]=t;
				   ing=i;
				   return ;
			   }
		   }
	   }
	   add(t);
   }
   
   /**
    * 队列增长
    */
   private void grawup(){
	   backup=0;
	   mSize=mSize+step;
	   System.out.println("group-up");
	   T[] tmp=(T[]) Array.newInstance(type, mSize);
	   queue=tmp;
	   System.arraycopy(queue, 0, tmp, 0, queue.length);
	 
	   backup=1;
   }
   

  
   
	 
}
