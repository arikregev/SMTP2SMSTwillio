# SMTP 2 SMS Twillio

#### A small App I wrote a few years ago, while learned Java Basics towords my BSC in CS.
#### The App gives a solution that allows the usage of 2FA Token through Twilio with a system
#### that uses SMTP 2 SMS (I don't know if by now twilio nativly supports it, back then they didn't).
#### The App listens on port 25 (Default, can be changed through configuration file),
#### Analyzes the SMTP message, extract it from the message, logs it into MYSQL DB and sends it to the user using the Twilio SDK.
----
Configuration File Parameters:

- SmsSenderName=<SenderName>
- Path2Logs=C:\Logs\
- Port=25
- StopServerPort=8000
- Linux=false #true or false for windows
- ACCOUNT_SID=<SID>
- AUTH_TOKEN=<TOKEN>
