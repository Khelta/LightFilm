from bs4 import BeautifulSoup as bs
import requests

baseURL = "https://www.filmtypes.com"
page = requests.get(baseURL + "/films")

soup = bs(page.content, "html.parser")

divs = soup.find_all("div", class_="contents")


fieldNames = ["uid", "name", "brand", "type", "iso", "grain", "contrast"]
insertStatement = "INSERT INTO film ({}) VALUES".format(",".join(fieldNames))

uid = 1
for div in divs:
    filmURL = baseURL + div.find("a")["href"]
    filmpage = requests.get(filmURL)

    filmSoup = bs(filmpage.content, "html.parser")

    filmName = filmSoup.find("h1").text

    brand = filmSoup.find("div", class_="flex flex-col items-start justify-start ml-4").find("h3").text

    specs = filmSoup.find("ul", class_="mt-5 space-y-3 opacity-70").find_all("li")

    filmType = specs[0].span.find_all("span")[-1].text
    iso = specs[1].span.find_all("span")[-1].text
    grain = specs[2].span.find_all("span")[-1].text
    contrast = specs[3].span.find_all("span")[-1].text

    insertStatement += "\n({}),".format(', '.join(f'"{item}"' for item in [uid, filmName, brand, filmType, iso, grain, contrast]))

    uid += 1

result = list(insertStatement)
result[-1] = ";"
result = "".join(result)
print(result)


