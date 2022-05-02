package VanMorrison;

import Data.Parameter;

import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.internet.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HTMLUtility {

    /**
     * @param requestStream The stream containing a MIME MultiPart form.
     * @return A Map from a parameter's name to a Parameter containing its data.
     */
    public static Map<String, Parameter> getMimeParameters(InputStream requestStream){
        try {
            Path tempPlaceToStoreData = Files.createTempFile(null, null);
            Files.copy(requestStream, tempPlaceToStoreData, StandardCopyOption.REPLACE_EXISTING);
            MimeMultipart formData = new MimeMultipart(new FileDataSource(tempPlaceToStoreData.toFile()));

            int count = formData.getCount();
            Map<String, Parameter> parts = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                BodyPart part = formData.getBodyPart(i);

                // See HTML 4.01 spec, section 17.13.4 at
                // https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2
                ContentDisposition disposition =
                        new ContentDisposition(((MimePart)part).getHeader("Content-Disposition", null));

                String name = disposition.getParameter("name");
                byte[] partData = part.getInputStream().readAllBytes();

                if (name.endsWith("[]")){
                    name = name.substring(0, name.length() - 2);
                    if (parts.containsKey(name)){
                        partData = combineData(parts.get(name).getData(), ",", partData);
                    }
                }
                Parameter parameterEntry = new Parameter((MimePart)part, partData);
                parts.put(name, parameterEntry);
            }

            Files.delete(tempPlaceToStoreData);

            return parts;
        } catch (Exception e) {
            System.out.println("Could not read mime " + e.getMessage());
            return new HashMap<>();
        }
    }

    private static byte[] combineData(byte[] first, String delimiter, byte[] second){
        byte[] delimiterData = delimiter.getBytes();
        int totalLength = first.length + second.length + delimiterData.length;

        byte[] total = Arrays.copyOf(first, totalLength);
        int writtenBytes = first.length;
        System.arraycopy(delimiterData, 0, total, first.length, delimiterData.length);
        writtenBytes += delimiterData.length;
        System.arraycopy(second, 0, total, writtenBytes, second.length);

        return  total;
    }
}
