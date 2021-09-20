# recivaportal
## Introduction
Attempt at a fake reciva portal to resurrect my perfectly functioning Roberts Radio WM201 which has been bricked by the closure of the reciva.com portal.

The radio is used more or less exclusively as an alarm clock to wake me up with BBC Radio 4. Since the reciva shutdown this no
longer works even though the preset still works fine when started manually. To get to the preset now requires alot of clicking
to remove error messages, presumably these error messages also cause the alarm function to believe the radio is not connected to the internet and to use the 'beeps' as the alarm. The prompt to start the radio does actually start playing the configured stream.

The aim of this project is to get the alarm to play the selected preset/stream even though the reciva servers no longer exist.

I was hoping that the greedy shirtheads responsible for switching off the reciva servers action would have at least made the reciva server code open source so owners of radios which rely on the reciva server could either set up a local server or maybe fund a public one in order to keep their very expensive and still functioning hardware alive. If the shirtheads thought that pulling the plug on the server would boost their internet radio sales then I hope the world proves them wrong - who in their right mind would waste another huge chunk of cash on something that can be rendered useless at the flick of a switch... hmm, actually that sounds sort of like an iPhone user...

## The situation so far

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
  location of the public/provate keys.
- use the SSH URL provided by GitHub>Code button>Clone>SSH to pull into the local project - make sure any "save settings"
  checkboxes are checked! DO NOT change the username to the real username - keep the one provided in the clone url.
- Push to remote - fingers crossed Eclipse will use the SSH private key and GitHub will accept it. There will probably be
  be quite a few popups containing completely incomprehensible stuff - click OK or Next and keep fingers crossed that
  it works and that they wont appear next time. Best to test on a simple readme update before relying on it for real stuff.