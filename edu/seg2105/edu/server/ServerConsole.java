package edu.seg2105.edu.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;

public class ServerConsole implements ChatIF{

	  //Class variables *************************************************
	  
	  /**
	   * The default port to listen on.
	   */
	
	  final public static int DEFAULT_PORT = 5555;
	  
	  //Instance variables **********************************************
	  /**
	   * Server object interacting with OCSF created in this program
	   */
	EchoServer server;
	  /**
	   * Scanner to read from the console
	   */
	  Scanner fromConsole; 
	  boolean isActive = false;
	  int port;
	  
	  
	  //Constructors ****************************************************
	public ServerConsole(int port) {
		this.port = port;
	    try 
	    {
	      server= new EchoServer(port, this);
	      server.listen(); //Start listening for connections
	      isActive = true;
	    } 
	    catch(Exception exception) 
	    {
	      System.out.println("Error: Can't setup connection!"
	                + " Terminating server client." + exception.getMessage());
	      System.exit(1);
	    }
	    
	    // Create scanner object to read from console
	    fromConsole = new Scanner(System.in); 
	}

	 
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
	    	        	server.close();
	    	        	display("Goodbye!");
	    	        	System.exit(0);
	    	        	
	    	        } else if (commandName.equals("STOP")) {
	    	        	display("Command: Terminating server listening");
	    	        	server.stopListening();
	    	        	display("Server has stopped listening for connections.");
	    	        	
	    	        } else if (commandName.equals("CLOSE")) {
	    	        	display("Command: Server closing... disconnecting all clients and stopping listening...");
	    	        	server.close();
	    	        	isActive = false;
	    	        	display("The server has shut down.");
	    	        	
	    	        } else if (commandName.startsWith("SETPORT")) {
	    	        	
	    	        	if (!isActive) {
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

	    	        } else if (commandName.equals("START")) {
	    	            display("Command: Server starting to listen for new clients...");
	    	        	if (!isActive) {
		    	            display("...");

	    	                try {
	  
	    	            	    try 
	    	            	    {
	    	            	      server= new EchoServer(port, this);
	    	            	      server.listen(); //Start listening for connections
	    	            	      isActive = true;
	    	            	    } 
	    	            	    catch(Exception exception) 
	    	            	    {
	    	            	      System.out.println("Error: Can't setup connection!"
	    	            	                + " Terminating client.");
	    	            	      System.exit(1);
	    	            	    }
	    	            	    
	    	            	    // Create scanner object to read from console
	    	            	    fromConsole = new Scanner(System.in); 

	    	                } catch (Exception e) {
	    	                    display("Something went wrong connecting!.");
	    	                } 
	    	        	} else {
	    	        		display("Cannot start and already running server!");
	    	        		
	    	        	}
	    	        } else if (commandName.equals("GETPORT")) {
	    	            display("Command: Retrieving current server port...");
	    	            display("Currently on port: " + port);
	    	        } else {
	    	        	display("Unrecognized command.");
	    	        }
	    	        
	        	} else {
	        		server.handleMessageFromClientUI("SERVER MESSAGE> " + message);
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
	
	@Override
	public void display(String message) {
//	    System.out.println("$ > " + message);
		System.out.println(message);
	}
	
	  //Class methods ***************************************************
	  
	  /**
	   * This method is responsible for the creation of 
	   * the server instance (there is no UI in this phase).
	   *
	   * @param args[0] The port number to listen on.  Defaults to 5555 
	   *          if no argument is entered.
	   */
	public static void main(String[] args) {
	    int port; //Port to listen on

	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
		
	    ServerConsole sv = new ServerConsole(port);
	    sv.accept();

		
	}
		
}
		

