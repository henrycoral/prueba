package ejemplo.servicio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Test {
	private static RespuestaSolicitud validarComprobante(byte[] xml) {
        RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService();
        RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
        return port.validarComprobante(xml);
    }
	
	
	public static void main(String[] args)  throws IOException {
		 /*System.setProperty("javax.net.ssl.keyStore", "/usr/lib/jvm/java-1.8.0-openjdk-amd64/jre/lib/security/cacerts");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
        System.setProperty("javax.net.ssl.trustStore", "/usr/lib/jvm/java-1.8.0-openjdk-amd64/jre/lib/security/cacerts");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");*/
        //RespuestaSolicitud respuesta = validarComprobante(null);
        //System.out.println(respuesta.getEstado());
        
        
        File file = new File("/home/henry/Downloads/FirmaSRI/fileSign.xml");
			byte[] bytes = loadFile(file);
			System.out.println("validarComprobante.result="
					+ validarComprobante(bytes).getEstado());

	}
	 private static byte[] loadFile(File file) throws IOException {
			InputStream is = new FileInputStream(file);

			long length = file.length();
			if (length > Integer.MAX_VALUE) {
				// File is too large
			}
			byte[] bytes = new byte[(int) length];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "
						+ file.getName());
			}

			is.close();
			return bytes;
		}  
}
