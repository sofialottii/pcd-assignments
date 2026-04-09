package pcd.sketch02.controller;

import pcd.sketch02.model.Counter;
import pcd.sketch02.util.BoundedBuffer;
import pcd.sketch02.util.BoundedBufferImpl;

public class ActiveController extends Thread {

	private BoundedBuffer<Cmd> cmdBuffer;
	private Counter counter;
	
	public ActiveController(Counter counter) {
		this.cmdBuffer = new BoundedBufferImpl<Cmd>(100);
		this.counter = counter;
	}
	
	public void run() {
		log("started.");
		while (true) {
			try {
				log("Waiting for cmds ");
				var cmd = cmdBuffer.get();
				log("new cmd fetched: " + cmd);
				cmd.execute(counter);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void notifyNewCmd(Cmd cmd) {
		try {
			cmdBuffer.put(cmd);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void log(String msg) {
		System.out.println("[ " + System.currentTimeMillis() + "][ Controller ] " + msg);
	}
}

/*
questo controller ha come obiettivo ricevere input asincroni e aggiornare il model. Per farlo,
uso un'architettura produttore consumatore. Quindi il controller ha un buffer (chiamato commandBuffer),

riga 31: notifyNewCommand mette il comando dentro al buffer.

Il controller, in quantocomonente attivo, ha un ciclo (riga 19), e quando riceve un comando lo esegue
*/