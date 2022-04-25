package Data;

import javax.mail.MessagingException;
import javax.mail.internet.ContentDisposition;
import javax.mail.internet.MimePart;

/**
 * A wrapper around a parameter which can easily report info about the contained parameter
 */
public class Parameter{
    MimePart body;
    byte[] data;

    public Parameter(MimePart body, byte[] data){
        this.body = body;
        this.data = data;
    }

    /**
     * @return The data carried by parameter.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @return The parameter's data as a string.
     */
    public String getDataAsString(){ return new String(data); }

    /**
     * Only works if the parameter contains filename data
     * @return The file name of a "file" type parameter
     */
    public String getFileName(){
        try {
            return body.getFileName();
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Unable to read data: \"filename\" from parameter";
        }
    }

    /**
     * @return The name of the parameter
     */
    public String getParameterName(){
        return getDisposition().getParameter("name");
    }

    private ContentDisposition getDisposition(){
        try {
            return new ContentDisposition((body).getHeader("Content-Disposition", null));
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
