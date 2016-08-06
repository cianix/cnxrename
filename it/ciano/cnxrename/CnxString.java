/**
 * file name  : it/ciano/cnxrename/CnxString.java
 * authors    : Luciano Xumerle
 * created    : mar 15 apr 2014 11:43:03 CEST
 * copyright  : GPL3
 *
 */

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
 * @version 0.6.0
 */
public class CnxString extends File {

    // destination name and file extension
    private String dest;
    private String ext;

    private boolean _caseInsensitive = false;
    private boolean _global = false;
    private boolean _renameDir = false;


    /**
     * Class constructor.
     *
     * @param ff The file to be renamed.
     */
    public CnxString ( File ff )
    throws java.io.IOException {
        this ( ff, "" );
    }


    /**
     * Class constructor.
     *
     * @param ff The file to be renamed.
     * @param destination New filename.
     */
    public CnxString ( File ff, String destination )
    throws java.io.IOException {
        super( ff.getCanonicalPath() );
        String[] tt=Str.getFilenameExtension( getName() );
        ext = tt[1].toLowerCase();

        if ( destination.equals ( "" ) )
            setDest(tt[0]);
        else
            setDest(destination);
    }


    /**
     * Overrides toString method.
     *
     * @return
     */
    public String toString () {
        StringBuilder res = new StringBuilder();
        res.append ( getName() );
        res.append ( " --> " );
        res.append ( dest );
        res.append ( ext );
        return res.toString();
    }


    /**
     * Renames the file when destination is set.
     */
    public boolean destRename() {
        if ( _renameDir == false && isDirectory() )
            return false;
        StringBuilder res = new StringBuilder();
        res.append ( getParent() ).append( File.separator ).append ( dest ).append ( ext );
        File temp = new File ( res.toString() );
        if ( ! temp.exists() )
            return renameTo ( temp );
        return false;
    }


    /**
     * Returns the set destonation filename.
     *
     * @return The destination.
     */
    public String getDest() {
        StringBuilder res = new StringBuilder();
        res.append ( dest ).append ( ext );
        return res.toString();
    }


    /**
     * Match in string replacement is set to true.
     */
    public void setCaseInsensitive() {
        _caseInsensitive = true;
    }


    /**
     * Replacing of a substring is set to global.
     */
    public void setGlobalReplace() {
        _global = true;
    }


    /**
     * If set also the directory will be renamed.
     */
    public void setRenameDir() {
        _renameDir = true;
    }


    /**
     * Replaces string.
     * Options for global replace or case sensitive have to be set before.
     *
     * @param regex The regex to be replaced.
     * @param repl
     */
    public void replaceDest ( String regex, String repl ) {
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
    public boolean srcISdest () {
        StringBuilder res = new StringBuilder();
        res.append ( dest ).append ( ext );
        return getName().equals( res.toString() );
    }


    /**
     * Set the destination with string.
     *
     * @param tt The destination filename.
     */
    public void setDest ( String tt ) {
        dest = Str.cleanString(tt, false);
    }


    /**
     * Adds an underscore before uppercase characters.
     * FooBar will be Foo_Bar
     */
    public void destUnderscore ( ) {
        dest=Str.destUnderscore(dest);
    }


    /**
     * Replaces filename extension.
     *
     * @param newExt New extension.
     */
    public void destNewExtension ( String newExt ) {
        ext = newExt.toLowerCase();
    }


    /**
     * Converts camel-case_versus_c to case_versus_c-camel.
     *
     * @param pos
     */
    public void destSwapPos ( String pos ) {
        dest=Str.destSwapPos( dest, pos );
    }


    /**
     * Destination filename will be String-##.ext
     *
     * @param tt The String.
     * @param pos The number ##.
     */
    public void getSequenceName ( String tt, int pos ) {
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
    public void destReplaceChar ( String param ) {
        String swap[] = Str.split( param, '-');
        dest = Str.replaceCharAtPosWithString ( dest, swap[1], Integer.parseInt ( swap[ 0 ] ) - 1 );
    }


    /**
     * Returns the file extension.
     *
     * @return the file extension.
     */
    public String getFileExt( ) {
        return ext;
    }

} // CLOSE CLASS
