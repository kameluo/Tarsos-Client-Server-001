package mypackagedemo4;

public interface clientInterface {
	String disconnectRequestMessage="DQR";//Sending a "DRQ" message to the server like an acknowledgement of the disconnect request he sent
	String serverON="SON";//"SEVRON"-->means that the Server is logging in and waiting for receiving the messages from the sender"clients"
	String connectionRequest="CRQ";//Sending a "CRQ" message to the receiver like an acknowledgement
	String readyToReceive="200";//if The Client Receives "200" it means that the Server is Ready to get Receives the Sound States
	String serverWantsDisconnect="555";//"555" means that the server wants to disconnect
}
