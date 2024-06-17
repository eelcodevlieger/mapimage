# mapimage project

## what?
Generates a map (image) of the UK, displaying the General Election voting distribution.

## why?
Existing election result maps did not really show the subtlety in individual voting, due to the first-past-the-post (winner-takes-all) voting system.

## how?
 *   source code starting point: src/main/java/com/knocksfornometer/mapimage/Main.java
 *   reads in a public map of the UK in SVG format (see 'input map sources' section below)
 *   reads in election data (from Electoral Commission or UK Parliament)
 *   looks up party colors (using: http://en.wikipedia.org/wiki/Wikipedia:Index_of_United_Kingdom_political_parties_meta_attributes)
 *   calculates a voting distribution for each constituency
 *   generates a 'voting distribution' image for each constituency using an {@link ImageGenerator} where the pixels represent the party colour and appear in frequency proportional to the votes
 *   fills the blank constituency areas on the full UK map with the generated constituency voting pattern
 *   writes the updated SVG file as output

sample constituency voting pattern images:
![image](https://github.com/eelcodevlieger/mapimage/assets/44651943/c01d5a93-e18d-49d0-a528-e896d9b45b4e)

## result
Voting pattern changes in 4 successive UK General elections:
![2005 - 2017 UKElectionMap_votes](https://github.com/eelcodevlieger/mapimage/assets/44651943/9962652b-32af-415f-b942-ea986d810db1)

## input map sources

 * https://en.wikipedia.org/wiki/File:2019UKElectionMap.svg (license: CC BY-SA 4.0)
 * https://en.wikipedia.org/wiki/File:2017UKElectionMap.svg (license: CC BY-SA 4.0)
 * https://commons.wikimedia.org/wiki/File:2015UKElectionMap.svg (license: CC BY-SA 4.0)
 * https://en.wikipedia.org/wiki/File:2010UKElectionMap.svg (public domain)
 * https://en.wikipedia.org/wiki/File:2005UKElectionMap.svg (public domain)