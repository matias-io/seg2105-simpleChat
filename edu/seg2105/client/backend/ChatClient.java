// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  
  /**
 *  The specified login ID variable
 */
String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    
    this.loginID = loginID;
    this.clientUI = clientUI;
    openConnection();
    
    
    sendToServer("#login " + loginID); //initiates automatic initial login, only first time
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    		
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
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
    	        	clientUI.display("Command: Terminating the client");
    	        	clientUI.display("Goodbye!");
    	        	quit();
    	        	
    	        } else if (commandName.equals("LOGOFF")) {
    	        	clientUI.display("Command: Disconnecting from Server");
    	        	try {
    	        		closeConnection();
    	        	} catch(IOException e){
    	        		clientUI.display(e.getMessage());
    	        	}
    	        	clientUI.display("Connection closed.");
    	        	 
    	        } else if (commandName.equals("SETHOST")) {
    	        	
    	        	if (!isConnected()) {
    	        		clientUI.display("Command: Setting New host...");
        	        	if(input.size() < 1) {
        	        		clientUI.display("Invalid SETHost command format. Usage: SETHOST <host>");
        	        	} else {
        	                    String  hostTmp = input.remove(0);
        	                    setHost(hostTmp);
        	                    clientUI.display("Setting server port to " + getHost());
        	        	}
    	        	} else {
    	        		clientUI.display("Command: Setting New Host...");
    	        		clientUI.display("Cannot Set a port while server is sitll active!");
    	        		
    	        	}
    	        	
    	        } else if (commandName.equals("SETPORT")) {
    	        	
    	        	if (!isConnected()) {
    	        		clientUI.display("Command: Setting New Port...");
        	        	if(input.size() < 1) {
        	        		clientUI.display("Invalid SETPORT command format. Usage: SETPORT <port>");
        	        	} else {
        	                try {
        	                    int portTmp = Integer.parseInt(input.remove(0));
        	                    setPort(portTmp);
        	                    clientUI.display("Setting server port to " + getPort());

        	                } catch (NumberFormatException e) {
        	                	clientUI.display("Invalid port number.");
        	                } 
        	        	}
        	        	} else {
        	        		clientUI.display("Command: Setting New Port...");
        	        		clientUI.display("Cannot Set a port while server is sitll active!");
        	        		
        	        	}
    	        	
    	        } else if (commandName.equals("LOGIN")) {
    	        	clientUI.display("Command: Logging in server...");
    	        	if (!isConnected()) {
    	        		clientUI.display("...");
        	            try 
        	            {
        	              openConnection();
        	              sendToServer("#login " + loginID);
        	              
        	            } 
        	            catch(IOException exception) 
        	            {
        	              System.out.println("Error: Can't setup connection!"
        	                        + " Terminating client.");
        	              System.exit(1);
        	            }
        	        	
        	        	} else {

        	        		clientUI.display("Cannot login  while already logged in!");
        	        		
        	        	}
    	        	
    	        } else if (commandName.equals("GETHOST")) {
    	        	clientUI.display("Command: Retrieving current client host...");
    	        	clientUI.display("Currently on host: " + getHost());
    	        	
    	        } else if (commandName.equals("GETPORT")) {
    	        	clientUI.display("Command: Retrieving current client port...");
    	        	clientUI.display("Currently on port: " + getPort());
    	        	
    	        } else {
    	        	clientUI.display("Unrecognized command.");
    	        }
    	        
        	} else {
        		sendToServer("> " + message);
        	}
        }
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) { }
    System.exit(0);
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
	
//Server Closing  methods ************************************************
	/**
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
	@Override
  protected void connectionClosed() {
	  	clientUI.display("The server has shut down.");
	  	
  }
	/**
	 * Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  @Override
  protected void connectionException(Exception exception) {
	  	clientUI.display("SYSTEM: Server has stopped responding!, closing client program...");
	  	
	  	quit();
  }
}


//End of ChatClient class
