import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    // broadcast part 1111
    private static List<Handler> clientsList = new ArrayList<>(); // clientList or handlerList

    public static void main(String[] args) {
        // indha project run aahum pozu
        // 1st ikke server socket(door/port) ondu open seyyanum (its like a door data va angem ingem transfer panna open seira door or entry point)
        // so ServerSocket da instance onda uruvaakki port onda assign pannanum
        try {
            ServerSocket serverSocket = new ServerSocket(8000); // port -> 8000 // so ippa server run aahum
            System.out.println("server has started..., on port 8000");
            // ippa ihina veachchi run senja client ikkaha wait pannama server start aahum port 8000 ill azoda appidiye terminate aahum

            while (true){
                // infinite loop use seyya reason system or indha resource (app) i use seyra ella client eam edukkavaahum
                // server side in responsibility vandhu eppvum client ikku response or service i koduppazaahum

                Socket socket = serverSocket.accept(); // accept() method return seyrazu socket object onda i aahum
                // indha server ikku client request ondai poattavudan indha while loop execute/trigger aahi indha socket
                // moolam andha resource i pudichchi keele pohum pozu sout print aahum (with internet address udan)
                System.out.println("Client connected..."+ socket.getInetAddress().getAddress());

                // so client i accept senjonne aza client a handle seyya vendi varum so veraya Handler i create seyya vendi varum

                // client a ippa summa connect aahura tha check seyya go Chrome -> localhost:8000 (type that)

                // client connected message vandhonna izu nadakkanum (iza client list ikku add aahanum)
                Handler handler = new Handler(socket);
                clientsList.add(handler);
                new Thread(handler).start(); // indha thread a trigger pannanum handler ennum runnable target a kuduththu
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // broadcast part 1111
    static void broadcast(String message, Handler handler){
        // iza method ikku static poda reason instance seyyama use seyya aahum
        // indha method trigger seyvathaayin message and Handler i anuppu (pass sei)
        // next system la eera ellarukkum (ella client ikku message a send seyyanum) so client a vechchila list ondu theawa

        // yaarachchcu message anppina clientsList inga iterate aahum (100 clients system ikku load aaheendha 100 um iterate aahum)
        for (Handler clients: clientsList
             ) {
            if(clients!=handler){
                // iza poda reason ex: naan anuppura message enakke varappada ve (naanum clientList la eere as a client aa so appidi nadakkeala)
                // broadcast() method ikku vaara client ikku clientList la eera client equal illatti if ulla eeratha print sei

                // ovvoru client aa indha loop aala access senji ovvondukkum iza message a pass sei
                clients.sendMessage(handler.getClientName() + " : " + message);

            }
        }
    }
}

class Handler implements Runnable { // Handler kattayam Runnable interface i implements seyyanum

    // iza reference a uruvaakki kolanum
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String clientName;
    private Socket socket;

    // client a handle seyya mehtod ondu theawa
    // azukku (constructor a poattaalum ok) kattaayam socket pass senje aahanum
    public Handler(Socket socket){
        this.socket=socket; // izaala socket initialize aahum
    }

    // client system (chat app) ikku request senyyum pozu yaaru iza chat a pandro avarda name a veachchi kolanum
    public String getClientName(){
        return clientName;
    }

    // message onda send seyya (senjandu sella) iza method thewa
    public void sendMessage(String message){
        printWriter.println(message); // print writer print seyyum to more theadu
    }

    @Override
    public void run() { // izil thread run aahum
        try {
            // datava pass seyya and datava edukka thewai aana environment i uruvaakkuzal
            // client ondu system ikku join aahinonna data edukka or datava kudukka environment i uruvaakkuzal
            // initialize bufferReader
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // pass aahum data va grab panna/eudkka thaan bufferedReader theawa so izaala InputStreamReader datas a kalatti edukka mudiyum
            // initialize printWriter
            printWriter = new PrintWriter(socket.getOutputStream(),true); // flush (close)

            printWriter.println("Hello, what is your name?"); // write this message
            clientName = bufferedReader.readLine();// (name a enter seyya buffer reader oonum) so name a client enter
            // seyratha read seithal -> terminal la type seyratha read/grab seyyalaam so client enter seyyum name a clientName variable ikku poadu

            System.out.println(clientName+": Connected..."); // client name udan izu print aahum

            // so ella client ikku oru messege poha veachchalaam that client connect aahi eeraar ena (oru client chat group ikku connect aahi ena sollalaam)

            // broadcast part 1111 (after meala eera part)
            // client connect aahino porahi izu nadakkanum
            Server.broadcast("has joined",this); // this mean indha hanlder (indha object) a eduththuttu po or anupputhal
            String message;
            while ((message=bufferedReader.readLine())!=null){ // bufferedReader vaara message a message ikku assign sei and iterate sei azu null illanda
                Server.broadcast(message,this);
            }
            // loop la eendhu veliya vandha (client left aana)
            Server.broadcast("user left",this);

            // izellam close seirathu best practice aahum
            bufferedReader.close();
            printWriter.close();
            socket.close();
            // izellam close senjonna server da side la vechchikondeendha sockets ellam release seyyum so vera client ondu kku azoda join aahalaam

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}