package es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.demo;

import es.uvigo.ei.sing.hlfernandez.soi.memorymanagement.core.MemoryManager;

public class MemoryManagementDemo {
	public static void applyDemoConfiguration(MemoryManager manager) {
		
		manager.createProcess(1, 10); // Trabajo A
		manager.createProcess(2, 20); // Hueco 1
		manager.createProcess(3, 10); // Trabajo para borrar
		manager.createProcess(4, 25); // Hueco 2
		manager.createProcess(5, 10); // Trabajo C
		manager.createProcess(6, 18); // Hueco 3
		manager.createProcess(7, 10); // Trabajo B

		manager.destroyProcess(3);
		manager.createProcess(3, 10); // Trabajo D: el último

		manager.destroyProcess(2);
		manager.destroyProcess(4);
		manager.destroyProcess(6);
	}
}
