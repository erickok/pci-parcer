# pci-parcer
A new publication mailer with biorXiv support

See [the GitHub release](https://github.com/erickok/pci-parcer/releases) to download the application. In the .zip file you will find a pci-parser-1.X.jar, which is the actual program. It can be run like any other Java program, for example: 

```
cd /path/wher/you/unzipped
java -jar pci-parser-1.2.jar
```

The program will read a settings file called `pci-parser-settings.json` that has to be located in a directory named `storage`. As you unzip the downloaded file you fill find it is already there, so just fill in the details.
- `"biorxivTopic"` is the subject code on biorXiv to scan; see https://www.biorxiv.org/alertsrss and check the feeds list. For example, you use `evolutionary_biology"` or `"genomics"` or `"zoology"`
- `"emailFromAddress"` is what will appear to be the sender address of the emails
- `"emailFromName"` is what will appear as sender name
- `"emailTo"` is a list of email addresses to send the mails to
- `"emailHostName"` is the SMTP server hostname to use as mailer, for example `"smtp.gmail.com"`
- `"emailHostUser"` is the SMTP server username, which is typically your full email adress
- `"emailHostPass"` is your SMTP server user password

(note that the program assumes secure TLS conenctions only, and default ports)

When running the program, it will use the public RSS feed of your biorXiv subject to see if there are any new publications it didn't know about. After the program has run it will write in the `storage` directory a file called `pci-parser-last.json` with the amount of new pubications it found (for which it should have send emails) and the list of publications it encountered (as not to send mails for those again). 

It will also write to `pci-parser-history.json` an entry for every publication it found and send email for. (A technical side-node: that file is not actually a valid JSON: you have to add a `[` character at the start, and a `]` at the end if you want that. This is merely for internal efficiency.)

What you typically want to do is run the program every couple of hours. (At least as often not to miss any publications in the RSS feed, which outputs only the latest 30.) Personally, I do this via a cron job that runs every 3 hours.