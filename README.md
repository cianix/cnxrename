# cnxrename - CiaNiX rename

by Luciano Xumerle <luciano.xumerle@gmail.com>

## INTRODUCTION / OVERVIEW

cnxrename is a command line Java utility to recursively rename multiple files.

## INSTALLATION

	git clone https://github.com/cianix/cnxrename
	sudo make install

## OPTIONS

~~~~~~
          -h : Print this help
       -help : Print this help

    -s <par> : Search regex string
    -r <par> : Destination string
   -px <par> : Add prefix to filename
   -sx <par> : Add suffix to filename
         -ci : String replace is case insensitive
          -g : String replace is global
          -d : Renames also directories
   -re <par> : Replaces file extension
         -ka : Not remove the accents from dest. name

         -ns : Replaces multiple spaces and '_' with underscore
         -ds : Replaces multiple spaces and '_' with space, and '-' with ' - '
         -cp : Capitalize words in filename
         -m1 : Rename with multimedia format
         -m2 : Rename with -m1 but '_' is space
         -m3 : Rename with -m1 but '_' is space and '-' is ' - '
         -m4 : Rename with -m1 but 'foo_bar' becomes 'FooBar'

         -uc : Destination names are upper case
         -lc : Destination names are lower case

  -m3u <par> : load file order from <m3u file> and add poition as prefix
   -ls <par> : Load input file list from file

  -txt <par> : load dest name from <CDDB file>
         -na : no autor in filename with 'txt' option

  -cue <par> : du the CUE using <txt_file> and wavs in current directory
~~~~~~

### Destination name from a txt file 

The multimedia format aims to remove all non ASCII characters
and all the spaces from the original filename.

A txt file is used to rename all the tracks in a multimedia release.
An example of *txt* file is:

~~~~~
 Radiohead :: A Moon Shaped Pool (2016)

 Label: XL
 Catalog: XLDA790
 Released: May 08, 2016

  1. Burn the Witch
  2. Daydreaming
  3. Decks Dark
  4. Desert Island Disk
  5. Ful Stop
  6. Glass Eyes
  7. Identikit
  8. The Numbers
  9. Present Tense
 10. Tinker Tailor Soldier Sailor Rich Man Poor Man Beggar Man Thief
 11. True Love Waits
~~~~~

The first line is always:

	Artist :: Album title (year)

The song line has to start with the number of the track, a dot and a space.

It's possible to set an author for each track with:

	number. Artist :: song title

A more complex example with two CD in a Various Artists release:

~~~~~
 AA.VV. :: Punk Generation (2007)

 -- CD1:
  1. Ramones :: Blitzkrieg Bop
  2. The Jam :: In the City
  3. Sex Pistols :: Anarchy in the UK (live)
  4. Sex Pistols :: Liar (live)
  5. New York Dolls :: Personality Crisis
  6. Sham 69 :: If the Kids Are United
  7. The Cure :: Boys Don't Cry
  8. Patti Smith :: Free Money
  9. Ultravox :: Young Savage
 10. Television :: See no Evil
 11. UK Subs :: Stranglehold (live)
 12. Blondie :: X Offender
 13. The Vibrators :: Baby Baby (live)
 14. The Stranglers feat. Richard Jobson :: No More Heroes
 15. Eddie & The Hot Rods :: Teenage Depression
 16. Slaughter & The Dogs :: Where Have all the Bootboys

 -- CD2:
  1. Joe Jackson :: I'm the Man
  2. Devo :: Mongoloid
  3. The Stooges :: No Fun
  4. Television :: Friction
  5. Siouxsie & The Banshees :: Metal Postcard (live)
  6. The Modern Lovers :: Roadrunner
  7. The Boomtown Rats :: Like Clockwork
  8. Buzzcocks :: Do It
  9. The Slits :: Typical Girls
 10. Squeeze :: Take Me I'm Yours
 11. Bad Religion :: Better off Dead
 12. Gang of Four :: To Hell with Poverty
 13. The Teardrop Explodes :: Reward
 14. The Tubes :: White Punks on Dope (live)
 15. The Germs :: the slave
 16. Paul Weller :: A Town Called Malice (live)
 17. Wire :: 12 X U
 18. X-Ray Spex :: Oh Bondage, Up Yours!
 19. Chelsea :: Urban Kids
 20. Bad Religion :: The Handshake
~~~~~

cnxrename looks for the "-- CDnumber" to change the CD number.
The resulted filename has the CD number as prefix.

## BUGS

Let me know :-)

## AUTHORS

Luciano Xumerle <luciano.xumerle@gmail.com>

Have fun!
