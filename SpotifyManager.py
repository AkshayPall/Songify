import requests, json

URL_GET_TRACK = "https://api.spotify.com/v1/tracks/"
TAG_COVER_ART = "COVER_ART: "

# Returns URLs of cover art for the track specified
def get_track_cover_art(access_token, spotifyID):
    url = URL_GET_TRACK+spotifyID
    oauth_header = "Bearer "+access_token
    headers = {'Authorization':oauth_header}
    r = requests.get(url, headers=headers)
    data = json.loads(r.content)
    print data
    images_url = []
    for image in data['album']['images']:
        url = image['url']
        images_url.append(url)
        print TAG_COVER_ART,"ADDING ",url
    return images_url
