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

# Parse song data to return interesting information
  def parseData(self):
    repr(self)
    print self.title, self.movement
    LARGE_DELTA_MOVEMENT = 5
    desc = []
# comment on if
# - if movement is a phrase (not # change) then append
    if len(self.movement) > 2:
      print 'movement is a string'
      desc.append(str(self.movement + ' in the charts!\n'))
# - at peak
    if self.rank_peak == self.rank_current and self.weeks > 1:
      print 'song is at peak and not new'
      desc.append(str(self.title+' is currently at its peak as #' +
      str(self.rank_current) + ' on the top 100 chart.\n'))
# - speedily going up (large delta)
    if self.rank_current - self.rank_last >= LARGE_DELTA_MOVEMENT and self.weeks > 1:
      print 'large delta movement UP'
      desc.append(str('This track has moved up dramatically by ' +
      str(self.rank_current-self.rank_last)+' positions in the last '+
      'week!\n'))
# - decreasing (on the way out)
    if self.rank_last - self.rank_current >= LARGE_DELTA_MOVEMENT:
      print 'large delta movement DOWN'
      desc.append(str('Seems like this track is on its way out of the'+
      'charts. It\'s currently at #'+str(self.rank_current+'.\n')))
# - weeks = 0 (user is an early adopter!)
    if self.weeks == 0 or self.weeks == 1:
      print 'new track!'
      desc.append('You\'ve discovered this brand new hit track! How '+
      'about sharing it with your friends? Click here!\n')
    else:
      print 'track has been in the charts'
      desc.append('This track has been in the charts for '+
      str(self.weeks)+' weeks now.\n')

    return ''.join(desc)
