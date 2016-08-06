/**

Copyright 2016 Luciano Xumerle

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

import java.util.Arrays;
import java.io.File;
import java.io.IOException;

/**
 * The Main Class
 *
 * @author Luciano Xumerle
 * @version 0.0.1
 */
public class CnxRename {

    /**
     * Informations used to print copyright string.
     */
    final private static String version = "5.0.1";
    final private static String date = "Aug 6, 2016";
    final private static String copyright = "2016";
    final private static String author = "Luciano Xumerle <luciano.xumerle@gmail.com>";
    final private static String name = "CNXRENAME";

    /**
     * Fixed String
     */
    private final static String question =
        "\n== print [yes] to rename files or [help] to show help ==> ";
    private final static String noFileRename =
        "\nNO FILEs RENAMED.\n\n";

    /**
     * Program inputs
     */
    static CommandLine PAR;
    static CnxString[] FILE;

    static int SIZE=-1;
    static boolean rename=false;

    public static void main ( String input[] )
    throws java.io.IOException {
        PAR=myPar(input);

        PAR.doMsg();
        if(PAR.isSet( "h" ) || PAR.isSet( "help" )) {
            PAR.doHelp();
            return;
        }

        /**
         * Import files/directories from m3u, filelist or cmmand line
         */
        if( PAR.isSet( "m3u" ) ) {
            String[] tt = Str.readTXT ( PAR.getParValue("m3u") );
            SIZE=tt.length;
            FILE=new CnxString[SIZE];
            for ( int i=0; i<SIZE; i++) {
                int pos=i+1;
                String dest=pos + "-";
                if ( pos<10 )
                    dest=0+dest;
                FILE[i]=new CnxString( new File( tt[i] ), dest + tt[i] );
            }
        } else if( PAR.isSet( "ls" ) ) {
            String[] tt = Str.readTXT ( PAR.getParValue("ls") );
            SIZE=tt.length;
            FILE=new CnxString[SIZE];
            for ( int i=0; i<tt.length; i++)
                FILE[i]=new CnxString( new File( tt[i] ) );
        } else {
            SIZE=PAR.getOptionalAdditionalParSize();
            FILE=new CnxString[SIZE];
            for( int i=1; i<=SIZE; i++ )
                try {
                    FILE[i-1]=new CnxString( new File( PAR.getOptionalAdditionalPar(i) ) );
                } catch( IOException e ) {
                    System.err.println("File or directory " + PAR.getOptionalAdditionalPar(i) + "not found!!!");
                }
        }

        /**
         * process the txt option
         */
        if( PAR.isSet( "txt" ) ) {
            CDDB txt = new CDDB ( PAR.getParValue( "txt" ), PAR.isSet( "na" ) );
            if ( txt.isFileOK() ) {
                String[] dests = txt.getDestination();
                for ( int i = 0; i < dests.length; i++ )
                    System.out.println( dests[i] +"      "+ SIZE  );
                if ( dests.length == SIZE ) {
                    for ( int i = 0; i < SIZE; i++ )
                        FILE[i].setDest ( dests[ i ] );
                    System.err.println( "== Multimedia File renamed from CDDB file ==" );
                } else {
                    System.err.println( "\n== SYNTAX ERROR processing CDDB file ==\n" );
                    return;
                }
            } else {
                System.err.println( "\n== ERROR on number of songs processing CDDB file ==\n\n" );
                return;
            }
        }


        /**
         * process the CUE option
         */
        if( PAR.isSet( "cue" ) ) {
            CDDB txt = new CDDB ( PAR.getParValue( "cue" ), false );
            if ( txt.isFileOK() && txt.getSongNumber() == SIZE ) {
                System.out.println(txt.getCUEheader());
                for( int i=0; i<SIZE; i++ )
                    System.out.println( txt.getCUEsong ( FILE[i].getName(), i ) );
            } else {
                System.err.println ( "ERROR: Bad txt file or wrong number of files and songs!" );
            }
            return;
        }

        /**
         * Set the replace strings if present
         */
        String search="";
        String replace="";
        if ( PAR.isSet("s") ) search=PAR.getParValue("s");
        if ( PAR.isSet("r") ) replace=PAR.getParValue("r");

        /**
         * PARSE ALL THE OTHER OPTIONS AND SET DESTINATION NAMES
         */
        for ( int i=0; i<SIZE; i++ ) {
            if ( ! search.equals("")  ) {
                if( PAR.isSet("ci") ) FILE[i].setCaseInsensitive();
                if( PAR.isSet("g") ) FILE[i].setGlobalReplace();
                if( PAR.isSet("d") ) FILE[i].setRenameDir();
                FILE[i].replaceDest( search, replace );
            }

            if ( PAR.isSet("ns") ) FILE[i].destNoSpace();
            else if ( PAR.isSet("ds")  ) FILE[i].destDummySpace();

            if ( PAR.isSet("cp") ) FILE[i].destCapitalize();
            else if ( PAR.isSet("-m1") ) FILE[i].destMp3o1();
            else if ( PAR.isSet("-m2") ) FILE[i].destMp3o2();
            else if ( PAR.isSet("-m3") ) FILE[i].destMp3o3();
            else if ( PAR.isSet("-m4") ) FILE[i].destMp3o4();

            if ( PAR.isSet("uc") ) FILE[i].destUpperCase();
            else if ( PAR.isSet("lc") ) FILE[i].destLowerCase();

            /**
             * PREVIEW RESULT
             */
            if ( ! FILE[i].srcISdest() ) {
                rename=true;
                System.err.println ( FILE[i].toString() );
            }
        }

        /**
         * ASK TO RENAME!!!
         */
        if ( rename ) {
            String ppp="no";
            System.err.print( "\n== print [yes] to rename files or [help] to show help ==> ");
            ppp = Str.getInput();

            // RENAME
            if (ppp.equals("yes")) {
                if( PAR.isSet("d") )
                    Arrays.sort(FILE);
                for ( int i = SIZE-1; i>=0 ; i-- )
                    FILE[i].destRename();
            }
        } else {
            PAR.doHelp();
            System.err.println( "==== ================ ====" );
            System.err.println( "==== NO FILEs RENAMED ====" );
            System.err.println( "==== ================ ====\n" );
        }
    } // END MAIN


