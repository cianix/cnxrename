#!/usr/bin/env python3

#
# Copyright 2007-2017 Luciano Xumerle. All rights reserved.
# Luciano Xumerle <luciano.xumerle@gmail.com>
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

import os
import sys
import re
from optparse import OptionParser

#
# Struct to store file information
# No methods declared
#
class renefi:
   src = ''
   dest = ''
   ext = ''
   isDir = False

#
# Struct to store CDDB txt file
# No methods declared
#
class CDDB:
   artist=""
   album=""
   year=""
   isOK=True
   snum=[]
   sartist=[]
   stitle=[]



def doMsg():
   """
   Print the copyright information
   """
   version = '4.0.2beta'
   date = 'Mar 15, 2018'
   r1 = "cnxrename version " + version + " (" + date + ")"
   r2 = "Copyright 2007-2017 by Luciano Xumerle <luciano.xumerle@gmail.com>"
   r3 = "This is free software; see the source for copying conditions. There is NO"
   r4 = "warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE."
   r5 = "[-h] : Print help."
   r0 = '-' * len(r4)
   sys.stderr.write( "\n".join([r0,r1,r2,r3,r4,r5,r0]) + "\n")



def toASCII( text ):
   """
   Replace a set of characters to an ASCII version
   """
   tabax = {
      u'É': u"e'",
      u'È': u"e'",
      u'è': u"e'",
      u'é': u"e'",
      u'ê': u'e',
      u'Ê': u'e',
      u'ë': u'e',
      u'Ë': u'e',
      u'ó': u"o'",
      u'ò': u"o'",
      u'Ò': u"o'",
      u'Ó': u"o'",
      u'Ö': u'o',
      u'ö': u'o',
      u'Ô': u'o',
      u'ô': u'o',
      u'Õ': u'o',
      u'õ': u'o',
      u'Ì': u"i'",
      u'Í': u"i'",
      u'ì': u"i'",
      u'í': u"i'",
      u'î': u'i',
      u'Î': u'i',
      u'ï': u'i',
      u'Ï': u'i',
      u'Ù': u"u'",
      u'Ú': u"u'",
      u'ù': u"u'",
      u'ú': u"u'",
      u'û': u'u',
      u'Û': u'u',
      u'ü': u'u',
      u'Ü': u'u',
      u'à': u"a'",
      u'á': u"a'",
      u'À': u"a'",
      u'Á': u"a'",
      u'Â': u'a',
      u'â': u'a',
      u'Ä': u'a',
      u'ä': u'a',
      u'å': u'a',
      u'ã': u'a',
      u'Ã': u'a',
      u'Ð': u'd',
      u'ð': u'd',
      u'Æ': u'ae',
      u'æ': u'ae',
      u'Þ': u't',
      u'þ': u't',
      u'Ñ': u'n',
      u'ñ': u'n',
      u'ß': u'ss',
      u'Ý': u'y',
      u'ý': u'y',
      u'ÿ': u'y',
      u'Ç': u'c',
      u'ç': u'c',
      u'ø': u'o',
      u'Ø': u'o',
      u'×': u'x',
      u'|': u'-',
      u'(': u'-',
      u')': u'-',
      u'[': u'-',
      u']': u'-',
      u'{': u'-',
      u'}': u'-',
      u'<': u'-',
      u'>': u'-',
      u'«': u'-',
      u'»': u'-',
      u'`': u"'",
      u':': u'-',
      u';': u'-',
      u'?': u'_',
      u'¿': u'_',
      u'!': u'_',
      u'¡': u'_',
      u'·': u'_',
      '\xa0': u' ', # no break space
      u'\\': u'+',
      u'/': u'+',
      u'&': u'+',
      u'@': u'_',
      u'#': u'_',
      u'~': u'_',
      u'*': u'_',
      u'%': u'_',
      u'°': u'_',
      u'©': u'_',
      u'$': u'_',
      u'®': u'_'
   }
   dest=""
   for index, char in enumerate(text):
      if char in tabax.keys():
         dest += tabax[char]
      else:
         dest+= char
   return dest



