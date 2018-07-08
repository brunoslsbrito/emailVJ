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
            UsuarioDto usuario = recuperarUsuario(email);
            montarEmail(usuario,message);
            mailSender.send(message);
            return HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.NOT_FOUND;
        }
    }

    public UsuarioDto recuperarUsuario(String email) {
        UsuarioDto usuario = null;
        try {
            Gson gson = new Gson();
            URL url = new URL("http://www.vistorieja.com/rest/usuario/find/" + email);
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

    private void montarEmail(UsuarioDto usuario, SimpleMailMessage email) {

        email.setTo(usuario.getEmail());
        email.setFrom("contato@vistorieja.com");
        email.setSubject("[VistorieJá] - Recuperação de Senha");
        email.setText(usuario.getPassword());
        email.setTo(usuario.getEmail());
        String  corpoMsg = "<html><style>.email {width:100%;padding:4 4 4 4;font-family:arial;font-size:12px;text-align:justify;}</style><body>"
                + "<span class='email'>"
                + "<br/>Bem-vindo, "+ usuario.getEmail()+" !</br>" +
                "Sua senha é:" + usuario.getPassword()+
                "<br/><br/>"
                + "<b>Atenciosamente,</b><br/>"
                + "<b>Formul&aacute;rio de Contato - VistorieJï¿½</b><br/>"
                + "<b>E-mail:&nbsp;</b> contato@vistorieja.com <br/>"
                + "http://www.vistorieja.com.br<br/>"
                + "</span></body></html>";
        email.setText(corpoMsg);
    }

}

