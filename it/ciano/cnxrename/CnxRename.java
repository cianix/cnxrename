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

*/

package it.ciano.cnxrename;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.IOException;

/**
 * Main class to rename files.
 *
 * @author Luciano Xumerle
 * @version 0.5.0
 */
public class CnxRename
{

    /**
     * Informations used to print copyright string.
     */
    final private static String version = "4.0.0b3";
    final private static String date = "Aug 25, 2015";
    final private static String copyright = "2015";
    final private static String author = "Luciano Xumerle <luciano.xumerle@gmail.com>";
    final private static String name = "CNXRENAME";


    /**
     * Regex to match multimedia file extension.
     */
    final static private String multimedia = "\\.(mp3|wma|ogg|wav|mp2|flac|ape|m4a|mp4)$";

    /**
     * Stores the source file list .
     */
    private static File[] srcFiles;

    /**
     * Strores file to be renamed and the destination name.
     */
    private static ArrayList <CnxString> renameFiles;


    private static String question =
        "\n== print [yes] to rename files or [help] to show help ==> ";
    private static String noFileRename =
        "\nNO FILEs RENAMED.\n\n";


    /**
     * Main program.
     *
     * @param String input[]
     */
    public static void main ( String input[] )
    throws java.io.IOException
    {
        CommandLine par = myPar ( input );
        if ( par != null )
        {
            // CnxString objects stores filenames to be renamed
            // and destination filename
            renameFiles = new ArrayList <CnxString> ();

            // set quiet output
            if ( !par.isSet ( "q" ) )
            {
                par.doMsg();
                if ( par.isSet ( "v" ) )
                    System.out.println ( par.toString() );
            }

            // check for help options
            if ( par.isSet ( "h" ) || par.isSet ( "help" ) )
                par.doHelp( );
            else
            {
                // when doTheWork == true the program can rename the files
                boolean doTheWork=false;
                if ( par.isSet ( "m3u" ) )
                {
                    String ff=par.getParValue ( "m3u" );
                    if( ff.indexOf ( ".m3u" ) > 0 )
                        doTheWork=doM3Uopt ( ff );
                }
                else
                {
                    // now get tke list of file
                    doFindFile ( par );

                    if ( par.isSet ( "txt" ) )
                        doTheWork=doTXTopt(par.getParValue( "txt" ), par.isSet( "na" ) );
                    if ( par.isSet ( "cue" ) )
                        doTheWork=doCUE( par.getParValue( "cue" ) );
                    else
                        doTheWork=true;

                    // CHECK VERBOSITY
                    if ( par.isSet ( "v" )  && ! par.isSet ( "q" ) )
                        System.out.println ( "List of files selected to rename\n"
                                             + srcFiles.toString() );
                    // DO RENAMING
                }
                if ( doTheWork ) doAllWork( par );
            }
        }
    }


    /**
     * Prepares a CUE file using a CDDB file and the multimedia filelist.
     *
     * @param txtFile The CDDB file.
     * @return
     */
    private static boolean doCUE ( String txtFile )
    {
        // get source file
        String[] src = new String[renameFiles.size()];
        for (  int i=0; i<renameFiles.size(); i++)
            src[i]=renameFiles.get(i).getSrc();

        try
        {
            CDDB txt = new CDDB ( txtFile, false );
            txt.doCUE( src );
        }
        catch (IOException e)
        {
        }
        // return false to not rename file
        return false;
    }


    /**
     * Prepare multimedia filenames to be renamed using a CDDB file.
     *
     * @param txtFile Filename.
     * @param noAuthor Print author in destination filename.
     * @return true or false.
     */
    private static boolean doTXTopt ( String txtFile, boolean noAuthor )
    throws java.io.IOException
    {
        // get the filename from txt file
        CDDB txt = new CDDB ( txtFile, noAuthor );
        String[] dests = txt.getDestination();

        // do the work
        if ( txt.isFileOK() && dests.length == renameFiles.size() )
        {
            for ( int i = 0; i < dests.length; i++ )
                renameFiles.get ( i ).setDest ( dests[ i ] );
            System.err.println ( "== Multimedia File renamed from CDDB file ==" );
        }
        else
        {
            if ( !txt.isFileOK() )
                System.err.print ( "\n== SYNTAX ERROR processing CDDB file ==\n" );
            else
                System.err.print ( "\n== ERROR on number of songs processing CDDB file ==\n" );
            System.err.print ( noFileRename );
            return false;
        }
        return true;
    }


