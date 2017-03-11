The server is assumed to work with HTTP GET messages.
• If running the server program using command line, the syntax should be
server_code_name < port_number >

Server  displays the request and header lines of request messages
on the  for the purpose of debugging.

• The client should be able to initiate a connection to the server, via a socket
and request any page on the server. Upon receipt of the response message
from the server, the client extracts and displays/logs the message status

You may execute the client program using command line, with the follow-ing syntax,
client_code < server_IP address >< port_no ><requested_file_name >

(a) Server_IPaddress: The IP address or name of the Web server, e.g.,
127.0.0.1 or localhost for the server running on the local machine.

(b) port_no: The port on which the server is listening to contnections

from clients. If the port number is not entered, the default port 8080
should be used.

(c) requested_file_name: The name of the requested file, which may
include the path to the file.
