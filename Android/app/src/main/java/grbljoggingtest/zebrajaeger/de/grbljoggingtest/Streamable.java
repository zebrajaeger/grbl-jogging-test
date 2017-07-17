package grbljoggingtest.zebrajaeger.de.grbljoggingtest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Lars Brandt on 16.07.2017.
 */
public interface Streamable {
    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;
}
