package correo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public class SendMailWithVelocityTemplate {

	public static void main(String[] args) {
		String to = "henrycoral@gmail.com";
		String from = "kripalkashyap@gmail.com";
		final Properties prop = System.getProperties();
		try {
			prop.load(new FileInputStream(
					new File(
							"/home/henry/Documents/workspace/EmailVelocity/src/correo/mail-settings.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Session session = Session.getDefaultInstance(prop, new Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
						prop.getProperty("mail.user"), prop
								.getProperty("mail.passwd"));
			}
		});

		try {
			// Initialize velocity
			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "class,file");
		    ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
		    ve.setProperty("runtime.log.logsystem.log4j.logger", "VELLOGGER");
		    ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		    ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
			ve.init();

			/* next, get the Template */
			Template t = ve.getTemplate("src/correo/mail-body-template.vm");
			/* create a context and add data */
			VelocityContext context = new VelocityContext();
			context.put("fecha", "fecha");
			context.put("nombre", "nombre");
			context.put("nombreEmpresa", "Los Pinchos de Langostino");
			context.put("paginaWeb", "www.lospinchosdelangostino.com");
			context.put("listaFactura", "listaFactura");
			context.put("direccionWeb", "http://www.lospinchosdelangostino.com/assets/img/logo.png");
			context.put("colorEmail", "#FFBA1A");
			//http://www.alitasdelcadillac.com/wp-content/uploads/2016/09/Logo-alitas_Cadillac.png
			context.put("tituloUno", "Nuestros Locales");
			context.put("detalleUno", "Local 1: Rio Coca e5 49 e Isla San Cristobal.");
			context.put("detalleDos", "Local 2: Av. Republica y La Pradera ( Food Garden La Pradera)");
			context.put("detalleTres", "Local 3: Av. America y Abelardo Moncayo (Esquina)");
			context.put("detalleCuatro", "Telefonos: 098 453 4646 02-2274844");
			
			
			
			
			
			
			/* now render the template into a StringWriter */
			StringWriter out = new StringWriter();
			t.merge(context, out);
			
			// Creating default MIME message object
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
			message.setSubject("Detalle de Compra");
			message.setContent(out.toString(), "text/html; charset=utf-8");

			Transport.send(message);
			System.out.println("Mail sent=>"+SendMailWithVelocityTemplate.class.getProtectionDomain().getCodeSource().getLocation().getPath());

			ClassLoader loader = SendMailWithVelocityTemplate.class.getClassLoader();
	        System.out.println(loader.getResource("correo/SendMailWithVelocityTemplate.class"));
			
	        
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void main2(String[] args) {
		String to = "henrycoral@gmail.com";
		String from = "kripalkashyap@gmail.com";
		final Properties prop = System.getProperties();
		try {
			prop.load(new FileInputStream(
					new File(
							"/home/henry/Documents/workspace/EmailVelocity/src/correo/mail-settings.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Session session = Session.getDefaultInstance(prop, new Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
						prop.getProperty("mail.user"), prop
								.getProperty("mail.passwd"));
			}
		});

		try {
			// Creating default MIME message object
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
			message.setSubject("Test mail through simple java API");

			BodyPart body = new MimeBodyPart();

			// velocity stuff.

			// Initialize velocity
			VelocityEngine ve = new VelocityEngine();
			ve.init();

			/* next, get the Template */
			Template t = ve.getTemplate("src/correo/mail-body-template.vm");
			/* create a context and add data */
			VelocityContext context = new VelocityContext();
			context.put("fName", "Kunal");
			context.put("lName", "kumar");
			context.put("proprietor", "Owner");
			/* now render the template into a StringWriter */
			StringWriter out = new StringWriter();
			t.merge(context, out);

			// velocity stuff ends.

			body.setContent(out.toString(), "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(body);

			body = new MimeBodyPart();

			String filename = "mail-attachement-template.txt";
			DataSource source = new FileDataSource(filename);
			body.setDataHandler(new DataHandler(source));
			body.setFileName("attachement.html");
			body.setDescription("Description.html");
			body.setText("text");

			multipart.addBodyPart(body);

			message.setContent(multipart, "multipart/mixed");

			// Send mail
			Transport.send(message);

			System.out.println("Mail sent");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
