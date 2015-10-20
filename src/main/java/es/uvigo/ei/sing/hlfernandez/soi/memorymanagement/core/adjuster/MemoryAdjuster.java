package es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.adjuster;

import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryManager;

public interface MemoryAdjuster {
	public int adjust(MemoryManager manager, int blockSize);
}
