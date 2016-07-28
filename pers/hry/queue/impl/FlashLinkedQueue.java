package pers.hry.queue.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import pers.hry.queue.IQueue;

import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
 
public class FlashLinkedQueue<E> 
        implements IQueue<E>  {
  
 
    static class Node<E> {
        E item;

     
        Node<E> next;

        Node(E x) { item = x; }
    }

    
    private final int capacity;

 
    private final AtomicInteger count = new AtomicInteger(0);

     
    private   Node<E> head;

    private   Node<E> last;
    private final ReentrantLock takeLock = new ReentrantLock();

    private final Condition notEmpty = takeLock.newCondition();

    private final ReentrantLock putLock = new ReentrantLock();

    private final Condition notFull = putLock.newCondition();

    private Class<E> type;
   
    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }
 
    private void signalNotFull() {
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }

  
    private void enqueue(Node<E> node) {
      
        last = last.next = node;
    }

    
    private E dequeue() {
      
        Node<E> h = head;
        Node<E> first = h.next;
        h.next = h; // help GC
        head = first;
        E x = first.item;
        first.item = null;
        return x;
    }

    
 
    public FlashLinkedQueue(Class<E> _type) {
    	
    	 this.capacity=(Integer.MAX_VALUE);
    	 last = head = new Node<E>(null);
         type=_type;
    }

     
       
    public void add(E e)   {
        if (e == null) throw new NullPointerException();
        int c = -1;
        Node<E> node = new Node(e);
        final ReentrantLock putLock = this.putLock;
        final AtomicInteger count = this.count;
      
        try {
        	 putLock.lockInterruptibly();
             
            
            while (count.get() == capacity) {
                notFull.await();
            }
            enqueue(node);
            c = count.getAndIncrement();
            if (c + 1 < capacity)
                notFull.signal();
        }catch(Exception exc){
        	exc.printStackTrace();
        } finally {
            putLock.unlock();
        }
        if (c == 0)
            signalNotEmpty();
    }
 
    
    public  E[]  getQueue()  {
        //  fullyLock();
    	 int c = -1;
      	 final ReentrantLock takeLock = this.takeLock;
      	  E[] a =null;
         
          try {
        	takeLock.lockInterruptibly();  
          	Node<E> l=last;
          	Node<E> pp=head;
          //	System.out.println("l "+l);
          	while (count.get() == 0) {
                notEmpty.await();
            }
          	int size = count.get();
           
          	//System.out.println("size "+size);
              a =  (E[]) Array.newInstance(type, size);   ;
              int k = 0;
              for (Node<E> p, h = head; (p = h.next) !=null; h = p) {
              
                  h.next = h;
                  a[k++] = p.item;
                  p.item = null;
                  pp=p;
                  c=count.getAndIncrement();
                  
                  if(p==l||k==size-1){
                  	break;
                  }
              }
              head = pp; 
            
              if (c> 1)
                  notEmpty.signal();
              
          } catch(Exception exc){
        	  
          }finally {
              takeLock.unlock();
          }
          if (c == capacity)
              signalNotFull();
          return a;
      }
    
          
}
