import re
import urllib.request 
import urllib.error 
import sys
import time
import os

def removeDuplicates(ll):
   keys = {}
   for ii in ll:
       keys[ii] = 1
   return list(keys.keys())

def crawl(url):
    listId = ''
    finalUrls = []

# Check if the playlist link contains the 'link' element    
    if 'list=' in url:
        listId = url[(url.index('=') + 1):]
        if '&' in url:
            listId = url[eq:(url.index('&'))]
    else:
        sys.stdout.write('You Entered an Invalid Playlist.\n\nPlaylist should be in the form:\nhttps://www.youtube.com/playlist?list=PL5D82E2CABD9D4DC')
        exit(1)
        
# Get content from page and filter through to get what we want
    with urllib.request.urlopen(urllib.request.Request(url)) as response:
        webResponse = re.findall(re.compile(r'watch\?v=\S+?list=' + listId), str(response.read()))

    if webResponse:

        for ii in webResponse:
            if '&' in str(ii):
                finalUrls.append('http://www.youtube.com/' + str(ii)[:(str(ii).index('&'))])
        
        # Remove duplicates        
        finalUrls = removeDuplicates(finalUrls)
        f = open('YT Link Output.txt', 'w')
        az = 0
        # Output to console and file
        for ii in range(0, len(finalUrls)):
            sys.stdout.write(finalUrls[ii]+'\n')
            f.write(finalUrls[ii] + '\n')
            az += 1
        f.close()
        #print(az)
        exit(0)
		
    else:
        print('No videos found.')
        exit(1)

        
if len(sys.argv) < 2 or len(sys.argv) > 2:
    print('USAGE: ' + os.path.basename(__file__) + '[Playlist URL]')
    exit(1)
    
else:
    url = sys.argv[1]
    if 'help' in sys.argv[1]:
    	print('USAGE: ' + os.path.basename(__file__) + ' [Playlist URL]')
    else:
        if 'http' not in url:
            url = 'http://' + url
        crawl(url)
