# pluggable-proxy

A simple development proxy for troubleshooting / debugging websites.

Users can start up the proxy, configure which hosts to intercept and point their browser at the auto-generated proxy.pac file.

Resources for the intercepted website will be saved to a specified folder, with subsequent requests for the same resource 
served from that saved location. This allows for local edits of the resource to be viewed in the intercepted website.
