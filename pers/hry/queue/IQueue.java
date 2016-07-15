package pers.hry.queue;

public interface IQueue<T> {

	public void add(T t);
	
	public T[] getQueue();
}
