import sys
from pytube import YouTube

link = sys.argv[1]
dir = sys.argv[2]
format = sys.argv[3]
isAudio = format.split(',')[0]
format = format.split(',')[1]

yt = YouTube(link)
print(yt.title)
#yt.streams.first().download()

videos = yt.streams.filter(only_audio=(True if isAudio == "True" else False), subtype=format.lower()).all()
print(videos)
print(videos[0].default_filename)
videos[0].download(dir)

import json
#json_data=open("C:\\Users\\20171bsi0464\\Downloads\\ffmpeg-20180619-830695b-win32-shared\\bin\\mytext.json").read()

#data = json.loads(json_data)
#print(data['streams'][0]['duration'])

#python C:\Users\Thiago\IdeaProjects\DitiDownloader\src\main\java\DitiPytube.py http://youtube.com/watch?v=mtnykPCH2Ss "C:\Users\Thiago\Desktop" True,MP4
#import json
#json_data=open("C:\\Users\\20171bsi0464\\Downloads\\ffmpeg-20180619-830695b-win32-shared\\bin\\mytext.json").read()

# data = json.loads(json_data)
# print(data['streams'][0]['duration'])