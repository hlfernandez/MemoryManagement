package es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.adjuster;

import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryBlock;
import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryManager;

public class MemoryAdjusterWorst implements MemoryAdjuster {

	public int adjust(MemoryManager manager, int blockSize) {
		int i = 0;
		int biggestBlock = -1;
		int biggestBlockIndex = -1;

		for (MemoryBlock bloque : manager.getBlocks()) {
			if (bloque.isFree() && bloque.getSize() >= blockSize
				&& (biggestBlock == -1 || bloque.getSize() > biggestBlockIndex)
			) {
				biggestBlock = i;
				biggestBlockIndex = bloque.getSize();
			}
			i++;
		}

		return biggestBlock;
	}
}
