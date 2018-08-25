package com.vistorieja.emailws;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

@RestController
public class EmailWsController {
    private static int HTTP_COD_SUCESSO = 200;
    @Autowired
    private MailSender mailSender;

    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(path = "/email-send/{email}", method = RequestMethod.GET)
    public HttpStatus sendMail(@PathVariable("email") String email) {
        SimpleMailMessage message = new SimpleMailMessage();


        try {
            String newPassword = PasswordGenerator.getRandomPassword(8);
            UsuarioDto usuario = recuperarUsuario(email,newPassword);
            montarEmail(usuario,message,newPassword);
            mailSender.send(message);
            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.NOT_FOUND;
        }
    }

    public UsuarioDto recuperarUsuario(String email, String newPassword) {
        UsuarioDto usuario = null;
        try {
            Gson gson = new Gson();
            URL url = new URL("http://www.vistorieja.com/rest/usuario/recovery/" + email + "/" + newPassword);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            if (con.getResponseCode() != HTTP_COD_SUCESSO) {
                throw new RuntimeException("HTTP error code : " + con.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));
            usuario  = gson.fromJson(br, UsuarioDto.class);
            con.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    private void montarEmail(UsuarioDto usuario, SimpleMailMessage email, String newPassword) {

        email.setTo(usuario.getEmail());
        email.setFrom("contato@vistorieja.com");
        email.setSubject("[VistorieJá] - Recuperação de Senha");
        email.setText(usuario.getPassword());
        email.setTo(usuario.getEmail());
        String corpoMsg = "<html><style>.email {width:100%;padding:4 4 4 4;font-family:arial;font-size:12px;text-align:justify;}</style><body>"
                + "<span class='email'>"
                + "<br/><br/><br/>"
                + "<b>Atenciosamente,</b><br/>"
                + "<b>Formul&aacute;rio de Contato - VistorieJï¿½</b><br/>"
                + "<b>E-mail:&nbsp; contato@vistorieja.com.b</b><br/>"
                + "http://www.vistorieja.com.br<br/>"
                + "</span></body></html>";
        email.setText(corpoMsg);
    }

    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(path = "/send-email-signup/{email}", method = RequestMethod.GET)
    public HttpStatus sendMailSignup(@PathVariable("email") String email) {
        SimpleMailMessage message = new SimpleMailMessage();

        try {
            final JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setUsername(email);
            sender.setPassword("1*Contato");
            sender.setHost("mail55.redehost.com.br");
            sender.setPort(587);
            sender.setUsername("contato@vistorieja.com");
            Properties prop = new Properties();
            prop.put("mail.smtp.auth",true);
            prop.put("mail.smtp.timeout","25000");

            sender.setJavaMailProperties(prop);


            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            String htmlMsg = "<html><style>.email {width:100%;padding:4 4 4 4;font-family:arial;font-size:12px;text-align:justify;}</style><body>"
                    + "<span class='email'>"
                    + "<br/><br/><br/>"
                    + "<b>Atenciosamente,</b><br/>"
                    + "<b>Formul&aacute;rio de Contato - VistorieJï¿½</b><br/>"
                    + "<b>E-mail:&nbsp;</b><br/>"
                    + "http://www.vistorieja.com.br<br/>"
                    + "</span></body></html>";
            mimeMessage.setContent(htmlMsg, "text/html");
            helper.setTo(email);
            helper.setSubject("VistorieJá - Bem vindo!");
            helper.setFrom("contato@vistorieja.com");
            sender.send(mimeMessage);

//            montarEmailSignup(email,message);
//
//
//            mailSender.send(message);
            return HttpStatus.OK;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return HttpStatus.NOT_FOUND;
        }
    }


}

