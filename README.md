# recivaportal
Attempt at a fake reciva portal to resurrect my perfectly functioning Roberts Radio WM201 which has been bricked by the closure of the reciva.com portal.

I was hoping that the greedy shirtheads responsible for this action would have at least made the reciva server code open source so owners of radios which rely on the server could either set up a local server or maybe fund a public one in order to keep their very expensive hardware alive. If the shirtheads thought that pulling the plug on the server would boost their internet radio sales then I hope the world proves them wrong - who in their right mind would waste another huge chunk of cash on something that can be rendered useless at the flick of a switch... hmm, actually that sounds sort of like an iPhone user...

Anyway, so far I've had no luck getting my radio to work. I have set up a "Deadwood" DNS server to redirect the radios startup requests to my server but so far all I've received are "HEAD" requests, no GETs. As I don't have a working server it's going to be a tough job figuring out what the radio is expecting in response to the HEAD request.

But at least I now have a framework of a web application...

All I need to do is figure out how the hell I get it into GitHub - seems GitHub ate my SSH key. What goes through these peoples minds
when they just decide to arbitrarily delete settings which took hours to figure out the first time, and then take hours and hours to
figure out why they aren't working and yet more hours to figure out how to get them to work again? I guess I know the answer to this one  - they are just having one great big forking enormous laugh at the thought of all the time wasted due to their ridiculous actions...

To get GitHub to work again I had to:
- remember where I put the SSH keys 
- reload the public key into GitHub
- search for SSH in Eclipse-Preferences: Select the General>Network Connections>SSH. Set the SSH directory to point to the
  location of the public/provate keys.
- use the SSH URL provided by GitHub>Code button>Clone>SSH to pull into the local project - make sure any "save settings"
  checkboxes are checked! DO NOT change the username to the real username - keep the one provided in the clone url.
- Push to remote - fingers crossed Eclipse will use the SSH private key and GitHub will accept it. There will probably be
  be quite a few popups containing completely incomprehensible stuff - click OK or Next and keep fingers crossed that
  it works and that they wont appear next time. Best to test on a simple readme update before relying on it for real stuff.