    /**
     * Adds the file position as prefix using the M3U playlist.
     *
     * @param m3u The filename.
     * @return true or false.
     */
    private static boolean doM3Uopt ( String m3u )
    throws java.io.IOException
    {
        String[] fileLines = Str.readTXT ( m3u );
        int counter = 1;
        for ( int i = 0; i < fileLines.length; i++ )
        {
            String cur = fileLines[i].trim();
            if ( cur.equals ( "" )
            || cur.indexOf ( "#" ) == 0 )
                continue;
            String dd[] = Str.getFilenameExtension ( cur );
            String prefix = counter + "-";
            if ( counter < 10 )
                prefix = 0 + prefix;
            if ( cur.indexOf ( prefix ) != 0 )
                dd[0] = prefix + dd[0];
            File file = new File ( cur );
            file = new File ( file.getAbsolutePath() );
            if ( file.exists() )
            {
                renameFiles.add ( new CnxString ( file, dd[0] ) );
                counter++;
            }
            else
            {
                System.err.println("File " + cur + " in m3u file not found!!!");
                return false;
            }
        }
        return true;
    }


    /**
     * Renames the files using the given parameters
     *
     * @param par
     */
    private static void doAllWork ( CommandLine par )
    {
        String par1 = par.getOptionalAdditionalPar ( 1 );
        String par2 = par.getOptionalAdditionalPar ( 2 );
        boolean renameSomeFiles = false;

        // for each file to be renamed we apply the parameters
        // updating the CnxString objects.
        for ( int i = 0; i < renameFiles.size(); i++ )
        {
            // SET CASE INSENSITIVE AND/OR GLOBAL REPLACING
            if ( par1 != "" )
            {
                if ( par.isSet ( "ci" ) )
                    renameFiles.get ( i ).setCaseInsensitive();
                if ( par.isSet ( "g" ) )
                    renameFiles.get ( i ).setGlobalReplace();
                renameFiles.get ( i ).replaceDest ( par1, par2 );
            }

            /**
             * Now the program checks all the parameters
             */

            // -u option
            if ( par.isSet ( "u" ) )
                renameFiles.get ( i ).destUnderscore();

            // -rp
            if ( par.isSet ( "rp" ) )
                renameFiles.get ( i ).destSwapPos ( par.getParValue ( "rp" ) );

            // -ps
            if ( par.isSet ( "ps" ) )
                renameFiles.get ( i ).destReplaceChar ( par.getParValue ( "ps" ) );

            // -ai
            if ( par.isSet ( "ai" ) )
                renameFiles.get ( i ).getSequenceName ( par.getParValue ( "ai" ), i + 1 );

            // -pf
            if ( par.isSet ( "pf" ) )
                renameFiles.get ( i ).addPrefix ( par.getParValue ( "pf" ) );

            // -sf
            if ( par.isSet ( "sf" ) )
                renameFiles.get ( i ).addSuffix ( par.getParValue ( "sf" ) );

            // -ne
            if ( par.isSet ( "ne" ) )
                renameFiles.get ( i ).destNewExtension ( par.getParValue ( "ne" ) );


            /**
             * RENAME MODES
             */
            if ( !par.isSet ( "dummy" ) )
            {
                if ( par.isSet ( "cp" ) )
                {
                    renameFiles.get ( i ).destCapitalize();
                    renameFiles.get ( i ).setGlobalReplace();
                    renameFiles.get ( i ).replaceDest ( "\\s+", "_" );
                }
                else if ( par.isSet ( "o1" ) )
                {
                    if ( !par.isSet ( "ka" ) )
                        renameFiles.get ( i ).deleteApostrophe();
                    renameFiles.get ( i ).destMp3o1();
                }
                else if ( par.isSet ( "o2" ) )
                {
                    if ( !par.isSet ( "ka" ) )
                        renameFiles.get ( i ).deleteApostrophe();
                    renameFiles.get ( i ).destMp3o2();
                }
                else if ( par.isSet ( "o3" ) )
                {
                    if ( !par.isSet ( "ka" ) )
                        renameFiles.get ( i ).deleteApostrophe();
                    renameFiles.get ( i ).destMp3o3();
                }
                else if ( par.isSet ( "o4" ) )
                {
                    if ( !par.isSet ( "ka" ) )
                        renameFiles.get ( i ).deleteApostrophe();
                    renameFiles.get ( i ).destMp3o4();
                }
                else if ( par.isSet ( "o5" ) )
                    renameFiles.get ( i ).destMp3o5();
                else
                {
                    if ( !par.isSet ( "ka" ) )
                        renameFiles.get ( i ).deleteApostrophe();
                    renameFiles.get ( i ).destMp3o1();
                }
            }

            // now the filename is well formed but
            // we can set if filename is all Upper/Lower case
            if ( par.isSet ( "lc" ) )
                renameFiles.get ( i ).destLowerCase();
            if ( par.isSet ( "uc" ) )
                renameFiles.get ( i ).destUpperCase();

            // PRINT RESULT
            if ( ! renameFiles.get ( i ).srcISdest() )
            {
                if ( !par.isSet ( "test" ) )
                    System.out.println ( renameFiles.get ( i ).toString() );
                renameSomeFiles = true;
            }
        }

        // CHECK RENAME OPTIONS
        if ( renameSomeFiles )
        {
            String ppp = "no";
            if ( par.isSet ( "test" ) )
                for ( int i = 0; i < renameFiles.size(); i++ )
                {
                    if ( ! renameFiles.get ( i ).srcISdest() )
                        System.out.println ( renameFiles.get ( i ).getDest() );
                }
            else if ( par.isSet ( "auto" ) )
                ppp = "yes";
            else
            {
                System.err.print ( question );
                ppp = Str.getInput();
            }

            // APPLY RENAME OPTIONS
            if ( ppp.equals ( "yes" ) )
            {
                // renaming only file and stores directory in a new ArrayList
                ArrayList <CnxString> d = new ArrayList <CnxString> ();
                for ( int i = 0; i < renameFiles.size(); i++ )
                {
                    if (  renameFiles.get ( i ).isDir()  )
                        d.add ( renameFiles.get ( i ) );
                    else
                        renameFiles.get ( i ).destRename();
                }
                // now renaming directories
                for (int i=0; i<d.size(); i++)
                    d.get(i).destRename();
            }
            else if ( ppp.equals ( "help" ) )
                par.doHelp( );
            else
                System.err.print ( noFileRename );
        }
        else
            System.err.print ( noFileRename );

        if ( par.isSet ( "t" ) ) testLength ( 64 );
        else if ( par.isSet ( "t103" ) ) testLength ( 103 );
    }


