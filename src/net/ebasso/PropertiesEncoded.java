package net.ebasso;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesEncoded {

    private String INIFILE;
    private Properties props;

    public PropertiesEncoded() {
    }

    public PropertiesEncoded(String inifile) throws Exception {
        this.INIFILE = inifile;
        this.loadAppProperties();
    }

    private void loadAppProperties() throws Exception {

        props = new Properties();
        FileInputStream file = new FileInputStream(INIFILE);
        props.load(file);

    }

    /*
    * Get Encode Password and return as plain text
     */
    public static String getPropertyPassword(String propertyValue) {
        String strAux = propertyValue;
        String strEncryptedPassword;
        String strRetorno = "";

        if (strAux == null) {
            return strRetorno;
        }
        strAux = propertyValue.trim();
        if (strAux.equals("")) {
            return strRetorno;
        }
        if (strAux.startsWith("ENC(") && strAux.endsWith(")")) {
            strEncryptedPassword = strAux.substring(4, strAux.length() - 1);
            strRetorno = EncryptAll.decryptString(strEncryptedPassword);
        }

        return strRetorno;
    }
}
