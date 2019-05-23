package com.product.com.product.util.mail;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MailUtil {
    private static String user = "xx@qq.com";
    private static String pwd = "xxxxxxx";

    public static void main(String[] args) {
        String from = "xx@qq.com";
        String[] tos = new String[]{"xx@gmail.com"};
        String[] ccs = new String[]{"xxx@qq.com"};
        String[] bccs = null;
        String subject = "test";
        String body = "正文测试， <img src='cid:1.jpg'/>\n 第二张图片<img src='cid:1.jpg'/>";
        String type = "text/html;charset=UTF-8";
        String[] attachs = new String[]{"/Users/wuzhengya/Desktop/TM_RDHD_Project.sql", "/Users/wuzhengya/Desktop/1.jpg"};

        Map<String, String> pics = new HashMap<>();
        pics.put("1.jpg", "/Users/wuzhengya/Desktop/1.jpg");


        try {
            MimeMessage message = message(session(true), subject, from, tos, ccs, bccs);
            message.setContent(mixMessage(body, type, pics, attachs));
            message.saveChanges();

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 注意getDefaultInstance 和 getInstance的区别
     * getDefaultInstance 作为默认实例，每次获取都会做校验
     * getInstance 创建一个新的Session实例
     *
     * @param auth true or false
     * @return Session
     */
    private static Session session(boolean auth) {
        if (auth)
            return Session.getDefaultInstance(PropertiesInstance.properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pwd);
                }
            });
        else
            return Session.getDefaultInstance(PropertiesInstance.properties);
    }


    public static MimeMessage message(Session session, String subject, String from, String to) throws MessagingException {
        return message(session, subject, from, to, null, null);
    }

    public static MimeMessage message(Session session, String subject, String from, String to, String cc) throws MessagingException {
        return message(session, subject, from, to, cc, null);
    }

    /**
     * @param session session
     * @param subject 主题
     * @param from    发件人
     * @param to      收件人
     * @param cc      抄送
     * @param bcc     密送
     * @return 返回创建的邮件对象
     */
    public static MimeMessage message(Session session, String subject, String from, String to, String cc, String bcc) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));

        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        if (null != cc)
            message.setRecipient(Message.RecipientType.CC, new InternetAddress(cc));
        if (null != bcc)
            message.setRecipient(Message.RecipientType.BCC, new InternetAddress(bcc));

        message.setSubject(subject);
        return message;
    }


    public static MimeMessage message(Session session, String subject, String from, String[] tos) throws MessagingException {
        return message(session, subject, from, tos, null, null);
    }

    /**
     * @param session session
     * @param from    发件人
     * @param tos     收件人（数组）
     * @param cc      抄送（数组）
     * @param bcc     密送（数组）
     * @param subject 邮件标题
     * @return 返回创建的邮件对象
     */
    public static MimeMessage message(Session session, String subject, String from, String[] tos,
                                      String[] cc, String[] bcc) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));

        message.setRecipients(Message.RecipientType.TO, getAddress(tos));
        if (cc != null)
            message.setRecipients(Message.RecipientType.CC, getAddress(cc));
        if (bcc != null)
            message.setRecipients(Message.RecipientType.BCC, getAddress(bcc));

        message.setSubject(subject);
        return message;
    }

    /**
     * 文本
     *
     * @param content 文本内容
     * @param type    文本类型
     * @return
     * @throws MessagingException
     */
    public static MimeBodyPart content(String content, String type) throws MessagingException {
        MimeBodyPart part = new MimeBodyPart();
        part.setContent(content, type);
        return part;
    }


    /**
     * 图片或附件
     *
     * @param path 文件路径
     * @return part
     * @throws MessagingException exception
     */
    public static MimeBodyPart file(String path, String contentID) throws MessagingException {
        MimeBodyPart part = new MimeBodyPart();
        DataHandler handler = new DataHandler(new FileDataSource(path));
        part.setDataHandler(handler);
        part.setFileName(handler.getName());
        if (null != contentID && !"".equals(contentID))
            part.setContentID(contentID);
        return part;
    }

    private static Address[] getAddress(String[] to) throws AddressException {
        Address[] addressescc = null;
        if (to != null && to.length > 0) {
            addressescc = new InternetAddress[to.length];
            for (int i = 0; i < to.length; i++) {
                addressescc[i] = new InternetAddress(to[i]);
            }
        }
        return addressescc;
    }

    /**
     * 纯文本
     *
     * @param body 正文
     * @param type 类型
     * @return
     * @throws MessagingException
     */
    public static MimeMultipart simpleMessage(String body, String type) throws MessagingException {
        return mixMessage(body, type, null, null);
    }

    /**
     * 文本和图片
     *
     * @param body 正文
     * @param type 类型
     * @param pics 图片
     * @return
     * @throws MessagingException
     */
    public static MimeMultipart cpMessage(String body, String type, Map<String, String> pics) throws MessagingException {
        return mixMessage(body, type, pics, null);
    }

    /**
     * 纯附件
     *
     * @param attachs 附件
     * @return
     * @throws MessagingException
     */
    public static MimeMultipart attachMessage(String[] attachs) throws MessagingException {
        return mixMessage(null, null, null, attachs);
    }

    /**
     * 文本和附件
     *
     * @param body    文本
     * @param type    类型
     * @param attachs 附件
     * @return
     * @throws MessagingException
     */
    public static MimeMultipart caMessage(String body, String type, String[] attachs) throws MessagingException {
        return mixMessage(body, type, null, attachs);
    }

    /**
     * 文本、图片、附件
     *
     * @param body    文本正文
     * @param type    文本类型
     * @param pics    图片集合
     * @param attachs 附件集合
     * @return
     * @throws MessagingException
     */
    public static MimeMultipart mixMessage(String body, String type, Map<String, String> pics, String[] attachs) throws MessagingException {
        MimeMultipart cp = null;
        MimeMultipart all = new MimeMultipart();

        //文本
        MimeBodyPart bodyPart = null;
        if (null != body && !"".equals(body)) {
            cp = new MimeMultipart();
            bodyPart = content(body, type);
            cp.addBodyPart(bodyPart);
        }


        //图片
        if (null != pics && pics.size() > 0) {
            if (null == cp)
                cp = new MimeMultipart();
            else
                cp.setSubType("related");
            for (String key : pics.keySet()) {
                cp.addBodyPart(file(pics.get(key), key));
            }
        }

        MimeBodyPart content = null;
        if (null != cp) {
            content = new MimeBodyPart();
            content.setContent(cp);
            all.addBodyPart(content);
        }


        //附件
        if (null != attachs && attachs.length > 0) {
            if (null != content)
                all.setSubType("mixed");
            for (String attach : attachs) {
                all.addBodyPart(file(attach, null));
            }
        }

        return all;
    }

    /**
     * 不设置mail.transport.protocol,则默认是smtp协议，如果使用pop3，imap等则改为
     * properties.setProperty("mail.pop3.host","...."); //在定义host等，添加上pop3等
     */
    private static class PropertiesInstance {
        private static Properties properties = new Properties();

        static {
            properties.setProperty("mail.transport.protocol", "smtp"); //邮件协议，默认smtp
            properties.setProperty("mail.debug", "true"); // 是否开启debug模式，默认false
            properties.setProperty("mail.from", user); //默认邮件发送地址
            properties.setProperty("mail.host", "smtp.qq.com"); //服务器地址
            properties.setProperty("mail.port", "25"); //服务器端口号，默认25
            properties.setProperty("mail.smtp.auth", "true"); //是否需要认证，默认false
            properties.setProperty("mail.timeout", "10000");//i/o连接超时时间，单位毫秒，默认永不超时

        }
    }
}
