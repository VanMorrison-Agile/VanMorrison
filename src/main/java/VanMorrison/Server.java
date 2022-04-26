package VanMorrison;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Map;

import Data.Parameter;

public class Server {

    public static final int PORT = 80;

    private String header = "",style ="", body = "", footer = "", script = "";

    CSVReader csv = new CSVReader();

    private static Server s;

    public static void main(String[] args) throws Exception {
        s = new Server();
        s.generateMain();
        s.server.start();
    }

    /**
     * Adds the input html code to the end of the website's body.
     * @param html The code to add
     */
    public void addBody(String html) {
        body += html;
    }

    public void addScript(String javascript) {
        script += javascript;
    }

    private void addStyle(){ style += csv.getStyle(); }

    HttpServer server;

    public Server() throws Exception {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", (HttpExchange t) -> {
            String response =
                "<!doctype html>" +
                        "<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "</head>"+
                "<header>" +
                    style +
                    header +
                "</header>" +
                "<body>" +
                    body +
                "</body>" +
                "<footer>" +
                    footer +
                "</footer>" +
                        "<script>" +
                        script +
                        "</script>" +
                        "</html>";
            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();
            generateMain();
        });

        server.createContext("/addProvider", (HttpExchange t) -> {
            String response = readHTML("src/addProvider.html");
            Map<String, Data.Parameter> params = HTMLUtility.getMimeParameters(t.getRequestBody());

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
            try {
                generateMain();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

                aa.append("<option value=\"" + companyName + "\">" + companyName + "</option>");
            }
        }

        return readData.replace("#OPTIONS#", aa.toString());
    }

    public void generateMain() {
        ///TODO: Add elements to the site by calling methods on s

        body = "";
        header = "<meta charset=\"UTF-16\">";
        addStyle();
        addBody("Hello world!");
        addBody(addProviderForm());
        addBody("<Br />");
        addBody("<a href=\"/pdf\" download=\"perfectOrder.pdf\">Download PDF</a>");

        addBody(csv.printToString());

        addScript(
                "function addItem(id) {\n" +
                        "  alert('Replace this alert with adding item ' + id);\n" +
                        "}"
        );

    }
}