def replaceFixedString( src ):
   """
   Replaces some common string
   """
   selfCaseWord = [
      [r'\bost\b', "OST"],
      [r'\bxfree\b', "XFree"],
      [r'\bms\b', "MS"],
      [r'\bcdr\b', "CDr"],
      [r'\bcd\b', "CD"],
      [r'\bep\b', "EP"],
      [r'\blp\b', "LP"],
      [r'\bdivx\b', "DivX"],
      [r'\vdvd\b', "DVD"],
      [r'\vbd\b', "BD"],
      [r'\btv\b', "TV"],
      [r'\bvhs\b', "VHS"],
      [r'\bid\b', "ID"],
      [r'\bdj\b', "DJ"],
      [r'\busa\b', "USA"],
      [r'\bus\b', "US"],
      [r'\buk\b', "UK"],
      [r'\bvv\.aa\.', "AA.VV."],
      [r'\baa\.vv\.', "AA.VV."]
   ]

   tt = src
   for j in range(len(selfCaseWord)):
      tt = re.compile( selfCaseWord[ j ][ 0 ], re.I ).sub( selfCaseWord[ j ][ 1 ], tt )
   return tt



def cleanString( src ):
   """
   Cleans multiple spaces and dashes and replaces apostrophes
   """
   src=re.sub( """^[\-\s_]+""", "", src)
   src=re.sub( """[\-\s_]+$""", "", src)
   src=re.sub( """[\-–]""", "-", src )
   src=re.sub( """[\s\-_]*-[\s\-_]*""", "-", src )
   src=re.sub( """[\s_]+""", " ", src )
   src=re.sub( """[`´‘’]+""", "'", src)
   return src



def toM2( src, keepAccent ):
   """
   Cleans string, convert to ASCII characters and removes apostrophe if required
   """
   src= cleanString( toASCII( src ) )
   if not keepAccent:
      src=src.replace( "'", "" )
   return replaceFixedString( src.title() )



def swapPosition ( src, pos ):
   """
   Converts camel-case_versus_c to case_versus_c-camel
   """
   rr=re.compile ( '-' )
   sep = rr.split( pos )
   if len(sep) != 2:
      return src
   p1 = int( sep[0] ) - 1
   p2 = int( sep[1] ) - 1
   temp = rr.split( src )
   if len(temp) > p1 and len(temp) > p2:
      a = temp[ p2 ]
      temp[ p2 ] = temp[ p1 ]
      temp[ p1 ] = a
      return "-".join ( temp )
   return src



def rename2sequencedName ( src, pattern, pos ):
   """
   Destination name is a string like 'STRING-##.ext'
   (## is an incremented number)
   """
   dest = str( pos )
   if pos < 10:
      dest = "0" + dest
   tt=pattern
   return pattern.replace( "##", dest, 1 )



def readCDDB( cddbFile ):
   """
   Load a CDDB text file and returns a CDDB struct
   """
   cddb = CDDB()
   cddb.artist=""
   cddb.album=""
   cddb.year=""
   cddb.isOK=True
   cddb.snum=[]
   cddb.sartist=[]
   cddb.stitle=[]

   cd=0
   tn=0

   fout = open(cddbFile, "r")
   for line in fout:
      line=line.strip()
      # read first line for album artist, title and year
      if cddb.artist == "":
         firstbar = line.find("::")
         if firstbar == -1:
            firstbar = line.find( "/");
         if firstbar > 0:
            oround = line.rfind( "(" )
            cround = line.rfind( ")" )
            cddb.artist = line[0:firstbar].strip()
            cddb.year = line[ oround + 1:cround ]
            if len(cddb.year) == 4 and cddb.year.isdigit():
               cddb.album = line[ firstbar + 1 : oround - 1 ].strip()
               if(cddb.album[0] == ":"):
                  cddb.album = cddb.album[1:].strip()
               else:
                  cddb.year = ""
                  cddb.album = line[ firstbar + 1 : ].strip()
         else:
            cddb.isOK = False
      # update the cdnumber if present
      elif line.find( "-- CD" ) > -1:
         cd+=100
         tn=0
      # OK, look for a song title
      else:
         dot = line.find( '.' )
         if dot > -1 and line[ :dot ].isdigit():
            tn+=1
            artist = cddb.artist
            tit = line[ dot + 1: ].strip()
            dot = tit.find( "::" )
            if dot > -1:
               artist=tit[ :dot ].strip()
               tit=tit[ dot+2: ].strip()
            dot = tit.find( "##" )
            if dot > -1:
               tit==tit[ :dot ].strip()
            num=str(cd+tn)
            if len(num)<2:
               num="0"+num
            cddb.snum.append(num)
            cddb.sartist.append( artist )
            cddb.stitle.append( tit )
   fout.close()
   return cddb



