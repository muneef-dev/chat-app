import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        // 1st ikke client side lem socket ondu open senji kolonum
        // (and server side lem socket open aahanum) 2 perukkum data communicate seyya aahum

        try {
            Socket socket = new Socket("localhost", 8000);// port 8000 aa eera socket a open seyyanum
            // localhost or 127.0.0.1 host aa kudukkalaam and server ezo port a use pannino aze thaan ingem varanum

            // client ondu maai data edukkavum oonum data send seyyavum oonum
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);

            System.out.println(bufferedReader.readLine()); // ennamaalum client type pannina azu print aahum
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the Name : ");
            String clientName = scanner.next();
            printWriter.println(clientName);

            // server response i eduththal
            String responseData = bufferedReader.readLine();
            System.out.println(responseData);

            // response data va print senjonna thread onda uruvaakki aza run sei
            Thread thread = new Thread(new MessageHandler(socket));
            thread.start();

            // messages a continue a veachchi kola while loop oonum
            while (true){
                String message = scanner.nextLine();
                printWriter.println(message);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class MessageHandler implements Runnable {
    // server side il client a manage seyya class ondu theawa maai client side la message a manage seyya class thewa

    private Socket socket; // data va catch seyya socket oonum

    MessageHandler(Socket socket){ // MessageHandler class a call senja socket a pass seiyyanum
        this.socket=socket;
    }

    @Override
    public void run() {
        try {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            while ((message=bufferedReader.readLine())!=null){
                System.out.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
