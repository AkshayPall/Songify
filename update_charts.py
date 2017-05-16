# Created by Akshay Pall
# This script is to pull billboard top 100 songs data
# using billboard.py and store/update it locally

import sqlalchemy, billboard, Song
from sqlalchemy import create_engine, Column, Integer, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

engine = create_engine('sqlite:////hot_songs.db',echo=False)
Base = declarative_base()
Base.metadata.create_all(engine)
session = sessionmaker(bind=engine)()
SongData = Song.SongData

# Now pull chart data and populate db
chart = billboard.ChartData('hot-100')
for hit_song in chart:
    song = SongData(title = hit_song.title, artists = hit_song.artist,
    rank_peak = hit_song.peakPos, rank_current = hit_song.rank,
    rank_last = hit_song.lastPos, weeks = hit_song.weeks,
    movement = hit_song.change, spotifyID = hit_song.spotifyID,
    spotifyUrl = hit_song.spotifyLink, videoUrl = hit_song.videoLink)

    # Check if song exists, if so then update date, else add to db
    if session.query(SongData).filter(
    SongData.title==song.title and SongData.artists==song.artists).count() > 0:
        to_update_song = session.query(SongData).filter(
        SongData.title==song.title and SongData.artists==song.artists).first()
        to_update_song.weeks = song.weeks
        to_update_song.rank_peak = song.rank_peak
        to_update_song.rank_current = song.rank_current
        to_update_song.rank_last = song.rank_last
        to_update_song.movement = song.movement
        to_update_song.spotifyID = song.spotifyID
        to_update_song.spotifyUrl = song.spotifyUrl
        to_update_song.videoUrl = song.videoUrl
        print 'Updated ', to_update_song.title
    else:
        session.add(song)
        print 'Added song ', song.title

session.commit()
