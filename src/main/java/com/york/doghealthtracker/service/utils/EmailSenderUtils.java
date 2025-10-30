package com.york.doghealthtracker.service.utils;

public class EmailSenderUtils {
    public final static String EMAIL_MESSAGE = """
            <html>
              <head>
                <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;600&display=swap" rel="stylesheet">
              </head>
              <body style="margin: 0; padding: 0; background-color: #fafafa; font-family: 'Outfit', Arial, sans-serif;">
                <div style="max-width: 600px; margin: 40px auto; background: #ffffff; border-radius: 16px;
                            box-shadow: 0 2px 6px rgba(0,0,0,0.08); padding: 40px; text-align: center;">
                  <h2 style="color: #000000; font-weight: 600; margin-bottom: 20px;">PawWell</h2>
                  <p style="color: #000000; font-size: 16px; margin-bottom: 16px;">Hello,</p>
                  <p style="color: #222222; font-size: 15px; margin-bottom: 32px;">
                    We received a request to reset your password.<br>
                    Click the button below to set a new password.
                  </p>

                  <a href="%s"
                     style="background-color: #000000; color: white; padding: 14px 28px; text-decoration: none;
                            border-radius: 8px; font-weight: 600; font-size: 16px; display: inline-block;">
                     Reset Password
                  </a>

                  <p style="color: #444444; font-size: 14px; margin-top: 32px;">
                    If you did not request a password reset, you can safely ignore this email.
                  </p>

                  <div style="margin-top: 28px; font-size: 13px; color: #555555;">
                    <p>Or copy and paste this link in your browser:</p>
                    <a href="%s" style="color: #000000; word-break: break-all;">%s</a>
                  </div>
                </div>
              </body>
            </html>
                                    """;
    public static final String RESET_LINK_URL = "http://localhost:5173/reset-password?token=";
    public static final String PASSWORD_RESET_EMAIL_SUBJECT = "Password Reset Request";
    public static final int PASSWORD_RESET_EXPIRY_SECONDS = 900;
}
