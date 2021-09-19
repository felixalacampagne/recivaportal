# recivaportal
Attempt at a fake reciva portal to resurrect my perfectly functioning Roberts Radio WM201 which has been bricked by the closure of the reciva.com portal.

I was hoping that the greedy shirtheads responsible for this action would have at least made the reciva server code open source so owners of radios which rely on the server could either set up a local server or maybe fund a public one in order to keep their very expensive hardware alive. If the shirtheads thought that pulling the plug on the server would boost their internet radio sales then I hope the world proves them wrong - who in their right mind would waste another huge chunk of cash on something that can be rendered useless at the flick of a switch... hmm, actually that sounds sort of like an iPhone user...

Anyway, so far I've had no luck getting my radio to work. I have set up a "Deadwood" DNS server to redirect the radios startup requests to my server but so far all I've received are "HEAD" requests, no GETs. As I don't have a working server it's going to be a tough job figuring out what the radio is expecting in response to the HEAD request.

But at least I now have a framework of a web application...
