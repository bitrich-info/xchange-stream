package info.bitrich.xchangestream.cexio;

import java.io.*;
import java.util.Properties;

public class CexioProperties {

    private String apiKey;
    private String secretKey;
    private final String fileName = "xchange-cexio/local.properties";

    public CexioProperties() throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            createFile();
        }
        Properties properties = new Properties();
        InputStream input = new FileInputStream(fileName);
        properties.load(input);
        apiKey = properties.getProperty("api-key");
        secretKey = properties.getProperty("secret-key");
        input.close();
    }

    private void createFile() throws IOException {
        Properties properties = new Properties();
        OutputStream output = new FileOutputStream(fileName);
        properties.setProperty("api-key", "");
        properties.setProperty("secret-key", "");
        properties.store(output, null);
        output.close();
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

}
