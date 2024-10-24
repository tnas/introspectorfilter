package io.github.tnas.introspectorfilter;

public class Node {
	
	private int height; 
	
	private int breadth;
	
	private Object value;
	
	public Node(int height, int breadth, Object value) {
		this.height = height;
		this.breadth = breadth;
		this.value = value;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getBreadth() {
		return breadth;
	}

	public void setBreadth(int breadth) {
		this.breadth = breadth;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