    /**
     * Tests for filename length.
     *
     * @param l The maximum lengh allowed.
     */
    private static void testLength ( int l )
    {
        boolean fileOK = true;
        System.out.println ( "== List of files with length > " + l + " chars ==\n" );
        for ( int i = 0; i < renameFiles.size(); i++ )
        {
            String pp = renameFiles.get ( i ).getDest();
            if ( pp.length() > l )
            {
                fileOK = false;
                System.out.println ( " - " + pp.length() + ": " + pp );
            }
        }
        if ( fileOK )
            System.out.println ( " - NO FILEs FOUND" );
        System.out.println ( "" );
    }


    /**
     * The method parsethe given directory and populate renameFiles and srcFiles.
     *
     * @param par The input parameters.
     * @return true or false.
     */
    private static void doFindFile ( CommandLine par )
    throws java.io.IOException
    {
        renameFiles = new ArrayList <CnxString> ();

        // parse the current directory using absolute path
        File file = new File ( "." );
        file = new File ( file.getAbsolutePath() );
        srcFiles=Str.find ( file, par.isSet ( "rd" ), par.isSet ( "f" ) );

        // filename is multimedia file used by txt option
        Pattern media = Pattern.compile ( multimedia, Pattern.CASE_INSENSITIVE );

        // filename has no match with...
        String inverseMatch = "^\\s+$";
        if ( par.isSet ( "nm" ) )
            inverseMatch = par.getParValue ( "nm" );
        Pattern invMatch = getRegex ( inverseMatch, par.isSet ( "ci" ) );

        // filename has match with
        String match = "\\w+";
        if ( par.isSet ( "m" ) )
            match = par.getParValue ( "m" );
        Pattern nMatch = getRegex ( match, par.isSet ( "ci" ) );

        // parse the file list
        for ( int i = 0; i < srcFiles.length; i++ )
        {
            String pp = srcFiles[ i ].getName ();
            if ( pp.indexOf ( "." ) != 0
            && !invMatch.matcher ( pp ).find()
            && nMatch.matcher ( pp ).find() )
            {
                if ( (par.isSet ( "txt" ) || par.isSet ( "cue" ))
                && !media.matcher ( pp ).find()  )
                    continue;
                renameFiles.add ( new CnxString ( srcFiles[ i ] ) );
            }
        }
    }


