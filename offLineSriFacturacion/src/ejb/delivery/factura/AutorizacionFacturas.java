package ejb.delivery.factura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import ec.gob.sri.comprobantes.ws.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesOffline;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesOfflineService;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;

public class AutorizacionFacturas {

	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement st = null;
		StringBuilder mensajeAutorizacion = new StringBuilder();
		StringBuilder infoAdicionalAutorizacion = new StringBuilder();
		String url = "jdbc:postgresql://localhost:5432/delivery";

		Properties props = new Properties();
		props.setProperty("user", "postgres");
		props.setProperty("password", "postgres");

		String claveAccesoComprobante = args[0];
		Long idordenactual = Long.parseLong(args[1]);

		AutorizacionComprobantesOfflineService service = new AutorizacionComprobantesOfflineService();
		AutorizacionComprobantesOffline port = service
				.getAutorizacionComprobantesOfflinePort();

		RespuestaComprobante respuesta = port
				.autorizacionComprobante(claveAccesoComprobante);

		if (respuesta.getAutorizaciones().getAutorizacion().size() > 0) {
			for (Autorizacion listaComp : respuesta.getAutorizaciones()
					.getAutorizacion()) {

				for (ec.gob.sri.comprobantes.ws.aut.Mensaje listaMsg : listaComp
						.getMensajes().getMensaje()) {
					mensajeAutorizacion.append(listaMsg.getMensaje());
					infoAdicionalAutorizacion.append(listaMsg
							.getInformacionAdicional());
				}

			}
		}
		try {
			conn = DriverManager.getConnection(url, props);
			st = conn
					.prepareStatement("UPDATE factura set estado_autorizacion=?, mensaje_autorizacion =? ,informacionadicional_autorizacion=? where idordenactual = ?");
			st.setString(
					1,
					StringUtils.abbreviate(respuesta.getAutorizaciones()
							.getAutorizacion().get(0).getEstado(), 255));
			st.setString(2,
					StringUtils.abbreviate(mensajeAutorizacion.toString(), 255));
			st.setString(3, StringUtils.abbreviate(
					infoAdicionalAutorizacion.toString(), 255));
			st.setLong(4, idordenactual);

			st.executeUpdate();
			st.close();

		} catch (SQLException e) {
			try {
				e.printStackTrace();
				st.close();
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		} finally {
			try {
				st.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}
}
