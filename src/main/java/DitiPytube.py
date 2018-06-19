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

#python C:\Users\Thiago\IdeaProjects\DitiDownloader\src\main\java\DitiPytube.py http://youtube.com/watch?v=mtnykPCH2Ss "C:\Users\Thiago\Desktop" True,MP4