import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;

//Test Anton
public class Server {

    public static final int PORT = 80;

    private String header = "", body = "", footer = "";

    CSVReader csv = new CSVReader();

    public static void main(String[] args) throws Exception {
        Server s = new Server();

        ///TODO: Add elements to the site by calling methods on s
        s.addBody("Hello world!"
                );

        s.server.start();
    }

    /**
     * Adds the input html code to the end of the website's body.
     * @param html The code to add
     */
    public void addBody(String html) {
        body += html;
    }

    public String generateList(){
        String s = "<br />" +
                "<table>" +
                "<tr>" +
                   generateTableHead() +
                "</tr>" +
                generateTableRows() +
                "</table>";
        return s;
    }

    public String generateTableRows(){
        String s = "";
            for(int i = 1; i < csv.getItemList().size(); i++){
                s += "<tr><td>" + csv.getItemList().get(i).getArtNr() + "</td><td>" + csv.getItemList().get(i).getName() + "</td><td>" + csv.getItemList().get(i).getPrice() + "</td></tr>";
            }
        return s;
    }

    public String generateTableHead(){
        String s = "<th>";
        s += csv.getItemList().get(0).getArtNr() + "</th><th>" + csv.getItemList().get(0).getName() + "</th><th>" + csv.getItemList().get(0).getPrice() + "</th>";
        return s;
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
                        generateList() +
                "</body>" +
                "<footer>" +
                    footer +
                "</footer></html>";
            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        });
    }
}

