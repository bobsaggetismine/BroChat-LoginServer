import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.sql.SQLException;

public class LoginServer
{

	private Thread t_rec;
	private Thread t_snd;
	
	private DatagramSocket socket;
	
	private static final int PORT = 8193;
	
	public LoginServer()
	{
		try 
		{
			socket = new DatagramSocket(PORT);
			System.out.println("Open on port: " + socket.getLocalPort());
		} 
		catch (SocketException e) 
		{
			System.out.println("Failed to bind to port: "+PORT + " because: "+e.getLocalizedMessage());
		}
		waitForRequest();
	}
	private void waitForRequest()
	{
		t_rec = new Thread(){
			public void run() {
				while (true) {
					
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try
					{
						socket.receive(packet);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					try
					{
						String req = new String(packet.getData(), packet.getOffset(), packet.getLength());
						if (req.startsWith("/c/")){
							String[] tokens = req.split("/c/|/n/|/n/|/e/");
							replyToRequest(tokens,packet);
						}else if (req.startsWith("/ca/"))
						{
							String[] hold = req.split("/ca/|/n/");
							createUser(hold[1],hold[2],packet);
						}
						
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		};
		t_rec.start();
	}
	private void createUser(String username, String password,DatagramPacket packet) throws SQLException, IOException
	{
		System.out.println(password);
		String msg = SQLChecker.createUser(username, password);
		byte[] data = new byte[1024];
		data = msg.getBytes();
		System.out.println(msg);
		DatagramPacket newPacket = new DatagramPacket(data,data.length,packet.getAddress(),packet.getPort());
		socket.send(newPacket);
	}
	private void replyToRequest(String[] tokens,DatagramPacket packet)
	{
		t_snd = new Thread(){
			
			public void run()
			{
				try
				{
					if(SQLChecker.CheckPassSQL(tokens[2], tokens[3]))
					{
						System.out.println("user approved!");
						byte[] data = new byte[1024];
						String message = "/a/"+tokens[1]+"/e/";
						data = message.getBytes();
						DatagramPacket replyPacket = new DatagramPacket(data,data.length,packet.getAddress(),packet.getPort());
						socket.send(replyPacket);
					}
					else
					{
						System.out.println("user declined!");
						byte[] data = new byte[1024];
						String message = "/d/"+tokens[1]+"/e/";
						data = message.getBytes();
						DatagramPacket replyPacket = new DatagramPacket(data,data.length,packet.getAddress(),packet.getPort());
						socket.send(replyPacket);
					}
				}
				catch (SQLException | IOException e)
				{
					e.printStackTrace();
				}
			}
			
		};
		t_snd.start();
	}
	public static void main(String[] args)
	{
		new LoginServer();
	}
}
