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
 * ��ʵ�ֲ��ó��ö��У�queue�������ķ�ʽ�������߽���Ϣ��T����ͼ���� ���У������в���ʱ�������Ӷ��еĳ���
 * 1����������Ҫ������һ������ʱ��һ��ͻ�ֹͣ
 * 2���������еĳ�����һ����ʱ���������Զ���������Խ�٣�Ч��Խ��
 * 3�����������󣬾Ͳ�������
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
    * ��ȡ����
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
    * ����
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
    * ��������
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
