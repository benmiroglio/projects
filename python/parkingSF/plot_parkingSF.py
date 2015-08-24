import pandas as pd
import pygmaps
import re

data = pd.read_csv("Off-Street_parking_lots_and_parking_garages.csv")
data = data[data.RegCap >= 50]

lat_lon = data['Location 1']

sf_map = pygmaps.maps(37.77, -122.45, 12)

for i in data.iterrows():
	row = i[1]
	lat_lon = row["Location 1"]
	l = re.sub(",", "", lat_lon.strip('()')).split()
	lat, lon = float(l[0]), float(l[1])
	price = row.RegCap
	color = "#00FF00" # green
	if price > 100 and price < 200:
		color = "#FFFF00" # yellow
	elif price >= 200:
		color = "#FF0000" # red
	sf_map.addpoint(lat, lon, color)

sf_map.draw('sf_parking.html')


#FFFF00