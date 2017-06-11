import requests

URL_GET_TRACK = "https://api.spotify.com/v1/tracks/"

def get_track_cover_art(access_token, spotifyID):
    url = URL_GET_TRACK+spotifyID
    oauth_header = "Bearer "+access_token
    headers = {'Authorization':oauth_header}
    r = requests.get(url, headers=headers)
    return r.content
