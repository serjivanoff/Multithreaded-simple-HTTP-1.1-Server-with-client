import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardProtocolFamily;
import java.util.StringTokenizer;

public final class WebServer {

        public static void main(String[] args) throws Exception{
            // Set the port number.
            int port = Integer.parseInt(args[0]);

            // Establish the listen socket.
            ServerSocket serverSocket=new ServerSocket(port);

            // Process HTTP service requests in an infinite loop.
            while(true){
                // Listen for a TCP connection request.
                System.out.println("Waiting for connection...");
                Socket in=serverSocket.accept();
                // Construct an object to process the HTTP request message.
                HttpRequest request=new HttpRequest(in);
                // Create a new thread to process the request.
                Thread thread=new Thread(request);
                // Start the thread.

                thread.start();
            }
        }
    }
final class HttpRequest implements Runnable{
    Socket socket;
    final static String CRLF="\r\n";
    // Constructor
    public HttpRequest(Socket socket)throws Exception {
        this.socket = socket;
    }
    // Implement the run() method of the Runnable interface.
    public void run(){
        try{
            processRequest();
        }catch (Exception e){
//            System.out.println(e);
            e.printStackTrace();
        }
    }
    private void processRequest()throws Exception{
        // Get a reference to the socket's input and output streams.
        InputStream is=socket.getInputStream();

        DataOutputStream os=new DataOutputStream(socket.getOutputStream());
        // Set up input stream filters.
        InputStreamReader isr=new InputStreamReader(is);
        BufferedReader br=new BufferedReader(isr);

        // Get the request line of the HTTP request message.
        String requestLine=br.readLine();
        // Display the request line.
        System.out.println();
        System.out.println(requestLine);

        // Extract the filename from the request line.
        StringTokenizer token=new StringTokenizer(requestLine);
        // skip over the method, which should be "GET"
        token.nextToken();
        String fileName=token.nextToken();
        String protocol=token.nextToken();
//        fileName="."+fileName;

        // Open the requested file.
        FileInputStream fis=null;
        boolean fileExists=true;
            try{
                fis=new FileInputStream(fileName);
               }catch (FileNotFoundException ex){
                System.out.println("file not found"+ex);
                fileExists=false;}
        // Construct the response message.
        String statusLine=null;
        String contentTypeLine=null;
        String entityBody=null;
        if(fileExists){
            statusLine="HTTP/1.1 200 OK"+CRLF;

            contentTypeLine="Content-Type: "+contentType(fileName)+CRLF;
                    }else{
            statusLine="HTTP/1.1 404 NOT FOUND"+CRLF;
            contentTypeLine="";
            entityBody="<HTML>" +
                    "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                    "<BODY>Body not found</BODY></HTML>";
        }
        // Send the status line.
        os.writeBytes(statusLine);
        // Send the content type line.
        os.writeBytes(contentTypeLine);
        // Send a blank line to indicate the end of the header lines.
        os.writeBytes(CRLF);
        // Send the entity body.
        if(fileExists){
            sendBytes(fis,os);
            fis.close();
                      }else {os.writeBytes(entityBody);}

        // Get and display the header lines.
        String headerLine=null;
        try{
        while((headerLine=br.readLine()).length()!=0){
            System.out.println(headerLine);
        }}catch (NullPointerException ignored){}


        System.out.println("**************");
        String clientHostName=socket.getInetAddress().getHostName();
        System.out.println("Client HostName: "+
                ("127.0.0.1".equals(clientHostName)?"localhost":clientHostName));
        System.out.println("Socket family: "+
                (clientHostName.split("\\.").length==4? StandardProtocolFamily.INET:StandardProtocolFamily.INET6));
        System.out.println("Protocol: "+protocol);
        System.out.println("Socket timeout: "+(socket.getSoTimeout()==0?"infinity":socket.getSoTimeout()));

        os.close();is.close();
        br.close();isr.close();
        socket.close();
    }

    private static void sendBytes(FileInputStream fis,DataOutputStream os)throws IOException{
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[]buffer=new byte[1024];
        int bytes=0;
        // Copy requested file into the socket's output stream.
        while((bytes=fis.read(buffer))!=-1){
            os.write(buffer,0,bytes);
        }
    }

    private static String contentType(String fileName){
        if(fileName.endsWith(".html")||fileName.endsWith(".htm")){return "text/html";}
        if(fileName.endsWith(".jpg")||fileName.endsWith(".jpeg")){return "image/jpeg";}
        if(fileName.endsWith(".gif")){return "image/gif";}
        return "application/octet/stream";
    }
}