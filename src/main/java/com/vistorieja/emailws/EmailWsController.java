package com.vistorieja.emailws;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
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
    @RequestMapping(path = "/email-send/{email}/{msg}", method = RequestMethod.GET)
    public String sendMail(@PathVariable("email") String email, @PathVariable("msg") String msg) {
        SimpleMailMessage message = new SimpleMailMessage();


        try {
            UsuarioDto usuario = recuperarUsuario(email);

            message.setText(msg);
            message.setTo(usuario.getEmail());
            message.setFrom("contato@vistorieja.com");
            message.setSubject("[VistorieJá] - Recuperação de Senha");
            message.setText(usuario.getPassword());
            mailSender.send(message);
            return "Email enviado com sucesso!";
        } catch (Exception e) {
            return "Erro ao enviar email.";
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
}
