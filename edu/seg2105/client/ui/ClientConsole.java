package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

 boolean  loggedIn = false;
 int port;
 String host;
  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port) 
  {
	  
	  this.host = host;
	  this.port = port;
    try 
    {
      client= new ChatClient(host, port, this);
      loggedIn = true;
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        
        if ( message != null && message.length() > 0) {
        	char firstChar = message.charAt(0);
        	
        	if(firstChar == '#') {
        		message = message.substring(1);
    	        List<String> input = this.tokenize(message);
    	        
    	        String commandName = input.remove(0).toUpperCase();
    	        
    	     // Create commands
    	        if (commandName.equals("QUIT")) {
    	        	display("Command: Terminating the server");
    	        	client.closeConnection();
    	        	display("Goodbye!");
    	        	System.exit(0);
    	        	
    	        } else if (commandName.equals("LOGOFF")) {
    	        	display("Command: Terminating server listening");
    	        	client.closeConnection();
    	        	display("Disconnected from server");
    	        	
    	        } else if (commandName.equals("SETHOST")) {
    	        	
    	        	if (!loggedIn) {
	    	            display("Command: Setting New host...");
	    	        	if(input.size() < 1) {
	    	        		display("Invalid SETPORT command format. Usage: SETHOST <host>");
	    	        	} else {
	    	                    String  hostTmp = input.remove(0);
	    	                    host = hostTmp;
	    	                    display("Setting server port to " + host);
	    	        	}
    	        	} else {
    	        		display("Command: Setting New Host...");
    	        		display("Cannot Set a port while server is sitll active!");
    	        		
    	        	}
    	        	
    	        } else if (commandName.equals("SETPORT")) {
    	        	
    	        	if (!loggedIn) {
	    	            display("Command: Setting New Port...");
	    	        	if(input.size() < 1) {
	    	        		display("Invalid SETPORT command format. Usage: SETPORT <port>");
	    	        	} else {
	    	                try {
	    	                    int portTmp = Integer.parseInt(input.remove(0));
	    	                    port = portTmp;
	    	                    display("Setting server port to " + port);

	    	                } catch (NumberFormatException e) {
	    	                    display("Invalid port number.");
	    	                } 
	    	        	}
	    	        	} else {
	    	        		display("Command: Setting New Port...");
	    	        		display("Cannot Set a port while server is sitll active!");
	    	        		
	    	        	}
    	        	
    	        } else if (commandName.equals("LOGIN")) {
	        		display("Command: Logging in server...");
    	        	if (!loggedIn) {
	    	            display("...");

	    	                try {
	  
	    	                    client.openConnection();
	    	                    display("Setting server port to " + port);

	    	                } catch (Exception e) {
	    	                    display("Something went wrong connecting!.");
	    	                } 
	    	        	
	    	        	} else {

	    	        		display("Cannot login  while already logged in!");
	    	        		
	    	        	}
    	        	
    	        } else if (commandName.equals("GETHOST")) {
    	        	display("Command: Retrieving current client host...");
    	            display("Currently on host: " + host);
    	        	
    	        } else if (commandName.equals("GETPORT")) {
    	        	display("Command: Retrieving current client port...");
    	            display("Currently on port: " + port);
    	        	
    	        } else {
    	        	display("Unrecognized command.");
    	        }
    	        
        	} else {
        		client.handleMessageFromClientUI("SERVER MSG> " + message);
        	}
        }

      }
        
        
      
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!" + ex.toString());
    }
  }

  
	/**
	 * Tokenizes a command string into a list of arguments.
	 * 
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
  
  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    String host = "";
    int port = 0;

    try
    {
      host = args[0];
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      host = "localhost";
    }
    
    try
    {
      port = Integer.parseInt(args[1]);
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      port = DEFAULT_PORT;
    }
    ClientConsole chat= new ClientConsole(host, port);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
