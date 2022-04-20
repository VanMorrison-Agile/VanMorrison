import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

//Test Anton
public class Server {

    public static final int PORT = 80;

    private String header = "", body = "", footer = "";

    public static void main(String[] args) throws Exception {
        Server s = new Server();
        ///TODO: Add elements to the site by calling methods on s
        s.addBody("Hello world!");

        s.addBody(s.addProviderForm());

        s.server.start();
    }

    /**
     * Adds the input html code to the end of the website's body.
     * @param html The code to add
     */
    public void addBody(String html) {
        body += html;
    }


    HttpServer server;

    public Server() throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", (HttpExchange t) -> {
            String response =
                "<!doctype html>"+
                "<header>" +
                    header +
                "</header>" +
                "<body>" +
                    body +
                "</body>" +
                "<footer>" +
                    footer +
                "</header>";
            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        });
    }

    public String addProviderForm(){
        StringBuilder aa = new StringBuilder();
        try {
            FileReader reader = new FileReader("src/form.html");
            while (reader.ready()) aa.append((char)reader.read());
        } catch (Exception e) {System.out.println(e);}
        String readData = aa.toString();

        new File("provider/").list();


        aa = new StringBuilder();
        File folder = new File("provider");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                String companyName = listOfFiles[i].getName();
                if (companyName.contains(".")){
                    companyName = companyName.substring(0, companyName.lastIndexOf('.'));
                }

                aa.append("<option value=" + companyName + ">" + companyName + "</option>");
            }
        }

        return readData.replace("#OPTIONS#", aa.toString());
    }
}