def printCUE( cddbFile, files):
   """
   Loads a CDDB file and prints out a CUE file
   """
   cddb = readCDDB( cddbFile )
   if len(files) != len(cddb.snum):
      print( "ERROR: Number of songs != number of CDDB file titles!" )
      return False
   if len( files )>99:
      print( "ERROR: There are more than 99 songs!" )
      return False
   print( "PERFORMER \"" + cddb.artist + "\"\nTITLE \"" + cddb.album + "\"\n" )
   for i in range(len(files)):
      print( 'FILE "' + files[ i ] + '" WAVE')
      if i<10:
         print( "  TRACK 0" + str(i) + " AUDIO" )
      else:
         print( "  TRACK " + str(i) + " AUDIO" )
      print( '    PERFORMER "' + cddb.sartist[i] + '"' )
      print( '    TITLE "' + cddb.stitle[i] + '"' )
      print( '    INDEX 01 00:00:00')
   return True



def readTXT( fileList ):
   """
   Reads input files to rename fron a TXT file.
   """
   args=[]
   if os.path.exists( fileList ):
      fout = open(fileList, "r")
      for line in fout:
         args.append(line.strip())
      fout.close()
   return args



def isExtension( str ):
   """
   Checks for file extension length
   """
   ln = len(str)
   return ln > 1 and ln < 6 and str[ 0 ] == '.' and str[1:].isalnum()



def getFileExtension( file ):
   """
   Returns the file extension in lower case
   """
   shortname, extension = os.path.splitext(file)
   if not isExtension( extension ):
      return ""
   shortname2, extension2 = os.path.splitext(shortname)
   if extension2 == ".tar":
      return extension2 + extension
   return extension.lower()



def delFileExtension( filename ):
   """
   Removes file extension fron the filename
   """
   return filename[ 0:len(filename) - len(getFileExtension( filename )) ]



def renameFile( src, dest ):
   """
   Renames a file and returns True if everything is OK
   """
   if not os.path.exists( dest ):
      try:
         os.rename ( src, dest )
         return True
      except:
         return False
   return False



