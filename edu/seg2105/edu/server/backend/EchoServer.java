package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF serverUI; 
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  serverUI.display("Message received: " + msg + " from " + client);
    
	  
	  if(client.getInfo("login") == null) {
		  String[]  msgs = new String[2];
		  try {
			msgs = msg.toString().split(" ");
			
		  if(msgs[0].equals("#login")) {
			  client.setInfo("login", msgs[1]);
		  } else {
			  serverUI.display("New Client: Wrong entry command");
			  client.close();
		  }
			
		  }catch(IOException i) {
			  serverUI.display("Critical Error");
			  System.exit(1);
		  }
		  catch(Exception e) {
			  serverUI.display("New Client: Failed to login a client");
		  }

	  } else {
		  if (msg.toString().startsWith("#login")) {
		 try{
			 serverUI.display("New Client: Error - Attempted login while logged in ....");
			  client.close();
		  }catch(IOException i) {
			  serverUI.display("Critical Error");
			  System.exit(1);
		  }
		  }
		  this.sendToAllClients(client.getInfo("login") + " " + msg);
	  }
	  
	  
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	  serverUI.display
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
	  serverUI.display
      ("Server has stopped listening for connections.");
  }
  
  
  
  public void handleMessageFromClientUI(String message) {
	    try
	    {
	      sendToAllClients(message);
	    }
	    catch(Exception e)
	    {
	      serverUI.display
	        ("Could not send message to clients.");
	    }
	  
  }

  //Client Connection methods ***************************************************
  
  protected void clientConnected(ConnectionToClient client) {
	  
	  serverUI.display("Client Status: New Client has connected!");
  }
  
	synchronized protected void clientDisconnected() {
		serverUI.display("Client Status: A Client has disconnected");
	}
	
	synchronized protected void clientException(	ConnectionToClient client, Throwable exception) {
		serverUI.display("Client Status: A Client stopped responding");
	}
}
//End of EchoServer class
