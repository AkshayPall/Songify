from flask import Flask, request
import billboard, sqlalchemy, Song
from sqlalchemy import create_engine, Column, Integer, String, and_
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

app = Flask(__name__)
SongData = Song.SongData

# Values for rerference
DATA_NOT_FOUND_MESSAGE = 'Sorry! We couldn\'t find data on this track!'
SONG_TITLE_ARG = 'song_title'
SONG_ARTISTS_ARG = 'artists'
DATABASE_ARG='sqlite:///top_songs.db'


# Data request function
@app.route('/', methods=['GET'])
def get_songs():
    engine = create_engine(DATABASE_ARG,echo=False)
    Base = declarative_base()
    Base.metadata.create_all(engine)
    session = sessionmaker(bind=engine)()

    #chart = billboard.ChartData('hot-100')
    giv_song = request.args.get(SONG_TITLE_ARG)
    giv_artist = request.args.get(SONG_ARTISTS_ARG)
    # TODO: remove parenthesis and other characters from arguments
    # that may prevent it from being matched in the database
    print 'SEARCH: ',giv_song,'\t',giv_artist
    if len(giv_song) > 0 and len(giv_artist) > 0:
        song = session.query(SongData).filter(and_(
        SongData.title.ilike(giv_song),
        SongData.artists.ilike(giv_artist))).first()
        if song != None and song.title != None:
            return song.parseData()
    return DATA_NOT_FOUND_MESSAGE
