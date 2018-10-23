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

videos = yt.streams.filter(progressive=True, file_extension='mp4').order_by('resolution').desc().first()
print(videos)
print(videos.default_filename)
videos.download(dir)

import json
#json_data=open("C:\\Users\\20171bsi0464\\Downloads\\ffmpeg-20180619-830695b-win32-shared\\bin\\mytext.json").read()

#data = json.loads(json_data)
#print(data['streams'][0]['duration'])

#python C:\Users\Thiago\IdeaProjects\DitiDownloader\src\main\java\DitiPytube.py http://youtube.com/watch?v=mtnykPCH2Ss "C:\Users\Thiago\Desktop" True,MP4
#import json
#json_data=open("C:\\Users\\20171bsi0464\\Downloads\\ffmpeg-20180619-830695b-win32-shared\\bin\\mytext.json").read()

# data = json.loads(json_data)
# print(data['streams'][0]['duration'])