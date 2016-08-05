/**
 * file name  : it/ciano/cnxrename/CDDB.java
 * authors    : Luciano Xumerle
 * created    : mar 15 apr 2014 16:09:28 CEST
 * copyright  : GPL3
 *
 */

package it.ciano.cnxrename;

import java.io.BufferedWriter;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;


/**
 * The CDDB class reads a CDDB file to parse for artist,
 * album title, year of publication and tracks title.
 *
 * @author Luciano Xumerle
 * @version 0.4.0
 */
public class CDDB {
    // Stores every line of the cddb file
    private String[] fileLines;

    // the original CDDB filename
    private String filename;

    // elements detected from the first line of CDDB file
    private String artist;
    private String album;
    private String year;

    // used to store track number, artist and song title of each track
    // from the CDDB file
    private String[] artists;
    private String[] songs;
    private int[] cd;

    // the first line of CDDB file is OK
    private boolean isArtistTitleOK;

    // this is increments by 100 when found a the "-- CD" string
    // track number is trackNum + boxNum
    // If no -- CD string is found, then we add 0 to the track number.
    // the constructor sets the value to boxNum/100 (the number of support in box).
    private int boxNum=0;

    // store the number of tracks detected in the CDDB file
    private int index;

    // if true then the tracks name have the artist field.
    private boolean _printArtist = true;


    /**
     * The class constructor
     *
     * @param file A CDDB filename
     * @param noArtist true or false
     */
    public CDDB ( String file, boolean noArtist )
    throws java.io.IOException {
        isArtistTitleOK = true;
        filename = file;

        _printArtist = !noArtist;

        fileLines = Str.readTXT ( file );

        artists = new String[fileLines.length];
        songs = new String[fileLines.length];
        cd = new int[fileLines.length];

        // track number start from 1
        int trackNum=1;
        boxNum=0;

        index=0;

        String line;
        for ( int row = 0; row < fileLines.length; row++ ) {
            line = fileLines[row].trim();
            if ( line.length() > 0 ) {
                if ( row==0 )
                    isArtistTitleOK = setArtistTitleYear ( line );
                else if ( line.indexOf ( "-- CD" ) > -1 ) {
                    boxNum+=100;
                    trackNum=1;
                } else {
                    if ( addSong ( line, index ) ) {
                        cd[index]= boxNum + trackNum;
                        trackNum++;
                        index++;
                    }
                }
            }
        }
        boxNum=boxNum/100;
    }


    /**
     * Returnsthe String object reporting the forst line and the tracks
     * strored in CDDB file.
     *
     * @return The string.
     */
    public String toString() {
        String pp="";
        String aa="";
        pp += " " + artist + " :: " + album + " (" + year + ")\n\n";

        int boxn=boxNum;

        for ( int i = 0; i < index; i++ ) {
            if ( boxNum>0 ) {
                pp += "\n -- CD" + boxn + ":\n";
                boxn--;
            }
            if ( cd[i] % 100 < 10 )
                pp += " ";
            if ( !artists[i].equals ( artist ) )
                aa=artists[i] + " :: ";
            pp += " " + ( cd[i] % 100 ) + ". " + aa + songs[i] + "\n";
        }
        return pp;
    }


    /**
     * Prints to STDOUT the cue file.
     *
     * @param waveFiles The sorted list of WAV files.
     */
    public void doCUE ( String[] waveFiles ) {
        if (  waveFiles.length != index  ) {
            System.err.println ( "ERROR: Number of files != Number of songs in txt file!" );
            return;
        }
        getCUEheader();
        for ( int i=0; i<index; i++ )
            System.out.println ( getCUEsong ( waveFiles[i], i ) );
    }


    /**
     * Returns true if CDDB file is well formed.
     *
     * @return true or false
     */
    public boolean isFileOK() {
        return isArtistTitleOK;
    }


    /**
     * Returns the artist name stored in first line of CDDB file.
     *
     * @return The artist name.
     */
    public String getArtist() {
        return artist;
    }


    /**
     * Returns the album title stored in first line of CDDB file.
     *
     * @return The album title.
     */
    public String getAlbum() {
        return album;
    }


    /**
     * Returns the year stored in first line of CDDB file.
     *
     * @return the year.
     */
    public String getYear() {
        return year;
    }


    /**
     * Returns the used CDDB filename.
     *
     * @return The filename.
     */
    public String getSourceFilename() {
        return filename;
    }


