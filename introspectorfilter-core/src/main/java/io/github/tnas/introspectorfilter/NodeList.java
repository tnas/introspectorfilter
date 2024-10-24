package io.github.tnas.introspectorfilter;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class NodeList extends ArrayList<Node> {

	private static final long serialVersionUID = 1L;
	
	public Node removeFirst() {
		
		if (this.isEmpty()) {
			throw new NoSuchElementException();
		} else {
			return this.remove(0);
		}
	}

}
