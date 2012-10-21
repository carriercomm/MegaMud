package pl.edu.agh.megamud;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import pl.edu.agh.megamud.base.Creature;
import pl.edu.agh.megamud.base.Controller;
import pl.edu.agh.megamud.base.PlayerController;

/**
 * Client session, a running socket.
 **/
public class Session implements Runnable {
	/**
	 * Real socket.
	 */
	public Socket socket;
	/**
	 * Game controller.
	 */
	public PlayerController user;
	/*
	 * Streams.
	 */
	private Scanner in;
	private PrintWriter out;
	
	public Session(Socket socket) throws IOException {
		this.socket = socket;
		user=new PlayerController(this);
		
		InputStream inps = socket.getInputStream();
		OutputStream outs = socket.getOutputStream();

		in = new Scanner(inps);
		out = new PrintWriter(outs, true);
	}
	
	/* Running is like:
	 * - read user's input
	 * - process it
	 */
	
	public void run() {
		try {
			user.onConnect();
			
			while (!user.isReadyToDisconnect() && in.hasNextLine()) {
				String line = in.nextLine();
				try{
					user.interpreteCommand(line);
				}catch(Exception e2){
					//TODO notify user
					e2.printStackTrace();
				}
			}
			
			user.onDisconnect();
		} finally{
			try{
				socket.close();
			}catch(Exception e2){}
			socket= null;
		}
	}
	
	/*
	 * Don't use this, use instead PlayerController.write().
	 */
	public void write(String txt){
		try{
			out.println(txt);
		}catch(Exception e){
			//TODO kill connection
			e.printStackTrace();
		}
	}
	
}
