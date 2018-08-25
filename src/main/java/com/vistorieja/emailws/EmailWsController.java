package com.vistorieja.emailws;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
        String  corpoMsg = "<html><style>.email {width:100%;padding:4 4 4 4;font-family:arial;font-size:12px;text-align:justify;}</style><body>"
                + "<span class='email'>"
                + "<br/>Bem-vindo, "+ usuario.getEmail()+" !</br>" +
                "Sua senha é:" + newPassword +
                "<br/><br/>"
                + "<b>Atenciosamente,</b><br/>"
                + "<b>Formul&aacute;rio de Contato - VistorieJï¿½</b><br/>"
                + "<b>E-mail:&nbsp;</b> contato@vistorieja.com <br/>"
                + "http://www.vistorieja.com.br<br/>"
                + "</span></body></html>";
        email.setText(corpoMsg);
    }
    private void montarEmailSignup(String email, SimpleMailMessage message) {

        message.setTo(email);
        message.setFrom("contato@vistorieja.com");
        message.setSubject("[VistorieJá] - Bem vindo!");
        String  corpoMsg = "<html><style>.email {width:100%;padding:4 4 4 4;font-family:arial;font-size:12px;text-align:justify;}</style><body>"
                + "<span class='email'>"
                + "<br/>Bem-vindo, ao VistorieJá!</br>" +
                "<br/><br/>"
                + "<b>Atenciosamente,</b><br/>"
                + "<b>Formul&aacute;rio de Contato - VistorieJï¿½</b><br/>"
                + "<b>E-mail:&nbsp;</b> contato@vistorieja.com <br/>"
                + "http://www.vistorieja.com.br<br/>"
                + "</span></body></html>";
        message.setText(corpoMsg);
    }

    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(path = "/send-email-signup/{email}", method = RequestMethod.GET)
    public HttpStatus sendMailSignup(@PathVariable("email") String email) {
        SimpleMailMessage message = new SimpleMailMessage();

        try {
            montarEmailSignup(email,message);
            mailSender.send(message);
            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.NOT_FOUND;
        }
    }


}

