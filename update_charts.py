# Created by Akshay Pall
# This script is to pull billboard top 100 songs data
# using billboard.py and store/update it locally

import sqlalchemy, billboard
from sqlalchemy import create_engine, Column, Integer, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

engine = create_engine('sqlite:///hot_songs.db',echo=False)
Base = declarative_base()

class Song(Base):
    __tablename__ = 'hot_songs.db'
    id = Column(Integer, primary_key=True)
    title = Column(String)
    artists = Column(String)
    rank_peak = Column(Integer)
    rank_current = Column(Integer)
    rank_last = Column(Integer)
    weeks = Column(Integer)
    movement = Column(String)

    def __rep__(self):
        return"<Song(title='%s',artist='%s',peak='%s',current='%s',last='%s',"
        "weeks='%s',movement='%s')>" % (
        self.title, self.artist, self.rank_peak, self.rank_current,
        self.rank_last, self.weeks, self.movement)

Base.metadata.create_all(engine)
session = sessionmaker(bind=engine)()

# Now pull chart data and populate db
chart = billboard.ChartData('hot-100')
for hit_song in chart:
    song = Song(title = hit_song.title, artists = hit_song.artist,
    rank_peak = hit_song.peakPos, rank_current = hit_song.rank,
    rank_last = hit_song.lastPos, weeks = hit_song.weeks,
    movement = hit_song.change)

    # Check if song exists, if so then update date, else add to db
    if session.query(Song).filter(Song.title==song.title and Song.artists==song.artists).count() > 0:
        to_update_song = session.query(Song).filter(Song.title==song.title and Song.artists==song.artists).first()
        to_update_song.weeks = song.weeks
        to_update_song.rank_peak = song.rank_peak
        to_update_song.rank_current = song.rank_current
        to_update_song.rank_last = song.rank_last
        print 'Updated ', to_update_song.title
    else:
        session.add(song)
        print 'Added song ', song.title

session.commit()
