# recivaportal
## Introduction
Attempt at a fake reciva portal to resurrect my perfectly functioning Roberts Radio WM201 which has been bricked by the closure of the reciva.com portal.

The radio is used more or less exclusively as an alarm clock to wake me up with BBC Radio 4. Since the reciva shutdown this no
longer works even though the preset still works fine when started manually. To get to the preset now requires alot of clicking
to remove error messages, presumably these error messages also cause the alarm function to believe the radio is not connected to the internet and to use the 'beeps' as the alarm. The alarm clock prompt to 'start the radio' does actually start playing the configured stream.

The aim of this project is to get the alarm to play the selected preset/stream even though the reciva servers no longer exist.

I was hoping that the greedy barstewards responsible for switching off the reciva servers would have at least made the reciva server code open source so owners of radios which rely on the reciva server could either set up a local server or maybe fund a public one in order to keep their very expensive and still functioning hardware alive. If the barstewards thought that pulling the plug on the server would boost their internet radio sales then I hope the world proves them wrong - who in their right mind would waste another huge chunk of cash on something that can be rendered useless at the flick of a switch... hmm, actually that sounds sort of like an iPhone user...

## The story so far

30-Oct-2021 Still trying ot get past 'OPSEL:Unable to decrypt session key'. Thought I was making some progress but once again I've hit a brick wall...  

A couple of things have been learnt;  
 - the output from sernum is reproducible for a given input.
 - the same input cannot be used twice in a row. Fortunately input can be repeated after 21 different inputs. I've implemented 21 different challenges
   and captured the sernum output for them and the webserver cycles through them to ensure it doesn't return the same response too soon. This
   might need to be expanded if desynchronization due to the weberver going down more often than the radio becomes an issue.
 - the decryption is working! Confirmed that the first part of the session request data is the 'challenge-response' value from sernum.
 - the encryption is probably working since re-encrypting the data from the radio produces a matching array
 - the checksum calculated for the challenge response data matches the one in the data so the checksum calculation is probably correct.
 - radio still says it can't decrypt the session key...  

Problem is that I don't know what is supposed to be in the session response. I've tried a couple of things previously but since there was a bug with the checksum calculation they need to be tried again. So far I've done;  
 - 16bytes with a nul terminator
 
To try;
 - 16bytes without a nul terminator - no good
 - remove the X-Reciva-Session-Id header from the response - no good
 - a data block as described for file transfer, eg. with a length header - no good
 - encrypt with the challenge response instead of the key - no good
 - some other form of encryption. Maybe it needs 3DES, or even AES. But how to get the keys since 'sread' doesn't appear to provide them, unless
   that is what the 'MAC' is supposed to be. The MAC does happen to be three times the length of the other 'keys'. The 'Encryption on Reciva' post
   claims the encryption of file download check blocks uses 3DES. The python script claims it used AES with the session key. They oth appear to agree
   on the decrypting of the session response needed DES with the challenge response key... so i've run out of ideas again...  b


24-Oct-2021 Seem to be heading in a good direction. The POST request contains a 256 binary data block. This appears to fit with what is described in the 'Encryption on Reciva' aticle. So now I've got to figure out how to decrypt the block, and potentially encrypt something to send back...  

Now I realise why I'm seeing the GET and POST requests! I'd assumed when I set up the Technitium DNS that the initial requests made by the radio were to the reciva.com domain but actually they are to domains like 'portal9.7803986842.com'. So when I switched to the Technitium DNS the initial requests from the radio are going to the original servers and I guess one of them must be still working and issue a redirect to a reciva.com server. I guess is what the radio is expecting this redirect which is why it only does a HEAD request. 
This is confirmed from the radio log. The HEAD request 'http://portal14.7803986842.com/portal/challenge?serial=0000df34&sp=v257-a-865-a-476' receives an HTTP redirect to 'http://p1.ext.h3.west.us.reciva.com/portal-newformat/challenge?serial=0000df34&sp=v257-a-865-a-476'. This means I will need to find a way of doing such a redirect for the '/portal' - this could easily be handled by the Java app if it wasn't for the problem with tomcat forcing the root of the url to be the name of the war file...  
Seem to have handled it with the following httpd.conf entry:  
RedirectMatch "^/portal/(.*)" "http://chris.reciva.com/portal-newformat/$1"  
and an update in the Technitium DNS for '7803986842.com'  

