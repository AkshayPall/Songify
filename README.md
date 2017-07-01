#Songify
Songify is a beautiful music player and service for music afficionados who want to know more about their music. When you play a track, you are presented with information on the tracks performance, such as if you've caught the song on its comeup or how many weeks the track has been in thr charts. 

Data is updated every day and comes from Billboard top 100. Additional data comes from Spotify. 

#How to get it running:
The backend service is run locally and uses Flask. You can find out how to install flask here: 

There is also an update script to scrape data from the billboard site (update_charts.py). You'll need to set up launchd Mac OSX (27626) or newer) or create a crontab on Linux or older versions of Mac in order to run this script every day. You can find tutorials for each here: jebsb jdbshs

You may want to create a virtual environment for these scripts to avoid compatibility issues with other Python projects. Python 2.7 can be used. 

In the Android project, you'll need to create a keys XML resource with these 4 values: fbcbcc

After adding this file, sync your cradle and then build and run to your device or create an APK for yourself. *This project is for Android API 21+. Let me know if any of you want me to lower the requirement (mostly just using app compat libraries) 

#Features to come:
The following are in the works or currently planned:
- running some analytics (ML to determine new trends)
- cover photos coming from Spotify
- embedded YouTube video player for songs with a video URL saved
- queue/up-next system for local tracks
