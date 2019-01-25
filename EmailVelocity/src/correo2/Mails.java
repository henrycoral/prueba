package correo2;

import java.io.StringWriter;

import org.apache.commons.mail.SimpleEmail;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import play.libs.*;
import play.libs.mailer.MailerPlugin;
import play.mvc.*;

public class Mails extends MailerPlugin {
	public static void message(String target, String sender, String subject,
			String targetname) {
		try {
			VelocityEngine ve = new VelocityEngine();
			ve.init();

			VelocityContext context = new VelocityContext();
			context.put("senderName", targetname);

			Template t = ve.getTemplate("app/views/Mails/email_template.vm");

			StringWriter writer = new StringWriter();
			t.merge(context, writer);

			SimpleEmail email = new SimpleEmail();
			email.setFrom(sender);
			email.addTo(target);
			email.setSubject(subject);
			email.setMsg(writer.toString());
			//Mail.send(email);

		} catch (Exception e) {
			System.out.println("Error " + e.getMessage());
		}
	}
}
