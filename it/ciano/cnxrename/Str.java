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


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Stack;


/**
 * The class is a collection of static methods to manage
 * strings.
 *
 * @author LucianoXumerle
 * @version 0.10
 */
public class Str
{

    /**
     * Gets a string from STDIN.
     *
     * @return The inserted string.
     */
    public static String getInput()
    {
        try
        {
            BufferedReader in = new BufferedReader ( new InputStreamReader ( System.in ) );
            return in.readLine().trim() ;
        }
        catch ( IOException e )
        {
            return "";
        }
    }


    /**
     * Returns the String[] {filename_without_extension, extension}.
     *
     * @param filename The filename.
     * @return The String[].
     */
    final public static String[] getFilenameExtension (String filename)
    {
        int pos = filename.lastIndexOf ('.');
        if (pos < 0)
            return new String[]
        {
            filename, ""
        };
        char tt[] = filename.substring (pos).toCharArray ();

        // extension length > 1 and < 7 ( . class == 6 )
        if (tt.length > 1 && tt.length < 6 && tt[0] == '.')
            for (int i = 1; i < tt.length; i++)
                if (!Character.isLetterOrDigit (tt[i]))
                    return new String[]
                {
                    filename, ""
                };

        if (pos - 4 > 0 && filename.substring (pos - 4, pos).equals (".tar"))
            pos = pos - 4;

        return new String[]
        {
            filename.substring (0, pos), filename.substring (pos).toLowerCase ()
        };
    }


    /**
     * A more efficient split function.
     *
     * @param string Input String.
     * @param separator The separator.
     * @return The output splitted String array.
     */
    public static String[] split( String string, char separator )
    {
        int size=1;
        for (int i=0; i<string.length(); i++)
            if (string.charAt(i) == separator)
                size++;

        String[] res=new String[size];
        int index=0;

        StringBuilder s = new StringBuilder();
        for (int i=0; i<string.length(); i++)
        {
            char t=string.charAt(i);
            if ( t == separator )
            {
                res[index] = s.toString();
                index++;
                s = new StringBuilder();
            }
            else
            {
                s.append(t);
            }
        }
        res[index]= s.toString();
        return res;
    }


    /**
     * Replaces char at position in string with string.
     *
     * @param s The input string.
     * @param replace String to be inserted.
     * @param pos Position of the char to be replaced.
     * @return The new string.
     */
    public static String replaceCharAtPosWithString ( String s, String replace, int pos )
    {
        StringBuilder buf = new StringBuilder ();
        char[] ch1 = s.toCharArray ();
        for ( int i = 0; i < ch1.length; ++i )
        {
            if ( i == pos )
                buf.append ( replace );
            else
                buf.append ( ch1[ i ] );
        }
        return buf.toString();
    }


    /**
     * Returns true if the given string is an integer.
     *
     * @param s The input string.
     * @return true or false.
     */
    final public static boolean isInteger ( String s )
    {
        char[] a = s.trim().toCharArray();
        for ( int i=0; i<a.length; i++)
            if ( a[i]<'0' && a[i]>'9' )
                return false;
        return true;
    }


    /**
     * Parses a directory tree.
     *
     * @param path Startpath to be parsed.
     * @param rec If true, recursive.
     * @param onlyFile If true, reports only file.
     * @return The File list.
     */
    public static File[] find (  File path, boolean rec, boolean onlyFile )
    {
        ArrayList <File> a = new ArrayList <File> ();
        Stack<File> stack = new Stack<File>();

        for(File f : path.listFiles()) stack.push(f);

        while(!stack.isEmpty())
        {
            File child = stack.pop();
            if (rec && child.isDirectory())
            {
                for(File f : child.listFiles()) stack.push(f);
                if ( !onlyFile )  a.add(child);
            }
            else if (child.isFile())
            {
                a.add(child);
            }
        }

        File[] res=a.toArray(new File[a.size()]);
        Arrays.sort(res);
        return res;
    }


    /**
     * Reads a txt file and returns a String[] array with elements is rows.
     *
     * @param filename The name of file.
     * @return The rows of file.
     */
    final public static String[] readTXT ( String filename )
    {
        ArrayList <String> lines = new ArrayList <String> ();
        try
        {
            String line;
            BufferedReader br = new BufferedReader ( new FileReader ( filename ) );
            while ( null != ( line = br.readLine() ) )
                lines.add ( line.trim() );
            br.close();
        }
        catch ( IOException e )
        {
            return new String[0];
        }
        return lines.toArray ( new String[lines.size()] );
    }


