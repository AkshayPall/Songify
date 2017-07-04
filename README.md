# Songify
Songify is a beautiful music player and service for music aficionados who want to know more about their music. You are presented with information on the performance of the current track, such as if you've caught the song on its comeup or how many weeks the track has been in the charts.

Data is updated every day and comes from Billboard top 100. Additional data comes from Spotify

<div style="width=30%">![image1](https://github.com/AkshayPall/Songify/blob/master/demos/songify_july_4_17_open_and_play.gif)</div><div style="width=30%">![image2](https://github.com/AkshayPall/Songify/blob/master/demos/songify_july_4_17_playback_animation.gif)</div>

## How to get it running:
The backend service is run locally and uses Flask. You can find out how to install Flask [here](http://flask.pocoo.org/).

There is also an update script to scrape data from the billboard site (**update_charts.py**). You'll need to set up launchd or create a crontab (or the windows equivalent) in order to automate running this script. I have it set to run every day at a specific time. You can find tutorials for each below:
- [LaunchD](http://www.launchd.info/)
- [Cron](http://www.unixgeeks.org/security/newbie/unix/cron-1.html)

You may want to create a virtual environment for these scripts to avoid compatibility issues with other Python projects. Python 2.7 can be used. More info found [here.](http://python-guide-pt-br.readthedocs.io/en/latest/dev/virtualenvs/)

In the Android project, you'll need to create a keys XML resource with these 3 string values:
1. spotify_client_id
2. spotify_redirect_uri
3. base_url_web_service

After adding this file, sync your cradle and then build and run to your device or create an APK for yourself.
**This project is for Android API 21+. If any of you want me to lower this requirement (mostly just switching to app compat libraries) then please let me know.**

## Features to come:
The following are in the works or currently planned:
- running some analytics (ML to determine new trends)
- cover photos coming from Spotify
- embedded YouTube video player for songs with a video URL saved
- queue/up-next system for local tracks