23-Oct-2021 So I'd more or less given up trying to get this to work but today I noticed a slightly different message on
the radio so I though I'd check the logs and maybe see if the RollingFileAppender thing was finally doing what it said on
the can (BTW It appears using 'file' is the cause of the problem, logback attempts to rename the file to the date patterned name and this fails because something has the file open, presumably logback - forking typical). I was surprised to see that
the contant stream of "HEAD /portal" requests had stopped and been replaced by a stream of "GET /portal-newformat" requests.
Inspite of the mapping in the portal war that it should handle "/*" the requests were being given a 404 which explained the
new message on the radio, not that it made it work any better - how the hell could the barstewards that wrote that code not expect that the server would be unavailable, and ensure that the radio continued to function normally until there was actually some need for a connection to the server????  
So the name of the war file now needs to be 'portal-newformat' and we'll see what happens now.  
Not really sure why the requests changed but I guess it must be related to the change of DNS server I made last weekend as this corresponds with the change of request. I wanted a DNS server to serve local addresses to the VPN clients which 'deadwood' was not suited to so I installed a 'Technitium' DNS server instead. Maybe there is difference in the response to the lookup requests from the radio - I'm sure I don' have a clue. But now that the server actually receives GET requests I can experiment with responses and the chances of getting it to work have improved a little.  
Now I see a 'POST' request...  
10-Oct-2021 Just discovered from here 'https://www.bbc.co.uk/blogs/internet/entries/481e7233-0aea-4b15-8ace-878ce549108c' that the streams used by the radio are likely to stop working soon, or be changed to an incompatible format, ie. HTTPS using TLS. If this is the case it would appear to be futile to spend any more time on this project...   
09-Oct-2021 many hours later finally got it to work. I have no idea which of the pieces of magic actually persuaded tomcat to start using the reciva portal REST api again but it is currently working. Unfortunately it is not sharing the REST api with the Jetty version as I copied the class into the tomcat project. The Jetty project appeared to require Java 11 but when I compile the tomcat code using Java 11 the tomcat installation complains about this 52 vs. 55 shirt. Making the project a 1.8 project in Eclipse fixes this problem - no clue why the Eclipse java version should change things when it is supposed to be using the nonesense in the master pom.xml for maven-compiler-plugin where it says release=11 which is supposed to mean it generates the code using Java 11. So I guess the next step is to figure out how to make a version of the shared class which is compile with Java 1.8 for the tomcat version and Java 11 for the jetty version.... Or I can forget about the jetty version for the moment as it did not appear to solve the problem of radio not sending any actual GET requests!  
09-Oct-2021 the tomcat version builds and deploys without error (usually, the maven building keeps throwing random errors which resolve themselves when the exact same command is repeated moments later). Tomcat then reports 404 for all portal URLs which should be handled by the REST class - index.jsp and the sharpfin URLs work fine. I have absolutely no clue why this is. It does not appear to be related to having the REST in a different JAR because I added the class to the Tomcat source and it still behaves the same. Another example of how the modern tools really go out of their way to FORK YOU OVER good and proper. Another wasted morning trying to get something working which was working before and now inexplicably does not. Magic at its wonderous best. Eclipse doesn't help as keeps displaying completely random errors for things that are not there: the latest is 'Element type "dependency" must be followed by either attribute specifications, ">" or "/>".' for the line 68 of the tomcat pom - the forking element is followed by a '>'.
08-Oct-2021 Spent a fascinating few hours trying to get rid of the red flags in Eclipse. This maven shirt is really good
for wasting hours and hours of time and combine it with Eclipse and its days and days of wasted time. From the facted version of something doesn't match with something else (every place a something is specified it matches the other somethings), file can't
be nested even though they are directories and having sub-directories is sort of why they exist. The best thing is how the solution to everyone of these inscrutable messages generates at least two additional messages which take inscrutablitly to an even higher level. I do think I managed to get things set up now so I can build just the jetty version or just the tomcat version. Will probably require a change to the master pom to switch between the two but hey, it's good enough for me.  
[Damn! there is still 'The superclass "jakarta.servlet.http.HttpServlet" was not found on the Java Build Path' for the main project,
which of course makes no sense since it refers to a JSP page in the tomcat project. Think I will have to tell eclipse to ignore this one as I'm unlikely to ever find out why it does it and it doesn't stop it from actually working].  
I do seem to have lost the comments regarding why I needed to make a jetty version, I guess that's one of the side-effects of using
the modern tools - they couldn't give a shirt about whether your time consuming hard work is irretrievably lost every now and then, after all its bound to be better the second/third time around.  
The reason for Jetty was... I noticed in the reciva logs that the HTTP response status from tomcat was appearing as "200 200" whereas the reponse from the upnp servers had "200 OK". The reciva logs also state "OPSEL:Unknown result code: 200" [yippee: I found the comments! They are in the code, so, not the modern tools... this time] and it crossed my mind that maybe reciva is actually looking for the "OK" instead of the numeric code. I figured it must be a trivial change to fix tomcat so it sent what I was supplying in the code as the status response... boy, I was so wrong! Long and short, tomcat developers are having a big laugh at the thought of all the hardware they have helped to render obsolete by blocking anyway to send the status code required by the hardcoded, unmodifiable clients which check the status text instead of the code. The simple test (I thought) to see if sending "OK" would get me a GET request turned out to be something of a nightmare. But anyway, I can now build the recivaportal as a standalone application with an embedded Jetty server (I don't think it can serve the static pages, though). Alas giving the radio the "OK" status text still did not get me a GET request so back to the drawing board I go - complete devoid of any ideas as to what to try next.  
06-Oct-2021 Split into three projects so I could make a Jetty version. No clue if it builds yet.  
02-Oct-2021 Tried response status codes between 1 and 999. Radio still reports unknown status code so I guess there must
be something else going on but haven't got a clue what it could be.  
28-Sep-2021 Messed around with the debug log configuration on the radio. The only clue gleaned is that the radio is possibly looking for a different HTTP return code than the normal 200-OK.  
Alternative codes might be;
- 203 No Content-possibly applicable to the HEAD request
- 401 Unauthorized-indicates user authorization is required. Response must contain header 'WWW-Authenticate: Basic realm="User visible realm"... maybe the value needs to be something else...
- other-will have to randomly try responses since none of the 'official' ones appear to be relevant

26-Sep-2021 Twiddled around with various responses - still don't see a GET request. The one I saw previously must have been 
a fluke, or maybe I did it myself via the browser and forgot. Tried using the radios 'curl' to make the requests and it seems
to work OK. For now I'm out of ideas as to what to do...
Added some of the modified sharpfin and related files to the server. The FTP on the radio does not work well with any of the FTP
clients I've tried (MS FTP, FileZilla, Mobaxterm) - it seems not to support getting a directory list - so the only way to get
files onto the radio is to use something like WGET so need the files on a webserver.
20-Sep-2021 Examined a dump of the /root/ir binary and updated the headers based on some of the strings I saw there. I
finally saw a GET request! I guess the response was not to the radios liking as I saw no other requests. No real idea
what the 'challenge' response should contain, maybe it expects the list of stations immediately. Did notice that turning the
radio on from standby starts to play the last stream - maybe the alarm will work...

18-Sep-2021 A "Deadwood" DNS server is configured to redirect the radios HTTP requests to a local server.
The server runs this webapp and is able to log the radios requests.
Only "HEAD" requests are being received - no GET requests and the alarm still does the 'beeps'

## Getting the project to GitHub

Adding the project to GitHub was the typical nightmare. I've done it before but of course nothing works like it did last time.
Apparently GitHub ate my SSH key. What goes through these peoples minds when they just decide to arbitrarily delete settings which took hours to figure out the first time, and then take hours and hours to figure out why they aren't working and yet more hours to figure out how to get them to work again? I guess I know the answer to this one  - they are just having one great big forking enormous laugh at the thought of all the time wasted due to their ridiculous actions...

I finally got GitHub to work again with the following:
- remember where I put the SSH keys 
- reload the public key into GitHub
- search for SSH in Eclipse-Preferences: Select the General>Network Connections>SSH. Set the SSH directory to point to the
  location of the public/private keys.
- use the SSH URL provided by GitHub>Code button>Clone>SSH to pull into the local project - make sure any "save settings"
  checkboxes are checked! DO NOT change the username to the real username - keep the one provided in the clone url.
- Push to remote - fingers crossed Eclipse will use the SSH private key and GitHub will accept it. There will probably be
  be quite a few popups containing completely incomprehensible stuff - click OK or Next and keep fingers crossed that
  it works and that they wont appear next time. Best to test on a simple readme update before relying on it for real stuff.

26-Sep-2021 After pushing and pulling without problem for a few days now I suddenly get 'rejected: not fast forward'. WTF does this
mean. Why don't they say sensible things when they decide randomly to stop it working? Had to pull from Github some old stuff, which Eclipse will not let me overwrite with the current stuff in its merge window (how is this supposed to work, it never lets me keep the stuff I want!!), and then push again. It seemed to work, luckily it was only the README which was affected. Maybe the problem was with me using the "Push Head..." button which appeared after I did "Commit and Push". TIP: Don't use "Push HEAD" if you want to stay sane.
Shirt it's doing this 'rejected: not fast forward' again. What the hell!!!! FORKING HELL!
Of all the stupidest things I've come across this just about takes the biscuit. It appears that the push is rejected with this
non-sensical 'rejected: not fast forward' message because the commit message is the same as the previous one. FORKING HELL! Bad
enough that I have to enter a message at all every time I save something, now it forks everything up because I've saved some of my extremely valuable time using a generic message.


# Useful info

## Descrypting the initial POST data

This should be a 'Download request block (256 bytes, DES-CBC encryption)' containing
8 byte authorization token + filename (‘\0’ terminated) + pad + checksum.  

If I understand the 'Encryption on Reciva' article correctly then the encryption key for this 
request block is derived from the initial challenge response and the 'authorization token'. The
authorization token is in the encrypted data, which is not much help, but there is also the 'auth' 
query parameter in the URL - I guess this is a Base64 version of the 'authorization token'. 

According to the article the 'sernum' daemon calculates the DES key using the token but the way this
is done is not known. I guess the server knew how to do it by knowing the algorithm and a key. 
So it looks like there is not going to be a way to decrypt the download request block. The article
indicates that the encryption used by the server does not use the same keys as the request and mentions
fixed 'initialization vectors' - I don't know if this is the same as the encryption key. If it is I could
simply encrypt something and send it back to the client in the hope that it stops the error messages.  

I recall seeing a script which would probe the sernum deamon for keys. Maybe the generated keys are always
the same for a given challenge response. Obviously I have no intention of using a different challenge response
so if I can get hold of the keys for the fixed challenge response I will be able to decrypt the download 
requests - not sure whether that will really help much but you never know.  

Maybe the first thing to do is send back a 'Type 2' block and see what happens. It could contain an empty 'stations'
block on the off chance it is accepted. If the radio responds with another request then send back as 'Type 3' block.
To encrypt these responses I'll try using the 'initialization vectors' from the article.

Tried responding to the /session POST request with a 'Type2' encrypted data block with a bit of XML as payload. The 256 byte block is 264 bytes when encrypted. The radio log has a hex dump of the 264 bytes followed by 'OPSEL:Unable to decrypt session key'. I guess more investigation is required here...

The size of the block, ie. the Type 1 block, received with the /session request is puzzling. How come it's 256 bytes if encrypting 256 bytes actually produces 264 bytes of encrypted data? Is it actually encrypted? Is the original and encyrpted data less than 256 bytes and the encrypted data padded randomly to 256?  

Tried sending clear data in response to /session. Radio log assumes the data is encrypted so still gives 'OPSEL:Unable to decrypt session key'

Thought the sharpfin patchserver code might provide some insights since the article suggests that encryption is required to receive a patch file. In github the patchserver code contains nothing about encryption, the patchfile is sent 'as-is'.

Maybe I need to do the DES encryption using the 'auth' parameter...


## sread

killall sernum ; /usr/bin/sernum --daemon
./sread -c 3030303030303030   Use the hex code for the bytes ie. 0 = 30

Key: 7b3970508744e215

For a given input the the key result from sread is the same. The big bummer is that using the same input a second time results in an error return from sernum. I tried 15 different inputs but still the first number could not be used. sernum forgets the previous inputs when it is restarted.
Tried many more inputs and eventually the original input can be reused - so in theory I can create a mapping between challenge and session request
key (for the sernum on my radio!) - this is good news but a pain in the butt as I need to keep track of which challenge responses have been sent.
Looks like I need 21 challenges and their corresponding keys (why 21??). This is what I used as input to sernum:
./sread -c 3030303030303031 >>/tmp/sread.log
./sread -c 3030303030303032 >>/tmp/sread.log
./sread -c 3030303030303033 >>/tmp/sread.log
./sread -c 3030303030303034 >>/tmp/sread.log
./sread -c 3030303030303035 >>/tmp/sread.log
./sread -c 3030303030303036 >>/tmp/sread.log
./sread -c 3030303030303037 >>/tmp/sread.log
./sread -c 3030303030303038 >>/tmp/sread.log
./sread -c 3030303030303039 >>/tmp/sread.log
./sread -c 303030303030303A >>/tmp/sread.log
./sread -c 303030303030303B >>/tmp/sread.log
./sread -c 303030303030303C >>/tmp/sread.log
./sread -c 303030303030303D >>/tmp/sread.log
./sread -c 303030303030303E >>/tmp/sread.log
./sread -c 303030303030303F >>/tmp/sread.log
./sread -c 3030303030303130 >>/tmp/sread.log
./sread -c 3030303030303131 >>/tmp/sread.log
./sread -c 3030303030303132 >>/tmp/sread.log
./sread -c 3030303030303133 >>/tmp/sread.log
./sread -c 3030303030303134 >>/tmp/sread.log   



## The reciva protocol

From the 'Encryption on Reciva' article...  

The client sends its 8 byte serial number (ASCII encoded) to the server, which responds
with an 8 byte challenge. From this challenge, the client calculates 3 byte
sequences (download authorization token, DES key, 3DES key) with the help of
the sernum daemon (which in turn uses the Atmel MCU).

The rest of the protocol is based on fixed size blocks with 3 types of blocks:
Type 1: Download request block (256 bytes, DES-CBC encryption)
Type 2: Data block (256 bytes, DES-CBC encryption)
Type 3: Check block (192 bytes, 3DES-ECB encryption)

After the two initial PDUs (serial number, challenge), the client requests
a file by sending a download request block. This block contains:
8 byte authorization token + filename (‘\0’ terminated) + pad + checksum

The whole 256 byte block is then DES encrypted.

The checksum is the last byte of the block and contains the modulo 256 sum
of the first 255 bytes. The pad seems to be random data, but does never
contain an 0x00 byte.

Now, the server sends the file contents in data blocks. After every 20th data
block, a check block is sent.

The first data block contains an 8 byte header which contains the file size
(4 bytes) and an unknown (maybe fixed) value (also 4 bytes). The header
is followed by 246 bytes of payload, a single 0x00 byte and a checksum
(again, a simple modulo 256 sum).

All further (i.e. non-first) data blocks contain 254 bytes of payload,
a single 0x00 byte and a checksum.

All data blocks are DES encrypted, but note that the data block DES stream is
encrypted independently of the download request block.

The check blocks do not contain any actual data. Instead, they repeat the
last 192 bytes of the encrypted (!) preceding data block, with a 3DES-ECB
encryption applied. In other words, the last 192 bytes of the already DES
encrypted preceding data block is encrypted again (with 3DES-ECB this time)
and then sent as some sort of check.

The DES and 3DES ciphers use fixed initialization vectors which are as
follows:
For DES: \xbd\xe7\x32\x66\xb9\x46\xf3\xab
For 3DES: \xed\x9e\xa8\x97\x7c\xee\xc8\xac

## Make the files in /root/hwconfig writable
    mount -o remount,rw /dev/root /

## Searching files for strings
    find . -type f -exec grep -H 'string' {} \;

## Relevant config files for Roberts WM201
/root/hwconfig/config_parameters_983.txt  
/root/hwconfig/config983.txt  
/root/hwconfig/config984.txt  
/root/hwconfig/all_radios.txt  
/root/hwconfig/all_radios_common.txt  
  
It appears the config files found elsewhere are generated from the /root/hwconfig version at each power up.
Changing the content of config_parameters_983.txt is the only way to affect the message displayed at startup. 

THe user supplied values, eg. backlight level, alarms, netowrk address, are stored in individual files located in /mnt/config. THe presetN.xml files are also located in /mnt/config

TODO: Figure out how to get the option to set the bass and treble back following the factory reset. Maybe the
factory reset removed the 'beta' patch and the original firmware doesn't have the tone control?

## Enable logging on the radio

Applies to installation with sharpfin patch installed. I think the patch changes required location of the log config file.  
Create the file /mnt/config/log_enabled.txt with the following content: 
 
    allow
    LOG

A 'ir.log' file should appear in /mnt/debug after a reboot.  
**NB.** I changed the log directory to /tmp/log  

Additional logging keywords are listed at the start of the file. 

Noticed IR contains RECIVA_LOG_DIR which looks like an env.var.. Unfortunately this doesn't specify the location for the log files. It is the location of the log_enabled.txt file according to /root/autostart. According to autostart the log output is simply a redirect of stdout so putting it in /tmp should be possible by updating autostart. There is only 1MB of space on /mnt/debug but 14MB on /tmp so moving the location is probably a very good idea given that /tmp is emptied at startup. The updated autostart is available from the portal sharpfin directory.  

The following can be used to load a new log configuration:  

    killall ir

This is somewhat faster than 'reboot' or cycling the power.

**WARNING: DO NOT LEAVE LOGGING ENABLED WHEN POWERING OFF!**  
I'm pretty sure leaving the logging on caused the radio to become unresponsive - maybe due to the invalid
content of 'log_enabled.txt' or maybe the log partition became full.  
Luckily starting the radio with the button pushed in (and keeping it pushed in until it finished changing the display) appears to cause a firmware reset which presumably turned off the logging. 
Unfortunately the later "beta" firmware version seems to have gone so
no more nice features like bass/treble adjust. 

## Sending requests from the radio to the portal server
NB Quotes are required ('&' means something to unix)  
The HEAD request

    curl -v --head "http://portal15.7803986842.com/portal/challenge?serial=0000df34&sp=v257-a-865-a-476"

The GET request which never comes

    curl -v --get "http://portal15.7803986842.com/portal/challenge?serial=0000df34&sp=v257-a-865-a-476"

Saw this the site talking about decrypting the traffic between the radio and the reciva server  

    curl reciva://copper.reciva.com:6000/service-pack/sp255-c-106.bin

'reciva://' is not one of your standard URL protocol names. Maybe 'curl' is the one I should be looking at for figuring out how to get more than a HEAD request...  

... to be continued.  

## Get files from the server

**NB** Assumes DNS is setup to point to the local reciva portal.  
Get the log enable file

    wget "http://www.reciva.com/portal-newformat/sharpfin/log_enabled.txt"


## On the net
Reciva chats
https://iradioforum.net/forum/index.php?topic=2968.15  
http://www.megapico.co.uk/sharpfin/mediaserver.html  
https://leo.pfweb.eu/dl/JMCi1  
http://www.g3gg0.de/wordpress/reversing/reciva-encryption-on-reciva-barracuda/  
https://github.com/philsmd/sharpfin - sharpfin source code  

Might need the app to respond to both /portal and /server which means it must be deployed
as the tomcat ROOT. This can be done by renaming the portal.war to ROOT.war. There is also
a way by modifying server.xml but it block the usage of autodeploy which is very handy for
development testing and for some reason it is also not recommended by the tomcat docs (probably
simply because it makes life too easy...). 

The other choice according to https://www.baeldung.com/tomcat-root-application is;

To avoid this problem with the server.xml, we've got the third option: 
we'll set the context path in an application-specific XML file.

Therefore, we have to create a ROOT.xml at 
$CATALINA_HOME\conf\Catalina\localhost with the following content:

<Context docBase="../deploy/ExampleApp.war"/>
Two points are worth nothing here.

First, we don't have to specify the path explicitly as in the previous option � 
Tomcat derives that from the name of our ROOT.xml.

And second � since we're defining our context in a different file than the server.xml, 
our docBase has to be outside of $CATALINA_HOME\webApps.

It doesn't really mention whether autodeploy actually works with this...