    /**
     * Sets parameters and parses the command line.
     *
     * @param input The command line arguments.
     * @return The CommandLine object.
     */
    private static CommandLine myPar ( String[] input ) {
        CommandLine parameter = new CommandLine ( -1 );
        parameter.addSintaxHelp ( "cnxrename [options] [regex [replace]]" );
        parameter.addMessageInfo ( name, version, date, copyright, author );
        parameter.addPar ( "h", "Print this help", false );
        parameter.addPar ( "help", "Print this help", false );
        parameter.addWhiteHelpLine ( "help" );

        parameter.addPar ( "s", "Search regex string", true );
        parameter.addPar ( "r", "Destination string", true );
        parameter.addPar ( "ci", "String replace is case insensitive", false );
        parameter.addPar ( "g", "String replace is global", false );
        parameter.addPar ( "d", "Renames also directories", false );
        parameter.addWhiteHelpLine ( "d" );

        parameter.addPar ( "ns", "Replaces multiple spaces and '_' with underscore", false );
        parameter.addPar ( "ds", "Replaces multiple spaces and '_' with space, and '-' with ' - '", false );
        parameter.addPar ( "cp", "Capitalize words in filename", false );
        parameter.addPar ( "m1", "Rename with multimedia format", false );
        parameter.addPar ( "m2", "Rename with -m1 but '_' is space", false );
        parameter.addPar ( "m3", "Rename with -m1 but '_' is space and '-' is ' - '", false );
        parameter.addPar ( "m4", "Rename with -m1 but 'foo_bar' becomes 'FooBar'", false );
        parameter.addWhiteHelpLine ( "m4" );

        parameter.addPar ( "uc", "Destination names are upper case", false );
        parameter.addPar ( "lc", "Destination names are lower case", false );
        parameter.addWhiteHelpLine ( "lc" );

        parameter.addPar ( "m3u", "load file order from <m3u file> and add poition as prefix", true );
        parameter.addPar ( "ls", "Load input file list from file", true );
        parameter.addWhiteHelpLine ( "ls" );

        parameter.addPar ( "txt", "load dest name from <CDDB file>", true );
        parameter.addPar ( "na", "no autor in filename with 'txt' option", false );
        parameter.addWhiteHelpLine ( "na" );
        parameter.addPar ( "cue", "du the CUE using <txt_file> and wavs in current directory", true );
        // parameter.addPar ( "updateCUE", "Update the CUE using wavs in current directory", true );

        parameter.parsePar ( input );

        if ( parameter.checkPar() )
            return parameter;

        System.err.println ( "Parse error: wrong input parameters." );
        return null;
    }

} // END CLASS
