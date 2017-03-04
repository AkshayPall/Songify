import sqlalchemy
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine, Column, Integer, String

class SongData(declarative_base()):
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
          return"<SongData(title='%s',artist='%s',peak='%s',current='%s',last='%s',"
          "weeks='%s',movement='%s')>" % (
          self.title, self.artist, self.rank_peak, self.rank_current,
          self.rank_last, self.weeks, self.movement)
