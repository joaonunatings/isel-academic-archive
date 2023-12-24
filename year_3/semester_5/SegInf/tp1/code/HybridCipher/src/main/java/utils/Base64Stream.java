package utils;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Base64Stream {
    private final String filename;

    private FileInputStream fis;
    private FileOutputStream fos;
    Base64InputStream bis;
    Base64OutputStream bos;

    public Base64Stream(String filename) {
        this.filename = filename;
    }

    public Base64Stream(File file) {
        this.filename = file.getAbsolutePath();
    }

    public int read(byte[] _bytes) throws IOException {
        if (fis == null)
            fis = new FileInputStream(filename);
        if (bis == null)
            bis = new Base64InputStream(fis);
        return bis.read(_bytes);
        //int read = bis.read(_bytes, 0, _bytes.length);
        //bytes = _bytes;
        //bytes = bis.readAllBytes();
    }

    public void write(byte[] bytes) throws IOException {
        if (fos == null)
            fos = new FileOutputStream(filename);
        if (bos == null)
            bos = new Base64OutputStream(fos);
        bos.write(bytes);
    }

    public void close() throws IOException {
        if (bis != null) {
            bis.close();
        }
        if (fis != null) {
            fis.close();
        }
        if (bos != null) {
            bos.close();
        }
        if (fos != null) {
            fos.close();
        }
    }
}
