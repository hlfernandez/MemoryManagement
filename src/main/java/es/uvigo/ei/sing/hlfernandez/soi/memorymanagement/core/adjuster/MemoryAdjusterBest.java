package es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.adjuster;

import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryBlock;
import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryManager;

public class MemoryAdjusterBest implements MemoryAdjuster {
    
	public int adjust(MemoryManager manager, int blockSize) {
		int i = 0;
		int smallestBlock = -1;
		int smallestBlockSize = -1;

		for (MemoryBlock block : manager.getBlocks()) {
			if (block.isFree()
				&& block.getSize() >= blockSize
				&& (smallestBlock == -1 || block.getSize() < smallestBlockSize)
			) {
				smallestBlock = i;
				smallestBlockSize = block.getSize();
			}
			i++;
		}
		return smallestBlock;
	}

}
