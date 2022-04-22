import Data.Parameter;
import com.sun.mail.util.MimeUtil;

import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class htmlUtility {
    public static Map<String, Parameter> getParameters(InputStream requestStream){
        try {
            Path tempPlaceToStoreData = Files.createTempFile(null, null);
            System.out.println("EE " + requestStream.available());
            Files.copy(requestStream, tempPlaceToStoreData, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("GOT: " + Files.readAllBytes(tempPlaceToStoreData).length + "\nDATA: " + new String(Files.readAllBytes(tempPlaceToStoreData)));
            MimeMultipart formData = new MimeMultipart(new FileDataSource(tempPlaceToStoreData.toFile()));

            int count = formData.getCount();
            Map<String, Parameter> parts = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                BodyPart part = formData.getBodyPart(i);

                // See HTML 4.01 spec, section 17.13.4 at
                // https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2
                ContentDisposition disposition =
                        new ContentDisposition(((MimePart)part).getHeader("Content-Disposition", (String)null));

                System.out.println("DIS \n" + part.getDisposition());
                System.out.println("FILE " + part.getFileName());
                String name = disposition.getParameter("name");
                byte[] partData = part.getInputStream().readAllBytes();
                Parameter parameterEntry = new Parameter(formData.getBodyPart(i), partData);
                parts.put(name, parameterEntry);

                Iterator<String> allParams = disposition.getParameterList().getNames().asIterator();
                while(allParams.hasNext()){
                    System.out.println("HAS PARAM: " + allParams.next());
                }

                /*if (fileInputControlName.equals(name)) {
                    Path saveFile = saveDir.resolve(part.getFileName());
                    try (InputStream content = part.getInputStream()) {
                        Files.copy(content, saveFile);
                    }
                    break;
                }*/
            }

            Files.delete(tempPlaceToStoreData);

            return parts;
        } catch (Exception e) {
            System.out.println("Could not read mime " + e.getMessage());
            return new HashMap<>();
        }
    }

    static String getName(MimePart part) throws MessagingException {
        String filename = null;
        String s = part.getHeader("Content-Disposition", (String)null);
        if (s != null) {
            ContentDisposition cd = new ContentDisposition(s);
            filename = cd.getParameter("name");
        }

        if (filename == null) {
            s = part.getHeader("Content-Type", (String)null);
            s = MimeUtil.cleanContentType(part, s);
            if (s != null) {
                try {
                    ContentType ct = new ContentType(s);
                    filename = ct.getParameter("name");
                } catch (ParseException var5) {
                }
            }
        }

        /*if (decodeFileName && filename != null) {
            try {
                filename = MimeUtility.decodeText(filename);
            } catch (UnsupportedEncodingException var4) {
                throw new MessagingException("Can't decode filename", var4);
            }
        }*/

        return filename;
    }
}
