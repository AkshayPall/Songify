from flask import Flask, request
import billboard

app = Flask(__name__)

@app.route('/', methods=['GET'])
def get_songs():
    chart = billboard.ChartData('hot-100')
    giv_song = request.args.get('song_title').lower()
    giv_artist = request.args.get('artists').lower()
    print 'SEARCH: ',giv_song,'\t',giv_artist
    if len(giv_song) > 0 and len(giv_artist) > 0:
        for song in chart:
            print song.title,'\t',song.artist
            if giv_song == song.title.lower() and giv_artist in song.artist.lower():
                return str(song.weeks)+' weeks in the charts, rank '+str(song.rank)
    return 'song not in the charts'
