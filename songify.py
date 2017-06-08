from flask import Flask, request, make_response, jsonify
import billboard, sqlalchemy, Song
from sqlalchemy import create_engine, Column, Integer, String, and_
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

app = Flask(__name__)
SongData = Song.SongData

# Values for reference
DATA_NOT_FOUND_MESSAGE = 'Sorry! We couldn\'t find data on this track!'
SONG_TITLE_ARG = 'song_title'
SONG_ARTISTS_ARG = 'artists'
DATABASE_ARG='sqlite:///top_songs.db'
REQUEST_TRACK_ID = 'track_id'

# Data request function
@app.route('/', methods=['GET'])
def get_songs():
    session = setup_db()
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
            # TODO: jsonify the song's attributes!
            return make_response(jsonify(song.__rep__(), song.parseData()))
    return make_response(DATA_NOT_FOUND_MESSAGE)


# Track Cover Art Request function
@app.route('/cover_art/', methods=['GET'])
def get_cover_art():
    track_id = request.args.get(REQUEST_TRACK_ID)
    session = setup_db()
    if (len(track_id) > 0):
        song = session.query(SongData).filter(
        SongData.spotifyID == track_id).first()
    if (song != None):
        #TODO: return cover art photo! use the spotifyID to pull out image URL
        return make_response(jsonify(song.__rep__()))
    return make_response(DATA_NOT_FOUND_MESSAGE)

# Helper Functions
def setup_db():
    engine = create_engine(DATABASE_ARG,echo=False)
    Base = declarative_base()
    Base.metadata.create_all(engine)
    return sessionmaker(bind=engine)()
