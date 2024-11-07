package edu.seg2105.edu.server;

import java.util.Scanner;
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

	  
	  
	  //Constructors ****************************************************
	/**
	 * Constructs an instance of the ServerConsole UI
	 * 
	 * @param port The initial port to listen on
	 */
	public ServerConsole(int port) {

	    try 
	    {
	      server= new EchoServer(port, this);
	      server.listen(); //Start listening for connections

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
	        
	        server.handleMessageFromClientUI(message);

	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!" + ex.toString());
	    }
	  }		  
	
	
	
	  /**
	   * This method overrides the method in the ChatIF interface.  It
	   * displays a message onto the screen.
	   *
	   * @param message The string to be displayed.
	   */
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
		

