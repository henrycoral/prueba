package ejb.delivery.factura;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import ec.gob.sri.comprobantes.ws.Comprobante;
import ec.gob.sri.comprobantes.ws.Mensaje;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOffline;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOfflineService;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;

public class RecepcionFacturas {

	public static void main(String[] args) {
		Connection conn = null;
		Statement statement = null;
		PreparedStatement st = null;
		ResultSet resultSet = null;
		StringBuilder mensajeRecepcion = new StringBuilder();
		StringBuilder infoAdicionalRecepcion = new StringBuilder();
		String url = "jdbc:postgresql://localhost:5432/delivery";
		Properties props = new Properties();
		props.setProperty("user", "postgres");
		props.setProperty("password", "postgres");
		// props.setProperty("ssl","true");

		Long idordenactual = Long.parseLong(args[0]);
		Long idcliente = Long.parseLong(args[1]);
		Long idproveedor = Long.parseLong(args[2]);
		String pathFactura = args[3];

		String nameFileOut = "" + idordenactual.intValue()
				+ idcliente.intValue() + idproveedor.intValue() + "SIGN.xml";
		RecepcionComprobantesOfflineService service = new RecepcionComprobantesOfflineService();
		RecepcionComprobantesOffline port = service
				.getRecepcionComprobantesOfflinePort();

		File file = new File(pathFactura + nameFileOut);
		byte[] bytes = null;
		try {
			bytes = loadFile(file);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		RespuestaSolicitud respuestaSri = port.validarComprobante(bytes);

		if (respuestaSri.getComprobantes().getComprobante().size() > 0) {
			for (Comprobante listaComp : respuestaSri.getComprobantes()
					.getComprobante()) {

				for (Mensaje listaMsg : listaComp.getMensajes().getMensaje()) {
					mensajeRecepcion.append(listaMsg.getMensaje());
					infoAdicionalRecepcion.append(listaMsg
							.getInformacionAdicional());
				}

			}
		}

		try {
			conn = DriverManager.getConnection(url, props);
			statement = conn.createStatement();
			resultSet = statement
					.executeQuery("SELECT * FROM factura where idordenactual ="
							+ idordenactual.intValue());

			if (resultSet.next()) {
				st = conn
						.prepareStatement("UPDATE factura set estado_recepcion=?, mensaje_recepcion =? ,informacionadicional_recepcion=? where idordenactual = ?");
				st.setString(1,
						StringUtils.abbreviate(respuestaSri.getEstado(), 255));
				st.setString(2, StringUtils.abbreviate(
						mensajeRecepcion.toString(), 255));
				st.setString(
						3,
						StringUtils.abbreviate(
								infoAdicionalRecepcion.toString(), 255));
				st.setLong(4, idordenactual);

				st.executeUpdate();
				st.close();

			} else {
				st = conn
						.prepareStatement("INSERT INTO factura (estado_recepcion,mensaje_recepcion,informacionadicional_recepcion,idordenactual) VALUES (?, ?, ?,?)");
				st.setString(1,
						StringUtils.abbreviate(respuestaSri.getEstado(), 255));
				st.setString(2, StringUtils.abbreviate(
						mensajeRecepcion.toString(), 255));
				st.setString(
						3,
						StringUtils.abbreviate(
								infoAdicionalRecepcion.toString(), 255));
				st.setLong(4, idordenactual);
				st.executeUpdate();
				st.close();
			}

		} catch (SQLException e) {
			try {
				e.printStackTrace();
				statement.close();
				st.close();
				resultSet.close();
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		} finally {
			try {
				statement.close();
				st.close();
				resultSet.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	public static byte[] loadFile(File file) throws IOException {
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
