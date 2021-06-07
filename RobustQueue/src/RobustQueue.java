
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;

/******************************************************************************
 * Raga Madhuri Podugu
 * This class is a homework assignment;
 * A Queue is an ADT and has public operations. 
 * The queue will support "robust" iterators, iterators that will never go stale
 *
 * This assignment uses a circular doubly-linked list implementation.
 ******************************************************************************/
public class RobustQueue<E> extends AbstractQueue <E>{

	private static class Node<T>
	{
		T data; 
		Node<T> prev, next; 

		@SuppressWarnings("unused")
		public Node() 
		{
			prev = next = null; 
			data = null; 
		}

		@SuppressWarnings("unused")
		public Node(T data, Node<T> prev, Node<T> next)
		{
			this.data = data; 
			this.prev = prev; 
			this.next = next; 
		}
		
		@Override // Implement 
		public String toString() {
			return super.toString() + "[" + data + "]";
		}
	}

	private Node<E> dummy; 
	private int size; 


	private static boolean doReport = true; // changed only by invariant tester

	private boolean report(String error) {
		if (doReport) System.out.println("Invariant error: " + error);
		else System.out.println("Caught problem: " + error);
		return false;
	}
	@Override // Implement 
	public String toString() {
		String list = " "; 
		for(Node<E> e = dummy; e!= dummy; e = e.next)
		{
			list += "[" + e.data + "]"; 
		}
		return list; 
	}

	private boolean wellFormed() {
		// 1. The linked list starting at dummy node has consistent
		//    prev/next links.  As long as you stop when you get back to the dummy node
		//    and make sure that all prev links are consistent as you traverse the list,
		//    you will avoid getting lost in a cycle.
		// 2. size is the length of the list.
		int count = 0; 
		if(dummy!= null)
		{
			Node<E> prev = dummy;
			for(Node<E> p = dummy.next; p!= dummy; p = p.next)
			{
				if(p == null)
				{
					return report("found null in the list after" + count + "nodes"); 
				}
				if(p.prev != prev)
				{
					return report("prev link is not correct after" + count + "nodes"); 
				}
				++count;
				prev = p; 
			}
			if(dummy.prev != prev)
			{
				return report("head's prev is not correct"); 
			}
		}
		if(size != count)
		{
			return report("many nodes should be " + count + ", was " + size); 
		}

		return true; 
	}
	
	/**
	 * Initialize an empty Queue .
	 * @postcondition
	 *  This Queue is empty.
	 **/  
	public RobustQueue()
	{
		dummy = new Node<E>(null, null, null); 
		dummy.prev = dummy.next = dummy; 
		size = 0; 
		assert wellFormed() : "Invariant false at the end of Constructor"; 
	}

	@Override // required
	public boolean offer(E e) {
		assert wellFormed() : "Invariant failed at the start of offer"; 
		Node<E> temp; 

		if(dummy.next == dummy)
		{
			temp = new Node<E>(e, dummy, dummy); 
			dummy.prev = temp; 
			dummy.next = temp; 
		}
		else
		{
			temp = new Node<E>(e,dummy.prev, dummy);
			dummy.prev.next = temp; 
			dummy.prev = temp; 
		}
		size++;
		assert wellFormed(): "Invariant failed at the end of offer"; 
		return true; 
	}

	@Override // required
	public E poll() {
		assert wellFormed() : "Invariant failed at the start of poll"; 
		Node<E> firstNode = new Node<E>(null, null, null);
		firstNode = dummy.next;
		if(dummy.next.next != dummy) {
			dummy.next.next.prev = dummy; 
			dummy.next = dummy.next.next; 
			--size;
		}
		else if(dummy.next != dummy) 
		{
			dummy.prev = dummy.next = dummy; 
			--size;
		}
		
		assert wellFormed() : "Invariant failed at the end of poll"; 
		return firstNode.data;
	}

	@Override // required
	public E peek() {
		assert wellFormed() : "Invariant failed at the start of peek"; 
		return dummy.next.data; 
	}

	@Override // required 
	public int size() {
		assert wellFormed() : "Invariant failed at the start of size"; 
		return size;
	}

	@Override // required
	public Iterator<E> iterator() {
		assert wellFormed() : "invariant broken at start of iterator()";
		return new MyIterator();
	}
	
	private class MyIterator implements Iterator<E> {

		private Node<E> cursor; 
		
		private MyIterator()
		{
			cursor = dummy; 
		}
		
		@Override // required
		public boolean hasNext() {
			if(dummy.next != dummy || cursor.next != dummy)
			{
				if(dummy.next != cursor)
				{
					cursor = dummy; 
				}
				return true; 
				
			}
			else
			{
				return false; 
			}
		}

		@Override // required
		public E next() {
			if(!hasNext())
			{
				throw new NoSuchElementException("There is no next element"); 
			}
		    cursor = cursor.next; 
			Node<E> temp = cursor; 
			if(dummy.next != cursor)
			{
				cursor = temp.prev; 
			}
			return cursor.data; 
		}
		
		@Override //implement
		public void remove()
		{
			if(!hasNext())
			{
				throw new IllegalStateException("There is no element to be removed"); 
			}
			Node<E> a = cursor; 
			cursor.data = null; 
			cursor.next.prev = cursor.prev; 
			cursor.prev.next = cursor.next; 
			--size; 
			
		}
	}

}
