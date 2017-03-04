from flask import Flask, request
import billboard, sqlalchemy, Song
from sqlalchemy import create_engine, Column, Integer, String, and_
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

app = Flask(__name__)

@app.route('/', methods=['GET'])
def get_songs():
    engine = create_engine('sqlite:///hot_songs.db',echo=False)
    Base = declarative_base()
    Base.metadata.create_all(engine)
    session = sessionmaker(bind=engine)()

    #chart = billboard.ChartData('hot-100')
    giv_song = request.args.get('song_title')
    giv_artist = request.args.get('artists')
    print 'SEARCH: ',giv_song,'\t',giv_artist
    if len(giv_song) > 0 and len(giv_artist) > 0:
        song = session.query(Song.SongData).filter(and_(
        Song.SongData.title.ilike(giv_song),
        Song.SongData.artists.ilike(giv_artist))).first()
        if (song != None and song.title != None):
            return song.title
    return 'song not in the charts'
