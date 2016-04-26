import java.net.ServerSocket;

public final class WebServer
{
	public static void main(String argv[]) throws Exception
	{
		int port = 8080;
		
		ServerSocket listen = new ServerSocket(port);
		
		// Connection active
		boolean active = true;
		
		System.out.println("Root Directory: " + System.getProperty("user.dir"));
		
		// Process HTTP service requests in an infinite loop
		while (active){
			HttpRequest request = new HttpRequest(listen.accept());
			Thread thread = new Thread(request);
			thread.start();
		}
		
		listen.close();
	}
}


//http://localhost:8080/index.html