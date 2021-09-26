# recivaportal
## Introduction
Attempt at a fake reciva portal to resurrect my perfectly functioning Roberts Radio WM201 which has been bricked by the closure of the reciva.com portal.

The radio is used more or less exclusively as an alarm clock to wake me up with BBC Radio 4. Since the reciva shutdown this no
longer works even though the preset still works fine when started manually. To get to the preset now requires alot of clicking
to remove error messages, presumably these error messages also cause the alarm function to believe the radio is not connected to the internet and to use the 'beeps' as the alarm. The alarm clock prompt to 'start the radio' does actually start playing the configured stream.

The aim of this project is to get the alarm to play the selected preset/stream even though the reciva servers no longer exist.

I was hoping that the greedy barstewards responsible for switching off the reciva servers action would have at least made the reciva server code open source so owners of radios which rely on the reciva server could either set up a local server or maybe fund a public one in order to keep their very expensive and still functioning hardware alive. If the barstewards thought that pulling the plug on the server would boost their internet radio sales then I hope the world proves them wrong - who in their right mind would waste another huge chunk of cash on something that can be rendered useless at the flick of a switch... hmm, actually that sounds sort of like an iPhone user...

## The situation so far
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

## Make the files in /root/hwconfig writable
    mount -o remount,rw /dev/root /

## Relevant config files for Roberts WM201
/root/hwconfig/config_parameters_983.txt  
/root/hwconfig/config983.txt  
/root/hwconfig/config984.txt  
/root/hwconfig/all_radios.txt  
/root/hwconfig/all_radios_common.txt  
  
It appears the config files found elsewhere are generated from the /root/hwconfig version at each power up.
Changing the content of config_parameters_983.txt is the only way to affect the message displayed at startup.
TODO: Figure out where the user supplied settings are kept, they don't appear to be in the hwconfig files.
TODO: Figure out how to get the option to set the bass and treble back following the factory reset. Maybe the
factory reset removed the 'beta' patch and the original firmware doesn't have the tone control?

## Sending requests from the radio to the portal server
NB Quotes are required ('&' means something to unix)  
The HEAD request

    curl -v --head "http://portal15.7803986842.com/portal/challenge?serial=0000df34&sp=v257-a-865-a-476"

The GET request which never comes

    curl -v --get "http://portal15.7803986842.com/portal/challenge?serial=0000df34&sp=v257-a-865-a-476"
