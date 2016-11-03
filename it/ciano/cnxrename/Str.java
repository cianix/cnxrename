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
     * Replaces not ASCII dashes and quotes with "-" and "'".
     *
     * @param s The input String.
     * @param ns If true then multiple spaces are replaces with a single space.
     * @return The cleaned String.
     */
    public static String cleanString( String s, boolean ns ) {
        char[] ch = s.toCharArray();
        for ( int i=0; i<ch.length; i++ )
            switch ( ch[i] ) {
            case '–':
                ch[i]='-';
                break;

            case '`':
            case '´':
            case '‘':
            case '’':
                ch[i]='\'';
                break;
            }

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
        int id1 = str.indexOf("-");
        int id2=-1;
        if ( id1>-1 ) {
            try {
                id2 = Integer.parseInt ( str.substring(id1+1) ) - 1;
                id1 = Integer.parseInt ( str.substring(0,id1) ) - 1;
            } catch( NumberFormatException e ) {
                return str;
            }
        }

        // SWAP POSITION
        String temp[] = str.split( "-" );
        if ( temp.length > id1 && temp.length > id2 ) {
            String a = temp[ id1 ];
            temp[ id2 ] = temp[ id1 ];
            temp[ id1 ] = a;

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
        char[] ch = strn.toLowerCase().toCharArray();
        ch[0]=Character.toUpperCase ( ch[0] );
        for ( int i = 1; i < ch.length-1; ++i ) {
            if ( Character.isWhitespace(ch[i])
                    || ch[i] == '_'
                    || ch[i] == '-'
                    || (ch[i]>='0' && ch[i]<='9')
                    || ch[i] == '.'
                    || ch[i] == '\'')
                ch[i+1]=Character.toUpperCase ( ch[i+1] );
        }
        return new String(ch);
    }


    /**
     * Returns the string with only ASCII characters and no spaces.
     *
     * @param strn The input string.
     * @return The managed string.
     */
    final public static String toMp3 ( String strn ) {
        StringBuilder sb = new StringBuilder();

        char[] ch = strn.toLowerCase().toCharArray();

        for ( int i=0; i<ch.length; i++ ) {
            switch ( ch[i] ) {
            case '[':
            case ']':
            case '(':
            case ')':
            case '{':
            case '}':
            case '<':
            case '>':
            case '«':
            case '»':
            case ':':
            case ';':
            case '&':
            case '/':
            case '\\':
            case '+':
                sb.append('-');
                break;

            case ' ':
            case ',':
            case '.':
            case '!':
            case '?':
            case '¿':
            case '°':
            case '#':
            case '@':
            case '"':
            case '“':
            case '*':
            case '%':
            case '~':
            case '¡':
            case '·':
            case '©':
            case '®':
                sb.append('_');
                break;

            case 'è':
            case 'é':
            case 'ê':
            case 'ë':
            case 'э':
                sb.append('e');
                break;

            case 'ó':
            case 'ò':
            case 'ö':
            case 'ô':
            case 'õ':
            case 'ø':
                sb.append('o');
                break;

            case 'ì':
            case 'í':
            case 'î':
            case 'ï':
            case 'и':
                sb.append('i');
                break;

            case 'ù':
            case 'ú':
            case 'û':
            case 'ü':
                sb.append('u');
                break;

            case 'à':
            case 'á':
            case 'â':
            case 'ä':
            case 'å':
            case 'ã':
                sb.append('a');
                break;

            case 'б':
                sb.append('b');
                break;

            case 'ð':
            case 'д':
                sb.append('d');
                break;

            case 'æ':
                sb.append("ae");
                break;

            case 'Þ':
            case 'т':
                sb.append('t');
                break;

            case 'м':
                sb.append('m');
                break;

            case 'ñ':
            case 'н':
                sb.append('n');
                break;

            case 'ß':
                sb.append("ss");
                break;

            case 'ý':
            case 'ÿ':
                sb.append('y');
                break;

            case 'ç':
                sb.append('c');
                break;

            case '×':
                sb.append('x');
                break;

            case 'я':
                sb.append("ya");
                break;

            case 'ф':
                sb.append('f');
                break;

            case 'г':
                sb.append('g');
                break;

            case 'л':
                sb.append('l');
                break;

            default:
                sb.append(ch[i]);
                break;
            }
        }

        String res = capitalize( sb.toString() );

        if (res.indexOf("Cd")>-1) res=res.replaceAll( "(?i)cd_*", "CD"  );
        if (res.indexOf("Lp")>-1) res=res.replaceAll( "(?i)lp_*", "LP"  );
        if (res.indexOf("Ep")>-1) res=res.replaceAll( "(?i)ep_*", "EP"  );
        if (res.indexOf("Divx")>-1) res=res.replaceAll( "(?i)divx", "DivX"  );
        if (res.indexOf("Ost")>-1) res=res.replaceAll( "(?i)ost", "OST"  );
        if (res.indexOf("Aa_Vv")>-1) res=res.replaceAll( "(?i)aa_vv", "AA.VV."  );

        return cleanString(res, true).trim();
    }

} // END CLASS
