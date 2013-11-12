import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
 
/**
 * This is an IRC bot I wrote a long time ago in 10th grade.
 * Feel free to use this to teach yourself the IRC protocol. 
 * User: Vartan
 * Date: Jun 5, 2010
 * Time: 5:57:43 PM
 */
 
 
public class Client {
    public static void main(String[] args) {
        Client c = new Client("irc.rizon.net", "Montgomery");
    }
 
    BufferedWriter out;
    BufferedReader in;
    String name;
    String password = "pwd";
 
    public Client(String server, String username) {
        int port = 6667;
        name = username;
        if (server.contains(":")) {
            port = Integer.parseInt(server.substring(server.indexOf(":") + 1));
            server = server.substring(server.indexOf(":"));
        }
        try {
            Socket irc = new Socket(server, port);
            out = new BufferedWriter(new OutputStreamWriter(irc.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(irc.getInputStream()));
            out.write("NICK " + username);
            out.newLine();
            out.write("USER MIKE_BOT Mike__ total-anarchy.net JAVA :Mike__'s Bot");
            out.newLine();
            out.flush();
            out.write("JOIN #Montgomery");
            out.newLine();

            out.flush();
            String message = null;
            while ((message = in.readLine()) != null) {
                onRawCode(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public void onRawCode(String raw) {
        String[] h = raw.split(":", 2);
        h[0] = h[0].trim();
        if (h[0].length() == 0) {
            onRawMessage(h[1]);
            return;
        }
        if (h[0].equalsIgnoreCase("PING")) {
            onPing(h[1]);
            return;
        }
        if (h[0].equalsIgnoreCase("ERROR")) {
            onError(h[1]);
            return;
        }
        System.out.println("!!!!!!!UNHANDLED RAW CODE!!!!!!!!");
        System.out.println(raw);
    }
 
    public void onRawMessage(String raw) {
        try {
            String user="";
            String hostName="";
            String type="PARSE ERROR";
            String[] a1= new String[0];
            String[] a2= new String[0];
            String[] a3= new String[0];
            try {
            a1 = raw.split(":", 2);
            a2 = a1[0].split(" ");
            a3 = a2[0].split("!");
            user = a3[0];
            hostName = a3[1];
            type = a2[1];
            }catch(Exception e) {
               
            }
 
            if (type.equalsIgnoreCase("PRIVMSG")) {
                String message = a1[1];
                String channel = a2[2];
 
                onMessage(user, hostName, channel, message);
                return;
            }
            if (type.equalsIgnoreCase("NOTICE")) {
                String message = a1[1];
                String channel = a2[2];
 
                onNotice(user, hostName, channel, message);
                return;
            }
            if (type.equalsIgnoreCase("MODE")) {
                String message = a1[1];
                String channel = a2[2];
 
                onModeChange(user, hostName, channel, message);
                return;
            }
            if (type.equalsIgnoreCase("KICK")) {
                String message="";
                String channel="";
                String recipient="";
                try {
                 message = a1[1];
                 channel = a2[2];
                 recipient = a2[3];
                }catch(Exception e) {
                }                    
                onKick(user, hostName, channel, recipient, message);
                return;
            }
            if (type.equalsIgnoreCase("JOIN")) {
                String channel="";
                try {
                channel = a1[1];
                }catch(Exception e) {
                }                    
                onJoin(user, channel);
                return;
            }
            if (type.equalsIgnoreCase("PART")) {
                String message="";
                String channel="";
                try {
                message = a1[1];
                channel = a2[2];
                }catch(Exception e) {
                }                    
                onPart(user, hostName, channel, message);
                return;
            }
            if (type.equalsIgnoreCase("QUIT")) {
                String message="";
                try {
                 message = a1[1];
                }catch(Exception e) {
                   
                }
                onQuit(user, hostName, message);
                return;
            }
            if (type.equalsIgnoreCase("NICK")) {
                String n = a1[1];
                onNickChange(user, n, hostName);
                return;
            }
 
            System.out.println("!!!!!!!!!!UNHANDLED TYPE: " + type + "!!!!!!!!!!");
            System.out.println(raw);
        } catch (Exception e) {
            System.out.println("Unparsable: " + raw);
        }
    }
 
    public void onNickChange(String old, String n, String hostName) {
        if (old.equals(name))
            name = n;
        System.out.println(old + "[" + hostName + "] changed their name to: " + n);
    }
 
    public void onJoin(String user, String channel) {
        ghost(user);
        System.out.println("<" + user + "> joined " + channel + ".");
    }
 
    public void onPart(String user, String hostName, String channel, String message) {
        System.out.println("<" + user + "[" + hostName + "]> has parted from " + channel + " (" + message + ")");
    }
 
    public void onQuit(String user, String hostName, String message) {
        System.out.println("<" + user + "[" + hostName + "]> has quit (" + message + ")");
    }
 
    public void onMessage(String user, String hostName, String channel, String message) {
        System.out.println(channel + " <" + user + ">: " + message);
 
    }
 
    public void onNotice(String user, String hostName, String channel, String message) {
        System.out.println("NOTICE <" + user + ">: " + message);
        if (user.equalsIgnoreCase("nickserv")) {
            if (message.toLowerCase().contains("/msg nickserv identify")) {
                try {
                    System.out.println("LOGGING IN");
                    out.write("PRIVMSG Nickserv :identify " + password);
                    out.newLine();
                    out.flush();
                } catch (Exception e) {
 
                }
            }
        }
    }
 
    public void onModeChange(String user, String hostName, String channel, String type) {
        System.out.println("MODE CHANGE <" + user + ">: " + type);
 
    }
 
    public void ghost(String user) {
        
    }
 
 
    public void onKick(String user, String hostName, String channel, String recipient, String message) {
        
 
    }
 
    public void onPing(String host) {
        try {
            out.write("PONG " + host);
            out.newLine();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
    public void onError(String error) {
        System.out.println("!!!!!!!!!!ERROR!!!!!!!!!");
        System.out.println(error);
    }
}