    /**
     * Sets parameters and parses the command line.
     *
     * @param input The command line arguments.
     * @return The CommandLine object.
     */
    private static CommandLine myPar ( String[] input )
    {
        CommandLine parameter = new CommandLine ( 2 );
        parameter.addSintaxHelp ( "cnxrename [options] [regex [replace]]" );
        parameter.addMessageInfo ( name, version, date, copyright, author );
        parameter.addPar ( "h", "Print this help", false );
        parameter.addPar ( "help", "Print this help", false );
        parameter.addPar ( "auto", "Automatic rename. No 'yes' required", false );
        parameter.addPar ( "test", "test mode: show only the new filenames", false );
        parameter.addPar ( "q", "Quiet. No verbose", false );
        parameter.addPar ( "v", "More verbose", false );
        parameter.addPar ( "t", "64 is the max name length for destination file", false );
        parameter.addPar ( "t103", "103 is the max name length for destination file", false );
        parameter.addWhiteHelpLine ( "t103" );
        parameter.addPar ( "rd", "Recursive directory", false );
        parameter.addPar ( "ci", "Match case insensitive", false );
        parameter.addPar ( "g", "Subs all matched string in file", false );
        parameter.addPar ( "f", "Rename only files (skip directory)", false );
        parameter.addPar ( "m", "Match with <par>", true );
        parameter.addPar ( "nm", "Skip files match with <par>", true );
        parameter.addWhiteHelpLine ( "nm" );
        parameter.addPar ( "dummy", "Rename in dummy mode (only replace)", false );
        parameter.addPar ( "lc", "Rename all lower case", false );
        parameter.addPar ( "uc", "Rename all upper case", false );
        parameter.addPar ( "cp", "Rename with capitalize word", false );
        parameter.addPar ( "o1", "Rename with MY format (default)", false );
        parameter.addPar ( "o2", "Rename with space (_ to space)", false );
        parameter.addPar ( "o3", "Rename with space", false );
        parameter.addPar ( "o4", "Rename without space (foo_bar becomes FooBar)", false );
        parameter.addPar ( "o5", "dummy + o3 options (_ to space)", false );
        parameter.addWhiteHelpLine ( "o5" );
        parameter.addPar ( "u", "Add underscore before upper case chars", false );
        parameter.addPar ( "rp", "Swap position <n-m>", true );
        parameter.addPar ( "ps", "Replace position with a string: <n-str>", true );
        parameter.addPar ( "ai", "Make numbered filename <name-##-name.ext>", true );
        parameter.addPar ( "pf", "Add a prefix to filename", true );
        parameter.addPar ( "sf", "Add a suffix to filename", true );
        parameter.addPar ( "ne", "Change filename extension <newExt>", true );
        parameter.addPar ( "ka", "Do not remove the apostrophe from filename.", false );
        parameter.addWhiteHelpLine ( "ka" );
        parameter.addPar ( "txt", "load dest name from <CDDB file>", true );
        parameter.addPar ( "na", "no autor in filename with 'txt' option", false );
        parameter.addPar ( "m3u", "load file order from <m3u file> and add poition as prefix", true );
        parameter.addWhiteHelpLine ( "m3u" );
        parameter.addPar ( "cue", "du the CUE using <txt_file> and wavs in current directory", true );
        parameter.addPar ( "updateCUE", "Update the CUE using wavs in current directory", true );
        parameter.parsePar ( input );

        if ( parameter.checkPar() )
            return parameter;

        System.err.println ( "Parse error: wrong input parameters." );
        return null;
    }


    /**
     * Sets a regex.
     *
     * @param regex The regex string.
     * @param caseInsensitive true or false.
     * @return The compiled Pattern.
     */
    final private static Pattern getRegex ( String regex, boolean caseInsensitive )
    {
        if ( caseInsensitive )
            return Pattern.compile ( regex, Pattern.CASE_INSENSITIVE );
        return Pattern.compile ( regex );
    }


} // CLOSE CLASS