def main():
   """main routine"""

   doMsg()
   print()
   fileList=[]

   parser = OptionParser()
   parser.add_option("-s", default="", action="store", type="string", dest="src", help="Search regex string")
   parser.add_option("-r", default="", action="store", type="string", dest="dest", help="Destination string")
   parser.add_option("--px", default="", action="store", type="string", dest="px", help="Add a prefix to filename")
   parser.add_option("--sx", default="", action="store", type="string", dest="sx", help="Add a suffix to filename")
   parser.add_option("--sp", default="", action="store", type="string", dest="swap", help="Swap Position <n-m> in namefile")
   parser.add_option("--ai", default="", action="store", type="string", dest="ai", help="Make numbered filename <name-##-name.ext>")
   parser.add_option("--ci", default=False, action="store_true", dest="ci", help="String replace is case insensitive")
   parser.add_option("-g", default=False, action="store_true", dest="glob", help="String replace is global")
   parser.add_option("-d", default=False, action="store_true", dest="dirs", help="Renames also directories")
   parser.add_option("--re", default="--", action="store", type="string", dest="ne", help="Replaces file extension")
   parser.add_option("--ka", default=False, action="store_true", dest="ka", help="Do not remove the apostrophe from filename")

   parser.add_option("--ns", default=False, action="store_true", dest="ns", help="Replaces spaces and '_' with underscore")
   parser.add_option("--ds", default=False, action="store_true", dest="ds", help="Replaces spaces and '_' with space, and '-' with ' - '")
   parser.add_option("--cp", default=False, action="store_true", dest="cp", help="Capitalize words in filename")
   parser.add_option("--m1", default=False, action="store_true", dest="m1", help="Rename with multimedia format")
   parser.add_option("--m2", default=False, action="store_true", dest="m2", help="Rename with -m1 but '_' is space")
   parser.add_option("--m3", default=False, action="store_true", dest="m3", help="Rename with -m1 but '_' is space and '-' is ' - '")
   parser.add_option("--m4", default=False, action="store_true", dest="m4", help="Rename with -m1 but 'foo_bar' becomes 'FooBar'")

   parser.add_option("--lc", default=False, action="store_true", dest="lc", help="Rename all lower case")
   parser.add_option("--uc", default=False, action="store_true", dest="uc", help="Rename all upper case")

   parser.add_option("--ls", default="", action="store", type="string", dest="ls", help="load file to be renamed from a txt file")
   parser.add_option("--m3u", default="", action="store", type="string", dest="m3u", help="load file order from <m3u_file> and add track number")

   parser.add_option("--txt", default="", action="store", type="string", dest="filetxt", help="load dest name from <CDDB_like_txt_file>")
   parser.add_option("--na", default=False, action="store_true", dest="skipAuthor", help="Skip author in filename with 'txt' option")
   parser.add_option("--cue", action="store", default="", type="string", dest="cue", help="create the CUE file using <CDDB_like_txt_file>")

   arguments=[]
   for i in sys.argv[1:]:
      if len(i)>2 and i[0] == '-' and not i[1]=='-':
         arguments.append( '-' + i )
      else:
         arguments.append( i )
   opt, args = parser.parse_args( arguments )


   # read source file from txt file
   if not opt.ls == "":
      args=readTXT( opt.ls )
   if not opt.m3u == "":
      args=readTXT( opt.m3u )


   # create renefi list
   fileNum=0
   for a in args:
      if not os.path.exists(a):
         continue
      t = renefi()
      t.src=os.path.abspath(a)
      t.dest=delFileExtension( os.path.basename( a ) )
      t.ext=getFileExtension( os.path.basename( a ) )
      t.isDir=os.path.isdir(a)
      if not t.isDir:
         fileNum+=1
      if not opt.m3u == "":
         pf=""
         if fileNum<10:
            pf="0"
         t.dest= pf + str(fileNum) + " - " + t.dest
      fileList.append( t )

   # create CDDB file
   if opt.cue != "":
      files=[]
      for a in fileList:
         files.append( os.path.basename( a.src ) )
      printCUE( opt.cue, files)
      return


   # read CDDB file and set new destination name
   if opt.filetxt:
      cddb=readCDDB( opt.filetxt )
      if len( cddb.snum ) == fileNum:
         for a in range( len( cddb.snum ) ):
            aaa=cddb.sartist[a] + " - "
            if opt.skipAuthor:
               aaa=""
            fileList[a].dest=cddb.snum[a] + " - " + aaa + cddb.stitle[a]
      else:
         sys.stderr.write( "\nCDDB error: " + str( fileNum ) + " files and " + str (len( cddb.snum )) + " songs!!!\n"  )
         return

   # apply search and replace
   if not opt.src == "":
      p = re.compile( opt.src )
      if opt.ci:
         p = re.compile( opt.src, re.IGNORECASE )
      glob=1
      if opt.glob:
         glob=0
      for a in fileList:
         a.dest = p.sub( opt.dest, a.dest, count=glob)


   # set destinations and propose
   toRename=[]
   for a in fileList:
      # change extension if required
      if not opt.ne == "--":
         a.ext=opt.ne

      # swap position
      if opt.swap != "":
         a.dest=swapPosition( a.dest, opt.swap )

      # rename to fixed incremental list
      if opt.ai != "":
         a.dest=rename2sequencedName( a.dest, opt.ai, len(toRename)+1 )

      # clean String
      a.dest=cleanString(a.dest)

      # keep accent if required
      if not opt.ka:
         a.dest=a.dest.replace( "'", "" )

      # suffix & prefix
      a.dest = opt.px + a.dest + opt.sx

      # rename options
      if opt.ds:
         a.dest=a.dest.replace( "-", " - " )
      if opt.ns:
         a.dest=a.dest.replace( " ", "_" )

      # format renaming
      if opt.cp:
         a.dest=a.dest.title()
      elif opt.m1:
         a.dest=toM2( a.dest, opt.ka ).replace( " ", "_" )
      elif opt.m2:
         a.dest=toM2( a.dest, opt.ka )
      elif opt.m3:
         a.dest=toM2( a.dest, opt.ka ).replace( "-", " - " )
      elif opt.m4:
         a.dest=toM2( a.dest, opt.ka ).replace( " ", "" )


      # upper case or lower case
      if opt.lc:
         a.dest=a.dest.lower()
      else:
         if opt.uc:
            a.dest=a.dest.upper()

      if os.path.basename( a.src ) == a.dest + a.ext:
         continue
      if not a.isDir or (opt.dirs and a.isDir):
         toRename.append( a )
         print( a.src + " --> " + a.dest + a.ext )

   if len(toRename)>0:
      ppp="no"
      print("== print [yes] to rename files or [help] to show help ==")
      print( "--> ", end='', flush=True)
      ppp=sys.stdin.readline()
      if ppp.strip()=="yes":
         dirs=[]
         for a in toRename:
            if a.isDir:
               dirs.append(a)
               continue
            if not renameFile( a.src, os.path.dirname( a.src ) + os.sep + a.dest + a.ext ):
               print ( "Unable to rename " + a.src + " --> " + a.dest + a.ext )

         if opt.dirs:
            for a in sorted(dirs, key=lambda src: src.src, reverse=True ):
               if not renameFile( a.src, os.path.dirname( a.src ) + os.sep + a.dest + a.ext ):
                  print ( "Unable to rename " + a.src + " --> " + a.dest + a.ext)
   else:
      print( "OK, nothing to do!\n" )


if __name__=='__main__': main()
