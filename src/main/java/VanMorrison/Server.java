package VanMorrison;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.pdfbox.pdmodel.interactive.form.FieldUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


public class Server {

    public static final int PORT = 80;

    private String header = "",style ="", body = "", footer = "";

    CSVReader csv = new CSVReader();

    private static Server s;

    public static void main(String[] args) throws Exception {
        s = new Server();
        generateMain();
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
            generateMain();
        });

        server.createContext("/viewProvider", (HttpExchange t) -> {
            //String response = readHTML("src/viewProvider.html");
            HtmlParser h = new HtmlParser("src/viewProvider.html");
            StringBuilder htmlProviders = new StringBuilder();

            String[] pathnames;

            File folder = new File("provider");
            File[] listOfFiles = folder.listFiles();

            // Populates the array with names of files and directories
            pathnames = folder.list();

            // For each pathname in the pathnames array
            for (String pathname : pathnames) {
                // Print the names of files and directories
                pathname = pathname.substring(0,pathname.lastIndexOf("."));
                htmlProviders.append("<li><a href=\"/products/"+pathname+"\">"+pathname+"</a></li>");

            }

            h.set("lis", htmlProviders.toString());
            String response = h.getString();

            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();

        });

        server.createContext("/products", (HttpExchange t) -> {
            //String response = readHTML("src/viewProvider.html");
//            System.out.println("test");
//            HtmlParser h = new HtmlParser("src/viewProvider.html");
//            h.set("hej", "inte hej");
//            h.set("main", "main2");
//            h.set("vad är den", "här");
//
//            System.out.println(h.getString());

            String response = t.getRequestURI().toString();
            response = response.substring(10);



            byte[] bytes = response.getBytes();
            t.sendResponseHeaders(200, bytes.length);
            OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();

        });

        server.createContext("/addProvider", (HttpExchange t) -> {
            String response = readHTML("src/addProvider.html");
            Map<String, Parameter> params = htmlUtility.getMimeParameters(t.getRequestBody());

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



        server.createContext("/styles", (HttpExchange t) -> {
            // Add the required response header for a PDF file
            String path = "src/styles/viewProvider.css";

            File file = new File(path);
            System.out.println(file.canRead());
            if(!file.exists()){

                Headers h = t.getResponseHeaders();
                h.add("Content-Type", "text/css");

//                Path p = Paths.get(path);

                try{

                    FileInputStream fl = new FileInputStream(file);

                    // Now creating byte array of same length as file
                    byte[] bytes = new byte[(int)file.length()];

                    // Reading file content to byte array
                    // using standard read() method
                    fl.read(bytes);

                    // lastly closing an instance of file input stream
                    // to avoid memory leakage
                    fl.close();


                    //System.out.println(file.getAbsolutePath());
                    //byte[] bytes = Files.readAllBytes(file.toPath());
                    t.sendResponseHeaders(404, bytes.length);
                    OutputStream os = t.getResponseBody();
                    os.write(bytes);
                    os.close();
                }catch (Exception e){
                    System.out.println(e);
                }
                //System.out.println(bytes.length);

            }else{
                System.out.println("finns inte!!!");
                String message = "File was not found";
                byte[] messageBytes = message.getBytes();
                t.sendResponseHeaders(404, messageBytes.length);
                OutputStream os = t.getResponseBody();
                os.write(messageBytes);
                os.close();
            }
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

    public static void generateMain() {
        ///TODO: Add elements to the site by calling methods on s

        s.body = "";
        s.header = "<meta charset=\"UTF-16\">";
        s.addStyle();
        s.addBody("Hello world!");
        s.addBody(s.addProviderForm());
        s.addBody("<Br />");
        s.addBody("<a href=\"/pdf\" download=\"perfectOrder.pdf\">Download PDF</a>");

    }
}

