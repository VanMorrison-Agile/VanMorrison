import Data.Parameter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Map;

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

        server.createContext("/addProvider", (HttpExchange t) -> {
            String response = readHTML("src/addProvider.html");
            Map<String, Parameter> params = htmlUtility.getParameters(t.getRequestBody());

            //DO BUSINESS LOGIC
            byte[] csvData = params.get("uploadedFile").getData();
            String prov = params.get("provider").getDataAsString();

            if (prov.equals("new")) {
                prov = params.get("uploadedFile").getFileName();
                if (prov.lastIndexOf(".") != -1)
                    prov = prov.substring(0,prov.lastIndexOf("."));
            }

            String tempString = "provider/" + prov + ".csv";
            File file = new File(tempString);
            file.delete();
            file.createNewFile();
            try {

                FileOutputStream writer = new FileOutputStream(file);
                writer.write(csvData);
                writer.close();

            } catch (Exception e) {
                e.getStackTrace();
            }


            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
        });
    }


    public String readHTML(String filename){
        StringBuilder html = new StringBuilder();
        try {
            FileReader reader = new FileReader(filename);
            while (reader.ready()) html.append((char)reader.read());
        } catch (Exception e) {System.out.println(e);}
        return html.toString();
    }

    /**
     * Compiles a form for creating/updating product lists for the current providers or a new one.
     * @return A complete html form represented as a string
     */
    public String addProviderForm(){
        String readData = readHTML("src/form.html");

        StringBuilder aa = new StringBuilder();
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
