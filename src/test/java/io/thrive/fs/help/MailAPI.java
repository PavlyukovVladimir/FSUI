package io.thrive.fs.help;

import javax.mail.*;
import javax.mail.search.AddressTerm;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Properties;

public class MailAPI {
//    mail.transport.protocol = smtps
//    mail.smtp.host = smtp.yandex.ru
//    mail.smtp.port = 465
//    mail.smtp.user = artem.boiar
//    mail.smtp.ssl.enable = true
//    mail.smtp.auth = true
//    mail.debug = true
    private Properties mailProperties;
    private String user;
    private String password;
    private String host;

    private Store store;

    public MailAPI() throws MessagingException, IOException {
        this.user = "vladimir.pavlyukov@thrive.io";
        this.password = "dcmylouazhaxkmut";
        this.host = "imap.yandex.ru";

        // Свойства установки
        this.mailProperties  = new Properties();
        this.mailProperties.put("mail.store.protocol", "imaps");  //SSL

        this.store = Session.getInstance(this.mailProperties).getStore();
        this.store.connect(this.host, this.user, this.password);

        Folder inbox = getOpenedFolder("INBOX", Folder.READ_ONLY);

        System.out.println("message " + inbox.getMessageCount());

        Message message = inbox.getMessage(inbox.getMessageCount());
        System.out.println(message.getContent());
        Address[] addresses = message.getFrom();
        Address address = addresses[0];

        String[] array = Arrays.stream(message.getFrom()).map(Address::toString).toArray(String[]::new);
        System.out.println(Arrays.toString(array));
        System.out.println(message.getContentType());
        array = Arrays.stream(message.getAllRecipients()).map(Address::toString).toArray(String[]::new);
        System.out.println(Arrays.toString(array));
        System.out.println(message.getReceivedDate());
        System.out.println(message.getSubject());
        System.out.println(message.getDescription());
        System.out.println(message.getDisposition());

    }

    public Folder getOpenedFolder(String folderName, int mode) throws MessagingException {
        if(folderName == null || folderName.equals("")){
            folderName = "INBOX";
        }
        if(mode != Folder.READ_ONLY && mode != Folder.READ_WRITE){
            mode = Folder.READ_ONLY;
        }
        Folder folder = this.store.getFolder(folderName);
        folder.open(mode);
        return folder;
    }
}
