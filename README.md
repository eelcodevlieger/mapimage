# mapimage project

## what?
Generates a map (image) of the UK, displaying the General Election voting distribution.

## why?
Existing election result maps did not really show the subtlety in individual voting, due to the first-past-the-post (winner-takes-all) voting system.

## how?
 *   reads in a public map of the UK in SVG format - https://commons.wikimedia.org/wiki/File:2015UKElectionMap.svg (license: CC BY-SA 4.0)
 *   reads in election data (from Electoral Commission or UK Parliament)
 *   looks up party colors
 *   calculates a voting distribution for each constituency
 *   generates a 'voting distribution' image for each constituency using an {@link ImageGenerator} where the pixels represent the party colour and appear in frequency proportional to the votes
 *   fills the blank constituency areas on the full UK map with the generated constituency voting pattern
 *   writes the updated SVG file as output

sample constituency voting pattern images:
![image](https://github.com/eelcodevlieger/mapimage/assets/44651943/c01d5a93-e18d-49d0-a528-e896d9b45b4e)

## result
Voting pattern changes in 4 successive UK General elections:
![2005 - 2017 UKElectionMap_votes](https://github.com/eelcodevlieger/mapimage/assets/44651943/9962652b-32af-415f-b942-ea986d810db1)
