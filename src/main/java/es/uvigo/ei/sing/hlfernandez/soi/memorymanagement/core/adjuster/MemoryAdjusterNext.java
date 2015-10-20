package es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.adjuster;

import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryBlock;
import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryManager;

public class MemoryAdjusterNext implements MemoryAdjuster {
    
	public int adjust(MemoryManager manager, int blockSize) {
		int res = manager.getLastAssignedBlock().isPresent() ? 
			manager.getBlocks().indexOf(manager.getLastAssignedBlock().get()) : 
			0;
		boolean listVisited = false;

		while (res != -1) {
			MemoryBlock block = manager.getBlocks().get(res);
			if (block.isFree() && block.getSize() >= blockSize) {
				break;
			}

			res++;

			if (res >= manager.getBlocks().size()) {
				if (listVisited) {
					res = -1;
				} else {
					res %= manager.getBlocks().size();
					listVisited = true;
				}
			}
		}

		return res;
	}
}
