package br.com.apssystem.algafood.infrastructure.email;

import br.com.apssystem.algafood.core.email.EmailProperties;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


public class SmtpEnvioEmailService implements EnvioEmailService {

    @Autowired
    JavaMailSender mailSender;
    @Autowired
    EmailProperties emailProperties;
    @Autowired
    Configuration freemarkerConfig;

    @Override
    public void enviar(Mensagem mensagem) {
        try {
            MimeMessage mimeMessage = criarMimeMessage(mensagem);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new EmailException("Não foi possível enviar e-mail", e);
        }
    }

    protected MimeMessage criarMimeMessage(Mensagem mensagem) throws MessagingException {
        String corpo = processarTemplate(mensagem);
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setFrom(emailProperties.getRemetente());

        helper.setTo(emailProperties.getDestinatario());
        if(!emailProperties.getDestinatario().isEmpty()) {
            helper.setTo(mensagem.getDestinatarios().toArray(new String[0]));
        }

        helper.setSubject(mensagem.getAssunto());
        helper.setText(corpo, true);
        return mimeMessage;
    }

    protected String processarTemplate(Mensagem mensagem) {
        try {
            Template template = freemarkerConfig.getTemplate(mensagem.getCorpo());
            return FreeMarkerTemplateUtils.processTemplateIntoString(
                    template, mensagem.getAtributos());
        } catch (Exception e) {
            throw new EmailException("Não foi possível montar o template do e-mail", e);
        }
    }
}
