package es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.adjuster;

import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryBlock;
import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryManager;

public class MemoryAdjusterFirst implements MemoryAdjuster {

	public int adjust(MemoryManager manager, int blockSize) {
		int res = 0;

		for (MemoryBlock block : manager.getBlocks()) {
			if (block.isFree() && block.getSize() >= blockSize) {
				break;
			}
			res++;
		}
		if (res == manager.getBlocks().size()) {
			res = -1;
		}
		return res;
	}
}
