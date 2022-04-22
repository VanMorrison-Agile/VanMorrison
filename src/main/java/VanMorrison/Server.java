package VanMorrison;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Server {

    public static final int PORT = 80;

    private String header = "",style ="", body = "", footer = "";

    CSVReader csv = new CSVReader();

    public static void main(String[] args) throws Exception {
        Server s = new Server();
        s.addBody("Hello world!");
        s.addBody("<Br />");
        s.addBody("<a href=\"/pdf\" download=\"perfectOrder.pdf\">Download PDF</a>");

        s.server.start();
    }

    /**
     * Adds the input html code to the end of the website's body.
     * @param html The code to add
     */
    public void addBody(String html) {
        body += html;
    }
    private void addStyle(){ style += csv.getStyle(); }

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
        
        server.createContext("/pdf", (HttpExchange t) -> {
            // Add the required response header for a PDF file
            Headers h = t.getResponseHeaders();
            h.add("Content-Type", "application/pdf");

            //Get byte array containing pdf
            byte [] docBytes = PDFExport.getPdf();

            // Send the response.
            t.sendResponseHeaders(200, docBytes.length);
            OutputStream os = t.getResponseBody();
            os.write(docBytes,0, docBytes.length);
            os.close();
        });
    }
}

