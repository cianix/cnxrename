/**

Copyright 2014-2015 Luciano Xumerle

This file is part of cnxrename.

cnxrename is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published
by the Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

cnxrename is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with cnxrename. If not, see http://www.gnu.org/licenses/.

**/

package it.ciano.cnxrename;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.Arrays;

/**
 * Class to manage filename renaming.
 *
 * @author Luciano Xumerle
 * @version 0.5.0
 */
public class CnxString
{
    private String src;
    private String dest;
    private String ext;
    private String path;
    private File file;

    private boolean _caseInsensitive = false;
    private boolean _global = false;


    /**
     * Class constructor.
     *
     * @param ff The file to be renamed.
     */
    public CnxString ( File ff )
    throws java.io.IOException
    {
        this ( ff, "" );
    }


    /**
     * Class constructor.
     *
     * @param ff The file to be renamed.
     * @param destination New filename.
     */
    public CnxString ( File ff, String destination )
    throws java.io.IOException
    {
        file = new File ( ff.getCanonicalPath() );
        path = file.getParent() + File.separator;
        src = file.getName();
        String[] tt=Str.getFilenameExtension(src);
        ext = tt[1].toLowerCase();
        if ( destination.equals ( "" ) )
            dest = tt[0];
        else
            dest = destination;
    }


    /**
     * Overrides toString method.
     *
     * @return
     */
    public String toString ()
    {
        StringBuilder res = new StringBuilder();
        res.append ( src );
        res.append ( " --> " );
        res.append ( dest );
        res.append ( ext );
        return res.toString();
    }


    //
    // return true if source File is a directory
    //

    /**
     * Returns true if source File is a directory.
     *
     * @return true or false.
     */
    public boolean isDir()
    {
        return file.isDirectory();
    }


    /**
     * Renames the file when destination is set.
     */
    public void destRename()
    {
        StringBuilder res = new StringBuilder();
        res.append ( path ).append ( dest ).append ( ext );
        File temp = new File ( res.toString() );
        if ( ! temp.exists() )
            file.renameTo ( temp );
    }


    /**
     * Returns the set destonation filename.
     *
     * @return The destination.
     */
    public String getDest()
    {
        StringBuilder res = new StringBuilder();
        res.append ( dest ).append ( ext );
        return res.toString();
    }


    /**
     * Returns original filename.
     *
     * @return The sourc filename.
     */
    public String getSrc ()
    {
        return src;
    }


    /**
     * Match in string replacement is set to true.
     */
    public void setCaseInsensitive()
    {
        _caseInsensitive = true;
    }


    /**
     * Replacing of a substring is set to global.
     */
    public void setGlobalReplace()
    {
        _global = true;
    }


    /**
     * Replaces string.
     * Options for global replace or case sensitive have to be set before.
     *
     * @param regex The regex to be replaced.
     * @param repl
     */
    public void replaceDest ( String regex, String repl )
    {
        Pattern p = null;
        if ( _caseInsensitive )
            p = Pattern.compile ( regex, Pattern.CASE_INSENSITIVE );
        else
            p = Pattern.compile ( regex );

        Matcher m = p.matcher ( dest );
        if ( _global )
            dest = m.replaceAll ( repl );
        else
            dest = m.replaceFirst ( repl );
    }


    /**
     * Returns true is new filename is not changed.
     *
     * @return true or false.
     */
    public boolean srcISdest ()
    {
        StringBuilder res = new StringBuilder();
        res.append ( dest ).append ( ext );
        if ( src.equals ( res.toString() ) )
            return true;
        return false;
    }


    /**
     * Set the destination with string.
     *
     * @param tt The destination filename.
     */
    public void setDest ( String tt )
    {
        dest = tt;
    }


    /**
     * Destination is set as uppercase for all characters.
     */
    public void destUpperCase()
    {
        dest = dest.toUpperCase();
    }


    /**
     * Destination is set as lowercase for all characters.
     */
    public void destLowerCase ()
    {
        dest = dest.toLowerCase();
    }


    /**
     * Removes apostrophe from destination filename.
     */
    public void deleteApostrophe()
    {
        StringBuilder buf = new StringBuilder();
        char[] ch = dest.toCharArray ();
        for ( int i = 0; i < ch.length; ++i )
        {
            if ( '\'' == ch[i] )
                continue;
            buf.append ( ch[i] );
        }
        dest = buf.toString();
    }


