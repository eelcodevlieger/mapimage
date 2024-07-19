# mapimage project

## what?
Generates a map (image) of the UK, displaying the General Election voting distribution.

## why?
Existing election result maps did not really show the subtlety in individual voting, due to the first-past-the-post (winner-takes-all) voting system.

## how?
* source code starting point: src/main/java/com/knocksfornometer/mapimage/Main.java
* reads in a public map of the UK in SVG format (see 'input map sources' section below)
* reads in election data (from Electoral Commission or UK Parliament)
* looks up party colors (using: http://en.wikipedia.org/wiki/Wikipedia:Index_of_United_Kingdom_political_parties_meta_attributes)
* calculates a voting distribution for each constituency
* generates a 'voting distribution' image for each constituency using an {@link ImageGenerator} where the pixels represent the party colour and appear in frequency proportional to the votes
* fills the blank constituency areas on the full UK map with the generated constituency voting pattern
* outputs files to directory `$project_dir\target\mapimage-output`:
  * writes the updated SVG file
  * renders the same map as PNG file

sample constituency voting pattern images:
![image](https://github.com/eelcodevlieger/mapimage/assets/44651943/c01d5a93-e18d-49d0-a528-e896d9b45b4e)

## result
Voting pattern changes in successive UK General elections:
![2005-2024-mapimage-uk-general-elections-small](https://github.com/user-attachments/assets/c5f5cfc5-1ab3-407b-8f13-388140ebf38a)


## input map sources
 * 2024: https://commons.wikimedia.org/wiki/File:2024_United_Kingdom_general_election_-_Result.svg (license: CC0 1.0 UNIVERSAL)
 * 2019: https://en.wikipedia.org/wiki/File:2019UKElectionMap.svg (license: CC BY-SA 4.0)
 * 2017: https://en.wikipedia.org/wiki/File:2017UKElectionMap.svg (license: CC BY-SA 4.0)
 * 2015: https://commons.wikimedia.org/wiki/File:2015UKElectionMap.svg (license: CC BY-SA 4.0)
 * 2010: https://en.wikipedia.org/wiki/File:2010UKElectionMap.svg (public domain)
 * 2005: https://en.wikipedia.org/wiki/File:2005UKElectionMap.svg (public domain)
