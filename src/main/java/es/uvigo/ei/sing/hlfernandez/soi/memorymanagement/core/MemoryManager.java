package es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;

import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.adjuster.MemoryAdjuster;

public class MemoryManager extends Observable {

	private Optional<MemoryBlock> lastAssignedBlock = Optional.ofNullable(null);
	private LinkedList<MemoryBlock> blocks;
	private MemoryAdjuster adjuster;
	private int[] memory;
	private int size;
	
	public MemoryManager(int size, MemoryAdjuster adjuster) {
		this.size = size;
		this.adjuster = adjuster;
		initMemory();
	}
	
	private void initMemory() {
		blocks = new LinkedList<MemoryBlock>();
		memory = new int[size];
		blocks.add(new MemoryBlock(true, 0, size, 0));
	}
	
    public boolean createProcess(int pid, int size) {
    	int hole = this.adjuster.adjust(this, size);
		
    	boolean res = (hole != -1);
		if (res) {
            int address = blocks.get(hole).getStart();
            MemoryBlock proceso = new MemoryBlock(false, address, size, pid);
            setLastAssignedBlock(proceso);

			int remainingSpace = blocks.get(hole).getSize() - size;

			blocks.set(hole, proceso);

			if (remainingSpace > 0) {
             	MemoryBlock remainingBlock = 
             		new MemoryBlock(true, address + size, remainingSpace, 0);
                blocks.add(hole + 1, remainingBlock);
            }
			writeMemory(address, size, 1);
			_notifyObservers();
		}
		
		return res;
	}

    private void writeMemory(int address, int size, int data) {
          for (int i = 0; i < size; i++) {
                 memory[address + i] = data;
          }
    }
    
    public void clear() {
    	initMemory();
    	_notifyObservers();
    }

    public boolean destroyProcess(int pid) {
        int index = 0;
        for (MemoryBlock bloque : blocks) {
               index++;
               if (bloque.getPid() == pid) {
                      break;
               }
        }
        index--;

        boolean found = (index != -1);

		if (found) {
			MemoryBlock blockToRemove = blocks.get(index);
			blocks.get(index).setFree(true);
			blocks.get(index).setPid(0);
			writeMemory(blockToRemove.getStart(), blockToRemove.getSize(), 0);
			mergeBlockWithNext(index);
			mergeBlockWithNext(index - 1);
			_notifyObservers();
		}
		
		return found;
	}
    
	private boolean mergeBlockWithNext(int index) {
		boolean mergeable = false;
		if (index >= 0 && (index + 1) < blocks.size()) {
			mergeable = blocks.get(index).isFree()
					&& blocks.get(index + 1).isFree();
		}
		if (mergeable) {
			int size = blocks.remove(index + 1).getSize();
			blocks.get(index).setSize(blocks.get(index).getSize() + size);
		}
		return mergeable;
	}

	public boolean resize(int newSize) {
		if(newSize > this.size) {
			this.size = newSize;
			updateBlocks(newSize);
			resizeMemory();
			_notifyObservers();
			return true;
		} else {
			return false;
		}
	}
	
	private void updateBlocks(int newSize) {
		MemoryBlock lastBlock = this.blocks.getLast();
		if(lastBlock.isFree()) {
			lastBlock.setSize(lastBlock.getSize() + (newSize - this.memory.length));
		} else {
			MemoryBlock newBlock = new MemoryBlock(true, this.memory.length, (newSize - this.memory.length), 0);
			this.blocks.add(newBlock);
		}
	}

	private void resizeMemory() {
		int[] newMemory = new int[this.getSize()];
		for(int i = 0; i < this.memory.length; i++) {
			newMemory[i] = this.memory[i];
		}
		for(int i = this.memory.length; i < newMemory.length; i++) {
			newMemory[i] = 0;
		}
		this.memory = newMemory;
	}

	@Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        toString.append("Memory blocks:\n");
		for (MemoryBlock block : blocks) {
			toString.append("\t").append(block.toString()).append("\n");
		}

        toString.append("Memory: ");

        for (int i : memory) {
        	toString.append(i);
        }
        
    	toString.append("\n");
    	
        return toString.toString();
  }

	public List<MemoryBlock> getBlocks() {
		return this.blocks;
	}

	public int getSize() {
		return this.size;
	}

	public void setMemoryAdjuster(MemoryAdjuster adjuster) {
		this.adjuster = adjuster;
	}

	public Optional<MemoryBlock> getLastAssignedBlock() {
		return lastAssignedBlock;
	}

	private void setLastAssignedBlock(MemoryBlock lastAssignedBlock) {
		this.lastAssignedBlock = Optional.ofNullable(lastAssignedBlock);
	}
	
	private void _notifyObservers() {
		this.setChanged();
		this.notifyObservers();
	}
}
