import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;

//Test Anton
public class Server {

    public static final int PORT = 80;

    private String header = "",style ="", body = "", footer = "";

    CSVReader csv = new CSVReader();

    public static void main(String[] args) throws Exception {
        Server s = new Server();

        ///TODO: Add elements to the site by calling methods on s
        s.addStyle();
        s.addBody("Hello world!");

        s.server.start();
    }

    /**
     * Adds the input html code to the end of the website's body.
     * @param html The code to add
     */
    public void addBody(String html) {
        body += html;
    }
    public void addStyle(){ style += csv.getStyle(); }

    HttpServer server;

    public Server() throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", (HttpExchange t) -> {
            String response =
                "<!doctype html>"+
                "<header>" +
                    style +
                    header +
                "</header>" +
                "<body>" +
                    body +
                        csv.printToString() +
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

