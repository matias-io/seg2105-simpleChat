package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	  if(msg.toString().charAt(0) == '>') {
		  serverUI.display("Message received: " + msg.toString().substring(2)+ " from " + client.getInfo("login"));
	  }
	  else {
		  
		  serverUI.display("Message received: " + msg + " from " + client.getInfo("login"));
	  }
    
	  
	  if(client.getInfo("login") == null) {
		  String[]  msgs = new String[2];
		  try {
			msgs = msg.toString().split(" ");
			
		  if(msgs[0].equals("#login")) {
			  client.setInfo("login", msgs[1]);
			  this.sendToAllClients(client.getInfo("login") + " has logged on");
			  serverUI.display(client.getInfo("login") + " has logged on");

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
		  this.sendToAllClients(client.getInfo("login") + msg.toString());
	  }
	  
	  
  }
    
  
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message) {
	    try
	    {
	    	
	    	if ( message != null && message.length() > 0) {
	        	char firstChar = message.charAt(0);
	        	
	        	if(firstChar == '#') {
	        		message = message.substring(1);
	    	        List<String> input = this.tokenize(message);
	    	        
	    	        String commandName = input.remove(0).toUpperCase();
	    	        
	    	     // Create commands
	    	        if (commandName.equals("QUIT")) {
	    	        	serverUI.display("Command: Terminating the server");
	    	        	try {
	    	        		close();
	    	        	}catch (Exception e) {}
	    	        	serverUI.display("Goodbye!");
	    	        	System.exit(0);
	    	        	
	    	        } else if (commandName.equals("STOP")) {
	    	        	serverUI.display("Command: Terminating server listening");
	    	        	stopListening();
	    	        	
	    	        } else if (commandName.equals("CLOSE")) {
	    	        	serverUI.display("Command: Server closing... disconnecting all clients and stopping listening...");
	    	        	try {
	    	        		close();
	    	        	}catch (Exception e) {}
	    	        	serverUI.display("The server has shut down.");
	    	        	
	    	        } else if (commandName.startsWith("SETPORT")) {
	    	        	
	    	        	if (!isListening()) {
	    	        		serverUI.display("Command: Setting New Port...");
		    	        	if(input.size() < 1) {
		    	        		serverUI.display("Invalid SETPORT command format. Usage: SETPORT <port>");
		    	        	} else {
		    	                try {
		    	                    int portTmp = Integer.parseInt(input.remove(0));
		    	                    setPort(portTmp);
		    	                    serverUI.display("Setting server port to " + getPort());

		    	                } catch (NumberFormatException e) {
		    	                	serverUI.display("Invalid port number.");
		    	                } 
		    	        	}
		    	        	} else {
		    	        		serverUI.display("Command: Setting New Port...");
		    	        		serverUI.display("Cannot Set a port while server is sitll active!");
		    	        		
		    	        	}

	    	        } else if (commandName.equals("START")) {
	    	        	serverUI.display("Command: Server starting to listen for new clients...");
	    	        	if (!isListening()) {
	    	        		serverUI.display("...");

	    	                try {
	    	                	listen();
	    	                } catch (Exception e) {
	    	                	serverUI.display("Something went wrong initiating server!.");
	    	                } 
	    	        	} else {
	    	        		serverUI.display("Cannot start and already running server!");
	    	        		
	    	        	}
	    	        } else if (commandName.equals("GETPORT")) {
	    	        	serverUI.display("Command: Retrieving current server port...");
	    	        	serverUI.display("Currently on port: " + getPort());
	    	        } else {
	    	        	serverUI.display("Unrecognized command.");
	    	        }
	    	        
	        	} else {
	        		sendToAllClients("SERVER MESSAGE> " + message);
	      	      serverUI.display("SERVER MESSAGE> " + message);
	        	}
	        }
	      	    }
	    catch(Exception e)
	    {
	      serverUI.display
	        ("Could not send message to clients.");
	    }
	  
  }
  
  
  //Server Status ***********************************
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	  serverUI.display
      ("Server listening for clients on port " + getPort());
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
  
  
  //Semantic Command Processing *****************************************

	/**
	 * Tokenizes a command string into a list of arguments.
	 * 
	 * 	@author UniversityManagementSystem
	 * @param command the command string
	 * @return a list of arguments
	 */
	private List<String> tokenize(String command) {
		List<String> tokens = new ArrayList<>();
		Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
		Matcher matcher = pattern.matcher(command);

		while (matcher.find()) {
			String token = matcher.group();
			token = removeQuotes(token); // Remove quotes if they are added

			tokens.add(token);
		}

		return tokens;
	}
	/**
	 * Removes quotes from a string if they exist.
	 *
	 * @author UniversityManagementSystem 
	 * @param str the string to process
	 * @return the string without quotes
	 */
	private String removeQuotes(String str) {
		if (str == null || str.length() < 2) {
			return str;
		}
		if (str.startsWith("\"") && str.endsWith("\"")) {
			return str.substring(1, str.length() - 1);
		}
		return str;
	}
	

  //Client Connection methods ***************************************************
  
	/**
	 * Hook method called each time a new client connection is
	 * accepted. The default implementation does nothing.
	 * @param client the connection connected to the client.
	 */
	@Override
  protected void clientConnected(ConnectionToClient client) {
	  
	  serverUI.display("A new client has connected to the server.");
  }
  
	/**
	 * Hook method called each time a client disconnects.
	 * The default implementation does nothing. The method
	 * may be overridden by subclasses but should remains synchronized.
	 *
	 * @param client the connection with the client.
	 */
	@Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		  // OCSF framework does not properly implement this ?, 
		  //That said, as by Javadoc, should be called when a client stops connection gracefully through closeConnection(). 
		  // and exception thrown when done ungracefully such as termination
		
		serverUI.display(client.getInfo("login") + " has disconnected.");
	}
	
	/**
	 * Hook method called each time an exception is thrown in a
	 * ConnectionToClient thread.
	 * The method may be overridden by subclasses but should remains
	 * synchronized.
	 *
	 * @param client the client that raised the exception.
	 * @param Throwable the exception thrown.
	 */
	@Override
	synchronized protected void clientException(	ConnectionToClient client, Throwable exception) {
		serverUI.display(client.getInfo("login") + " has disconnected abruptly.");
	}
}
//End of EchoServer class