    /**
     * Adds an underscore before uppercase characters.
     * FooBar will be Foo_Bar
     */
    public void destUnderscore ( )
    {
        StringBuilder buf = new StringBuilder();
        char[] ch = dest.toCharArray ();
        for ( int i = 0; i < ch.length; ++i )
        {
            if ( Character.isUpperCase ( ch[ i ] ) )
                buf.append ( '_' );
            buf.append ( ch[ i ] );
        }
        dest=buf.toString ();
    }


    /**
     * Destination is capitalized.
     * Converts camel_case_versus_c to Camel_Case_Versus_C
     */
    public void destCapitalize ()
    {
        dest = Str.capitalize ( dest );
    }


    /**
     * Converts camel_case_versus_è to Camel_Case_Versus_E
     */
    public void destMp3o1 ()
    {
        dest=Str.toMp3(dest);
    }


    /**
     * Converts camel-case_versus_c to Camel-Case Versus C.
     */
    public void destMp3o2 ()
    {
        dest = Str.toMp3 ( dest ).replace ( "_", " " );
    }


    /**
     * Converts camel-case_versus_c to Camel - Case Versus C.
     */
    public void destMp3o3 ()
    {
        dest = Str.toMp3 ( dest ).replace ( "_", " " ).replace ( "-", " - " );
    }


    /**
     * Converts camel-case_versus_c to Camel-CaseVersusC.
     */
    public void destMp3o4 ()
    {
        dest =  Str.toMp3( dest ).replaceAll ( "[_ ]+", "" );
    }


    /**
     * Works like dummy, but replacing multiple '_' and ' ' with space.
     */
    public void destMp3o5 ()
    {
        dest =  dest.replace("-"," - ").replaceAll ( "[_\\s]+", " " );
    }


    /**
     * Replaces filename extension.
     *
     * @param newExt New extension.
     */
    public void destNewExtension ( String newExt )
    {
        ext = newExt.toLowerCase();
    }


    /**
     * Converts camel-case_versus_c to case_versus_c-camel.
     *
     * @param pos
     */
    public void destSwapPos ( String pos )
    throws NumberFormatException
    {
        int[] ps = new int[ 2 ];
        // FIND POSITIONS
        String swap[] = Str.split ( pos, '-' );
        if ( swap.length == 2 )
        {
            ps[ 0 ] = Integer.parseInt ( swap[ 0 ] ) - 1;
            ps[ 1 ] = Integer.parseInt ( swap[ 1 ] ) - 1;
        }
        // SWAP POSITION
        String temp[] = Str.split( dest, '-' );
        if ( temp.length > ps[ 0 ] && temp.length > ps[ 1 ] )
        {
            String a = temp[ ps[ 1 ] ];
            temp[ ps[ 1 ] ] = temp[ ps[ 0 ] ];
            temp[ ps[ 0 ] ] = a;

            dest=temp[0];
            for (int i=1; i<temp.length; i++  )
                dest = dest + '-' + temp[i];
        }
    }


    /**
     * Destination filename will be String-##.ext
     *
     * @param tt The String.
     * @param pos The number ##.
     */
    public void getSequenceName ( String tt, int pos )
    {
        String pp = "";
        if ( pos > 9 )
            pp += pos;
        else
            pp += "0" + pos;
        dest = tt.replace ( "##", pp );
    }


    /**
     * Replaces character at position N with string.
     *
     * @param param Param is a string "IntegerPosition-String".
     */
    public void destReplaceChar ( String param )
    {
        String swap[] = Str.split( param, '-');
        dest = Str.replaceCharAtPosWithString ( dest, swap[1], Integer.parseInt ( swap[ 0 ] ) - 1 );
    }


    /**
     * Adds a prefix tothe  filename.
     *
     * @param prefix A string.
     */
    public void addPrefix ( String prefix )
    {
        StringBuilder res = new StringBuilder();
        res.append ( prefix );
        res.append ( dest );
        dest = res.toString();
    }


    /**
     * Adds a suffix tothe  filename.
     *
     * @param suffix A string.
     */
    public void addSuffix ( String suffix )
    {
        StringBuilder res = new StringBuilder();
        res.append ( dest );
        res.append ( suffix );
        dest = res.toString();
    }


    /**
     * Returns the file extension.
     *
     * @return the file extension.
     */
    public String getFileExt( )
    {
        return ext;
    }

} // CLOSE CLASS
