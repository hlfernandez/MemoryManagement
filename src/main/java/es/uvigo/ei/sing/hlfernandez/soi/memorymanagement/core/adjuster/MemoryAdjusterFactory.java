package es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.adjuster;

public class MemoryAdjusterFactory {

	public static MemoryAdjuster createNextMemoryAdjuster() {
		return new MemoryAdjusterNext();
	}

	public static MemoryAdjuster createFirstMemoryAdjuster() {
		return new MemoryAdjusterFirst();
	}

	public static MemoryAdjuster createBestMemoryAdjuster() {
		return new MemoryAdjusterBest();
	}

	public static MemoryAdjuster createWorstMemoryAdjuster() {
		return new MemoryAdjusterWorst();
	}
}
