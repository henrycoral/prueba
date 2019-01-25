package autorizacion.comprobantes;

public class TestAutorizacion {

	public static void main(String[] args) {
		   AutorizacionComprobantesOfflineService service = new AutorizacionComprobantesOfflineService();
	          AutorizacionComprobantesOffline port = service.getAutorizacionComprobantesOfflinePort();
	          RespuestaComprobante respuesta =port.autorizacionComprobante("0212201801179214673900110020010000000011234567813"); 
	         System.out.println("respuesta=>"+respuesta.getAutorizaciones().getAutorizacion());

	}

}