    /**
     * The method capitalize the input string.
     * Example: "foo bar" becomes "Foo Bar"
     *
     * @param strn input String.
     * @return Capitalized String.
     */
    public static String capitalize ( String strn )
    {
        StringBuilder buf = new StringBuilder();
        char[] ch = strn.toLowerCase().toCharArray();
        buf.append ( Character.toUpperCase ( ch[ 0 ] ) );
        for ( int i = 1; i < ch.length; ++i )
        {
            if ( isPreUpperChar(ch[i-1]) )
                buf.append ( Character.toUpperCase ( ch[i] ) );
            else
                buf.append ( ch[i] );
        }
        return buf.toString();
    }


    /**
     * Returns the string with only ASCII characters and no spaces.
     *
     * @param strn The input string.
     * @return The managed string.
     */
    final public static String toMp3 ( String strn )
    {
        StringBuilder sb = new StringBuilder();

        final String dash = "[]{}():;&/\\-+";
        final String space = " _,.!?°#@~\"*%";

        StringTokenizer tok = new StringTokenizer(strn, dash);

        while (tok.hasMoreTokens())
        {
            StringTokenizer part = new StringTokenizer( tok.nextToken(), space );
            int j=0;
            String t="";
            while ( part.hasMoreTokens() )
            {
                if (j > 0)
                    sb.append('_');
                String nn=part.nextToken();

                if(nn.matches("(?i)^[ivx]+$"))
                    sb.append( nn.toUpperCase() );
                else
                {
                    for (int ii = 0; ii < nn.length(); ii++)
                    {
                        char s=Character.toLowerCase(nn.charAt(ii));
                        if ( s == 'à'
                                || s == 'á'
                                || s == 'ä'
                                || s == 'â')
                            t="a";
                        else if ( s == 'è'
                                  || s == 'é'
                                  || s == 'ê'
                                  || s == 'ë')
                            t="e";
                        else if ( s == 'ì'
                                  || s == 'í'
                                  || s == 'î'
                                  || s == 'ï')
                            t="i";
                        else if ( s == 'ó'
                                  || s == 'ò'
                                  || s == 'ö'
                                  || s == 'ô')
                            t="o";
                        else if ( s == 'ù'
                                  || s == 'ú'
                                  || s == 'û'
                                  || s == 'ü')
                            t="u";
                        else if ( s == 'þ' || s == 'Þ')
                            t="th";
                        else if ( s == 'æ')
                            t="ae";
                        else if ( s == 'ß')
                            t="ss";
                        else if ( s == 'ý')
                            t="y";
                        else if ( s == 'ð')
                            t="d";
                        else if ( s == 'ç')
                            t="c";
                        else if ( s == '`' || s == '’')
                            t="'";
                        else
                            t=Character.toString(s);

                        // DO CAPITALIZE
                        if ( ii==0 ||  isPreUpperChar( nn.charAt(ii-1 ) ))
                            sb.append(t.toUpperCase());
                        else
                            sb.append(t);
                    }
                }
                j++;
            }
            if ( sb.charAt( sb.length()-1 ) != '-' && tok.hasMoreTokens() )
                sb.append('-');
        }

        String res= sb.toString();
        if (res.indexOf("Cd")>-1) res=res.replaceAll( "(?i)cd_*", "CD"  );
        if (res.indexOf("Lp")>-1) res=res.replaceAll( "(?i)lp_*", "LP"  );
        if (res.indexOf("Ep")>-1) res=res.replaceAll( "(?i)ep_*", "EP"  );
        if (res.indexOf("Divx")>-1) res=res.replaceAll( "(?i)divx", "DivX"  );
        if (res.indexOf("Ost")>-1) res=res.replaceAll( "(?i)ost", "OST"  );

        return res;
    }


    /**
     * The program uppercases a char if this check returns true.
     *
     * @param a The Char.
     * @return true or false.
     */
    final private static boolean isPreUpperChar( char a )
    {
        return ( Character.isWhitespace(a)
                 || a == '-'
                 || a == '.'
                 || (a>='0' && a<='9')
                 || a == '_'
                 || a == '\'');
    }

} // END CLASS
