# SMTP 2 SMS Twillio

A small App I wrote a few years ago, while learned Java Basics towords my BSC in CS.</br>
The App gives a solution to allow the usage of 2FA Token through Twilio with a system</br>
that uses SMTP 2 SMS (I don't know if by now twilio nativly supports it, back then they didn't).</br>
The App listens on port 25 (Default), can be changed through configuration file,</br>
Analyze the SMTP message, extract it from the message, log it into MYSQL DB and sends it to the user using the Twilio SDK.</br>