    /**
     * Returns the list of track names stored in the CDDB file.
     *
     * @return The track list array.
     */
    public String[] getDestination() {
        String[] ttt = new String[ index ];
        for ( int i = 0; i < index; i++ ) {
            StringBuilder temp = new StringBuilder();

            // print song number
            if ( boxNum>0 )
                temp.append ( cd[i] ).append ( "-" );
            else {
                int tt = cd[i] % 100;
                if ( tt < 10 )
                    temp.append ( 0 );
                temp.append ( tt ).append ( "-" );
            }

            // print artist
            if ( _printArtist )
                temp.append ( artists[i] ).append ( "-" );

            // print song title
            temp.append ( songs[i] );
            ttt[ i ] = temp.toString();
        }
        return ttt;
    }


    /**
     * Returns the number of songs stored in CDDB file.
     *
     * @return The number of songs.
     */
    public int getSongNumber() {
        return index;
    }


    /**
     * Rewrite the CDDB file with better identation.
     */
    public void saveCDDB()
    throws java.io.IOException {
        String old = "";
        Writer output = new BufferedWriter ( new FileWriter ( filename ) );
        for ( int i = 0; i < fileLines.length; i++ ) {
            String line = fileLines[i].trim();
            if ( line.length() > 0 || ( line.length() == 0 && old.length() > 0 ) ) {
                String space = " ";
                char ch[] = line.toCharArray();
                if ( line.length() > 1
                        && Character.isDigit ( ch[ 0 ] )
                        && ch[ 1 ] == '.'
                        && ch[ 2 ] == ' ' )
                    space += " ";
                output.write ( space + line + "\r\n" );
            }
            old = line;
        }
        output.close();
    }


    /**
     * Parses the first line of CDDB file to detect artist, albu and year.
     * Returns true if the line is correct.
     *
     * @param line The first line of the CDDB file.
     * @return true or false.
     */
    private boolean setArtistTitleYear ( String line ) {
        year="";
        artist="";
        album="";
        int idx=line.indexOf ( "::" );
        if ( idx>0 ) {
            artist=line.substring ( 0, idx ).trim();
            album=line.substring ( idx+2 ).trim();
        } else {
            idx=line.indexOf ( "/" );
            if ( idx>0 ) {
                artist=line.substring ( 0, idx ).trim();
                album=line.substring ( idx+1 ).trim();
            }
        }
        idx=album.lastIndexOf ( "(" );
        if (  idx>0 && album.length() >idx+5
                &&  Str.isInteger ( album.substring ( idx+1,idx+5 ) ) ) {
            year=album.substring ( idx+1,idx+5 );
            album=album.substring ( 0,idx-1 ).trim();
        }
        if ( artist.equals ( "" )   ) return false;
        return true;
    }


    /**
     * Used by the constructor to get a track from the CDDB line.
     * The method check if the row is a correct song name.
     *
     * @param line A single CDDB row.
     * @param i The index of the current song.
     * @return true or false.
     */
    private boolean addSong ( String line, int i ) {
        int dot = line.indexOf ( ". " );
        int dpdp = line.indexOf ( "::" );

        int comment=line.indexOf ( "##" );
        if (comment<0)
            comment=line.length();

        if ( dot > -1 && Str.isInteger ( line.substring ( 0, dot ) ) ) {
            if ( dpdp>0 ) {
                artists[i]=line.substring ( dot+1,dpdp ).trim();
                songs[i]=line.substring ( dpdp+2, comment ).trim();
            } else {
                artists[i]=artist;
                songs[i]=line.substring ( dot+1, comment ).trim();
            }
            return true;
        }
        return false;
    }


    /**
     * Returns the header with album artist and title used in a CUE file.
     *
     * @return The CUE header.
     */
    public String getCUEheader () {
        return "PERFORMER \"" + artist + "\"\nTITLE \"" + album + "\"";
    }


    /**
     * Returns the row used to define a CUE track in the CUE file.
     *
     * @param wav The WAV name
     * @param trackNum The numbeer of the track.
     * @return The CUE string to define the track.
     */
    public String getCUEsong ( String wav, int trackNum ) {
        int ss=trackNum+1;
        StringBuilder out=new StringBuilder();
        out.append("FILE \"").append(wav).append("\" WAVE\n");
        out.append ( "  TRACK " );
        if ( ss<10 ) out.append ( "0" );
        out.append ( ss );
        out.append ( " AUDIO\n" );
        out.append ( "    PERFORMER \"" );
        out.append ( artists[trackNum] );
        out.append ( "\"\n    TITLE \"" );
        out.append ( songs[trackNum] );
        out.append ( "\"\n" );
        out.append ( "    INDEX 01 00:00:00" );
        return out.toString();
    }

} // CLOSE CLASS
