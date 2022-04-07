import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;

//Test Anton
public class Server {

    public static final int PORT = 80;

    public static void main(String[] args) throws Exception {
        Server s = new Server();
        s.server.start();

    }

    HttpServer server;

    public Server() throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", (HttpExchange t) -> {
            String response =
                    "<!doctype html>"+
                            "van morrison was here </br>"+
                            "Oh and hello, world";
            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        });
    }
}

