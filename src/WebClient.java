import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.StringTokenizer;

public class WebClient {
    final static String CRLF="\r\n";
    public static void main(String[] args)throws IOException {
//Extracting connection parameters from args[]:
        String host=args[0];
        String filePath=null;
// If the port number is not entered, the default port 8080 should be used.
        int port=8080;
        if(args.length<3){
             filePath=args[1];
        }else{
             port=Integer.parseInt(args[1]);
             filePath=args[2];
        }
//Request-Line constructing
        String requetsLine="GET "+filePath+" HTTP/1.1"+CRLF;

//Start of measuring round trip time
        long start=System.nanoTime();

        Socket socket=new Socket(host,port);
//Need for reading from/writing to Socket
        InputStreamReader isr=new InputStreamReader(socket.getInputStream());
        PrintWriter writer=new PrintWriter(socket.getOutputStream());
        BufferedReader reader=new BufferedReader(isr);
//Header-Line constructing
        String headerLine="Host: "+host+":"+port;
//Sending request to server
        writer.write(requetsLine);
        writer.write(headerLine);
        writer.flush();


//Extracting Status-Line from Response-Message as it is the first line
        String statusLine=reader.readLine();
        System.out.println(statusLine);
//Extracting protocol from Status-Line
        String protocol= new StringTokenizer(statusLine).nextToken();

        String line=null;
        while((line=reader.readLine()).length()!=0){
            System.out.println(line);
        }

//        Read out Entity-body (body of requested file) if it presents
        if("HTTP/1.1 200 OK".equals(statusLine)) {
            while(reader.ready()) {
                char[] buff=new char[1024];
                reader.read(buff);
                System.out.println(String.copyValueOf(buff));
            }
        }
//        End of measuring round trip time
        long end=System.nanoTime();

        System.out.println("**************");
        System.out.println("Round Trip Time: "+(double)(end-start)/1000000+" milliseconds");
        String hostName=socket.getInetAddress().getHostName();
        System.out.println("Server HostName: "+
          ("127.0.0.1".equals(hostName)?"localhost":hostName));

//        0.0.0.0 and 0:0:0:0:0:0:0:0 for IPv4 and IPv6 accordingly
        System.out.println("Socket family: "+
         (host.split("\\.").length==4?StandardProtocolFamily.INET:StandardProtocolFamily.INET6));
        System.out.println("Protocol: "+protocol);
        System.out.println("Socket timeout: "+(socket.getSoTimeout()==0?"infinity":socket.getSoTimeout()));
        System.out.println(Inet4Address.getLocalHost());

        reader.close();
        writer.close();
        socket.close();
    }
}
