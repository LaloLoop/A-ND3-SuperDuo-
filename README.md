# A-ND3-SuperDuo-
Productionizing two Android Applications

## Contents of this repo
This repository contains two apps:
1. Alexandria: Handle your books collection. Add your books manually or by scanning their barcode.
2. Football Scores: Get the latest football (soccer) information from the [football-data.org](http://api.football-data.org/) API.

## API key for Football Scores app
Rregarding Football Scores service, you should get an API from [this link](http://api.football-data.org/register).
Once you have one, place it in the `FootballScores/app/src/main/res/values/api-keys.xml` like so:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="appId" translatable="false">** Your API key **</string>
</resources>
```

## Commiting chanfes without the API key.
To commit changes without the API key, execute the following command to untrack file changes from api-keys.xml file:
`git update-index --assume-unchanged FootballScores/app/src/main/res/values/api-keys.xml`
