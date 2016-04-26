import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.activation.MimetypesFileTypeMap; //to get MIME types

final class HttpRequest implements Runnable
{
	final static String CRLF = "\r\n";
	private Socket socket = null;
	
	public HttpRequest(Socket s) throws Exception {
		this.socket = s;
	}

	@Override
	public void run() {
		try {
			processRequest();
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private void processRequest() throws Exception
	{
		 
		 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		 DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		 String requestLine = in.readLine();
		 
		 System.out.println(requestLine);
		 
		 String headerLine = null;
		 while ((headerLine = in.readLine()).length() != 0) {
		 	System.out.println(headerLine);		 	
		 }
		 
		 // Extract the filename from the request line.
	 	StringTokenizer tokens = new StringTokenizer(requestLine);
	 	tokens.nextToken();  // skip over the method, should be "GET"
	 	String fileName = tokens.nextToken();
	 	
	 	// Prepend a "." so that file request is within the current directory.
	 	fileName = "." + fileName;
	 	
	 	FileInputStream fis = null;
	 	boolean fileExists = true;
	 	try {
	 		fis = new FileInputStream(fileName);
	 	} catch (FileNotFoundException e) {
	 		fileExists = false;
	 	}
	 	
	 	// Construct the response message.
	 	String statusLine = null;
	 	String contentTypeLine = null;
	 	String entityBody = null;
	 	if (fileExists) {
	 		statusLine = "HTTP/1.1 200 OK";
	 		contentTypeLine = "Content-type: " + 
	 			contentType( fileName ) + CRLF;
	 	} else {
	 		statusLine = "HTTP/1.1 404" + CRLF;
	 		contentTypeLine = "Content-Type: text/html; charset=ISO-8859-1" + CRLF;
	 		entityBody = "<HTML>" + 
	 			"<HEAD><TITLE>Not Found</TITLE></HEAD>" +
	 			"<BODY>Not Found</BODY></HTML>";
	 	}
	 	
	 	os.writeBytes(statusLine);
	 	os.writeBytes(contentTypeLine);
	 	os.writeBytes(CRLF);
	 	
	 	if (fileExists)	{
	 		sendBytes(fis, os);
	 		fis.close();
	 	} else {
	 		os.writeBytes(entityBody);
	 	}
	 	
	 	os.close();
		in.close();
		socket.close();
	}
	
	private static void sendBytes(FileInputStream fis, DataOutputStream os) throws Exception
	{
	   byte[] buffer = new byte[1024];
	   int bytes = 0;

	   while((bytes = fis.read(buffer)) != -1 ) {
	      os.write(buffer, 0, bytes);
	   }
	}

	private String contentType(String fileName)
	{
		return new MimetypesFileTypeMap().getContentType(new File(fileName));
	}
	
}