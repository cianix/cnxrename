/* file name  : Str.java
 * authors    : Luciano Xumerle
 * created    : dom 15 nov 2015 00:56:13 CET
 * copyright  : GPL3
 *
 */

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
 * @version 0.2.0
 */
public class Str {

    /**
     * Gets a string from STDIN.
     *
     * @return The inserted string.
     */
    public static String getInput() {
        try {
            BufferedReader in = new BufferedReader ( new InputStreamReader ( System.in ) );
            return in.readLine().trim() ;
        } catch ( IOException e ) {
            return "";
        }
    }


    /**
     * Returns the String[] {filename_without_extension, extension}.
     *
     * @param filename The filename.
     * @return The String[].
     */
    final public static String[] getFilenameExtension (String filename) {
        int pos = filename.lastIndexOf ('.');
        if (pos < 0)
            return new String[] {
            filename, ""
        };
        char tt[] = filename.substring (pos).toCharArray ();

        // extension length > 1 and < 7 ( . class == 6 )
        if (tt.length > 1 && tt.length < 6 && tt[0] == '.')
            for (int i = 1; i < tt.length; i++)
                if (!Character.isLetterOrDigit (tt[i]))
                    return new String[] {
                    filename, ""
                };

        if (pos - 4 > 0 && filename.substring (pos - 4, pos).equals (".tar"))
            pos = pos - 4;

        return new String[] {
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
    public static String[] split( String string, char separator ) {
        int size=1;
        for (int i=0; i<string.length(); i++)
            if (string.charAt(i) == separator)
                size++;

        String[] res=new String[size];
        int index=0;

        StringBuilder s = new StringBuilder();
        for (int i=0; i<string.length(); i++) {
            char t=string.charAt(i);
            if ( t == separator ) {
                res[index] = s.toString();
                index++;
                s = new StringBuilder();
            } else {
                s.append(t);
            }
        }
        res[index]= s.toString();
        return res;
    }


    /**
     * Replaces not ASCII dashes and quotes with "-" and "'".
     *
     * @param s The input String.
     * @param ns If true then multiple spaces are replaces with a single space.
     * @return The cleaned String.
     */
    public static String cleanString( String s, boolean ns ) {
        char[] ch = s.toCharArray();
        for ( int i=0; i<ch.length; i++ )
            if ( ch[i]=='–' )
                ch[i]='-';
            else if( ch[i]=='`' || ch[i]=='´' || ch[i]=='‘' || ch[i]=='’' )
                ch[i]='\'';

        if (ns) {
            boolean dash=false;
            boolean space=false;
            StringBuilder buf = new StringBuilder();

            for ( int i = 0; i < ch.length; ++i ) {
                if ( Character.isWhitespace(ch[i]) || ch[i] == '_' ) {
                    ch[i]=' ';
                    space=true;
                } else if ( ch[i]=='-' ) {
                    dash=true;
                    space=false;
                } else {
                    if ( dash )
                        buf.append ( '-' );
                    else if ( space )
                        buf.append ( ' ' );
                    dash=false;
                    space=false;
                    buf.append ( ch[i] );
                }
            }
            return buf.toString();
        }

        return new String(ch);
    }


    /**
     * Removes apostrophe chars from the destination filename.
     */
    public static String deleteApostrophe( String a ) {
        StringBuilder buf = new StringBuilder();
        char[] ch = a.toCharArray ();
        for ( int i = 0; i < ch.length; ++i ) {
            if ( '\'' == ch[i] || ch[i] == '`' || ch[i] == '’' )
                continue;
            buf.append ( ch[i] );
        }
        return buf.toString();
    }


    /**
     * Adds an underscore before uppercase characters.
     * FooBar will be Foo_Bar
     */
    public static String destUnderscore ( String a ) {
        StringBuilder buf = new StringBuilder();
        char[] ch = a.toCharArray ();
        for ( int i = 0; i < ch.length; ++i ) {
            if ( Character.isUpperCase ( ch[ i ] ) )
                buf.append ( '_' );
            buf.append ( ch[ i ] );
        }
        return buf.toString ();
    }


    /**
     * Replaces char at position in string with string.
     *
     * @param s The input string.
     * @param replace String to be inserted.
     * @param pos Position of the char to be replaced.
     * @return The new string.
     */
    public static String replaceCharAtPosWithString ( String s, String replace, int pos ) {
        StringBuilder buf = new StringBuilder ();
        char[] ch1 = s.toCharArray ();
        for ( int i = 0; i < ch1.length; ++i ) {
            if ( i == pos )
                buf.append ( replace );
            else
                buf.append ( ch1[ i ] );
        }
        return buf.toString();
    }


    /**
     * Converts camel-case_versus_c to case_versus_c-camel.
     *
     * @param a The String to change.
     * @param pos A string "num1-num2".
     */
    public static String destSwapPos ( String str, String pos ) {
        int[] ps = new int[ 2 ];
        // FIND POSITIONS
        String swap[] = Str.split ( pos, '-' );
        if ( swap.length == 2 ) {
            try {
                ps[ 0 ] = Integer.parseInt ( swap[ 0 ] ) - 1;
                ps[ 1 ] = Integer.parseInt ( swap[ 1 ] ) - 1;
            } catch( NumberFormatException e ) {
                return str;
            }
        }

        // SWAP POSITION
        String temp[] = Str.split( str, '-' );
        if ( temp.length > ps[ 0 ] && temp.length > ps[ 1 ] ) {
            String a = temp[ ps[ 1 ] ];
            temp[ ps[ 1 ] ] = temp[ ps[ 0 ] ];
            temp[ ps[ 0 ] ] = a;

            a=temp[0];
            for (int i=1; i<temp.length; i++  )
                a = a + '-' + temp[i];
            return a;
        }
        return str;
    }


    /**
     * Returns true if the given string is an integer.
     *
     * @param s The input string.
     * @return true or false.
     */
    final public static boolean isInteger ( String s ) {
        char[] a = s.trim().toCharArray();
        for ( int i=0; i<a.length; i++ )
            if ( a[i]<'0' || a[i]>'9' )
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
    public static File[] find (  File path, boolean rec, boolean onlyFile ) {
        ArrayList <File> a = new ArrayList <File> ();
        Stack<File> stack = new Stack<File>();

        for(File f : path.listFiles()) stack.push(f);

        while(!stack.isEmpty()) {
            File child = stack.pop();
            if (rec && child.isDirectory()) {
                for(File f : child.listFiles()) stack.push(f);
                if ( !onlyFile )  a.add(child);
            } else if (child.isFile()) {
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
    final public static String[] readTXT ( String filename ) {
        ArrayList <String> lines = new ArrayList <String> ();
        try {
            String line;
            BufferedReader br = new BufferedReader ( new FileReader ( filename ) );
            while ( null != ( line = br.readLine() ) )
                lines.add ( line.trim() );
            br.close();
        } catch ( IOException e ) {
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
    public static String capitalize ( String strn ) {
        StringBuilder buf = new StringBuilder();
        char[] ch = strn.toLowerCase().toCharArray();
        buf.append ( Character.toUpperCase ( ch[ 0 ] ) );
        for ( int i = 1; i < ch.length; ++i ) {
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
    final public static String toMp3 ( String strn ) {
        StringBuilder sb = new StringBuilder();

        final String dash = "[]{}():;&/\\-+";
        final String space = " _,.!?°#@~\"*%";

        StringTokenizer tok = new StringTokenizer(strn, dash);

        while (tok.hasMoreTokens()) {
            StringTokenizer part = new StringTokenizer( tok.nextToken(), space );
            int j=0;
            String t="";
            while ( part.hasMoreTokens() ) {
                if (j > 0)
                    sb.append('_');
                String nn=part.nextToken();

                if(nn.matches("(?i)^[ivx]+$"))
                    sb.append( nn.toUpperCase() );
                else {
                    for (int ii = 0; ii < nn.length(); ii++) {
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
    final private static boolean isPreUpperChar( char a ) {
        return ( Character.isWhitespace(a)
                 || a == '-'
                 || a == '.'
                 || (a>='0' && a<='9')
                 || a == '_'
                 || a == '\'');
    }

} // END CLASS
