package es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core;

public class MemoryBlock {

	private boolean free;
	private int start;
	private int size;
	private int pid;

	public MemoryBlock(boolean free, int start, int size, int pid) {
		this.free = free;
		this.start = start;
		this.size = size;
		this.pid = pid;
	}
	
	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public int getStart() {
		return start;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}
	
	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder();
		toString
			.append("[")
			.append(isFree()?"Free":"Used")
			.append(" - Size: ")
			.append(getSize())
			.append(" - Start: ")
			.append(getStart())
			.append(" - PID: ")
			.append(getPid())
			.append("]");
		return toString.toString();
	}
}
