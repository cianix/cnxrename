/**
 * file name  : it/ciano/cnxrename/CommandLine.java
 * authors    : Luciano Xumerle
 * created    : mar 15 apr 2014 16:17:17 CEST
 * copyright  : GPL3
 *
 */

package it.ciano.cnxrename;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.StringBuffer;

/**
 * Simple class to manage the CLI arguments.
 *
 * @author Luciano Xumerle
 * @version 0.4.0
 */
public class CommandLine {
    // get the par position into the list
    private HashMap <String,String[]> _pars;

    // list of parameters
    private ArrayList  <String> _order;

    // optional additional parameters -> in mv is a file name
    private ArrayList <String> _addpar;

    // NUMBER OF _addpar ALLOWED
    private int _numberOfParameters;

    // DEFAULT SYNTAX HELP
    private StringBuffer syntaxHelp;
    private String messageHelp;

    // IS ALL OK
    private boolean paramOK;

    // first year on copyright info
    private String startYear;


    /**
     * Class constructor.
     *
     * @param numOfPar How many fields are not to be consider parameters. Use -1 to hane no limit.
     * @param startYearOnCopyrigth Copyright year.
     */
    public CommandLine ( int numOfPar, String startYearOnCopyrigth ) {
        _numberOfParameters=numOfPar;

        _pars=new HashMap <String,String[]> ();
        _order= new ArrayList <String> ();
        _addpar = new ArrayList <String> ();

        syntaxHelp = new StringBuffer();
        messageHelp = new String();
        startYear = startYearOnCopyrigth;
        paramOK = true;
    }


    /**
     * Class constructor.
     *
     * @param numOfPar How many fields are not to be consider parameters.
     */
    public CommandLine ( int numOfPar ) {
        this ( numOfPar, "1970" );
    }


    /**
     * Adds a parameters to the list.
     *
     * @param parname Parameter's name.
     * @param help The help String.
     * @param withValue true or false.
     */
    public void addPar ( String parname, String help, boolean withValue ) {

        String wv="n";
        if (  withValue ) wv="y";

        // the array fields are: Arguments if given, help string,
        // par must have a value [y,n], adds a white line after par in help string [y,n],
        // par is set [y,n]
        _pars.put ( parname, new String[] { "", help,  wv, "n", "n" } );
        _order.add ( parname );
    }


    /**
     * Sets a white line in help string after the given parameter name.
     *
     * @param parameter Parameter's name.
     */
    public boolean addWhiteHelpLine ( String parameter ) {
        if ( _pars.containsKey( parameter ) ) {
            String[] t = _pars.get( parameter );
            t[3]="y";
            _pars.put ( parameter, t );
            return true;
        }
        return false;
    }


    /**
     * Appends the fixed prefix strings to the help.
     *
     * @param str The str to be appened.
     */
    public void addSintaxHelp ( String str ) {
        syntaxHelp.append ( "SYNTAX:\n" ).append ( str ).append ( "\n" ).append ( "\nOPTIONS:\n" );
    }


    /**
     * Appends the copyright info to the help.
     *
     * @param name Program's name.
     * @param version Version.
     * @param date Build date.
     * @param cp Copyright.
     * @param auth Author.
     */
    public void addMessageInfo ( String name, String version, String date, String cp, String auth ) {
        String yy = startYear;
        if ( !startYear.equals ( cp ) )
            yy += "-" + cp;

        messageHelp=name + " version " + version + " (" + date + ")\n";
        String cr = "Copyright (C) " + yy + " by " + auth + "\n";
        String notice1 = "This is free software; see the source for copying conditions. There is NO\n";
        String notice2 = "warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.\n";
        String help = "[-h] : Print help.\n";
        String line = "";
        int ln = Math.max ( Math.max ( messageHelp.length(), cr.length() ), notice2.length() );
        for ( int i = 1; i < ln; i++ )
            line += "-";
        messageHelp=line + "\n" + messageHelp + cr + notice1 + notice2 + help + line + "\n";
    }


    /**
     * Returns true if input parameters are OK.
     *
     * @return true or false.
     */
    public boolean checkPar() {
        return paramOK;
    }


    /**
     * Sets to true the given parameter.
     *
     * @param parameter Parameter's name.
     * @return true or false.
     */
    public boolean setSelected ( String parameter ) {
        if ( _pars.containsKey( parameter ) ) {
            String[] t = _pars.get( parameter );
            t[4]="y";
            _pars.put ( parameter, t );
            return true;
        }
        return false;
    }


    /**
     * Returns true if the given parameter is set.
     *
     * @param par Parameter's name.
     * @return true or false.
     */
    public boolean isSet ( String parameter ) {
        if ( _pars.containsKey( parameter ) ) {
            String[] t = _pars.get( parameter );
            return ( t[4].equals("y")  );
        }
        return false;
    }


    /**
     * Set the value of the given parameter.
     *
     * @param parameter Parameter's name.
     * @param value Parameter's value.
     * @return true or false.
     */
    public boolean setValue ( String parameter, String value ) {
        if ( _pars.containsKey( parameter ) ) {
            String[] t = _pars.get( parameter );
            t[0]=value;
            _pars.put ( parameter, t );
            return true;
        }
        return false;
    }


    /**
     * Returns true if the parameter must have a value.
     *
     * @param parameter Parameter's name.
     * @return true or false.
     */
    public boolean parWithValue ( String parameter ) {
        if ( _pars.containsKey( parameter ) ) {
            String[] t = _pars.get( parameter );
            return ( t[2].equals("y") );
        }
        return false;
    }


    //
    // parse the command line input
    //

    /**
     * Parses the command line input.
     *
     * @param input args string[].
     */
    public void parsePar ( String[] input ) {
        boolean ret = true;
        String nextpar = "0";
        for ( int i = 0; i < input.length; i++ ) {
            if ( nextpar.equals ( "0" ) ) {
                boolean ispar = false;
                String cpar = trimParameter ( input[ i ] );

                if ( !cpar.equals ( "" ) && _pars.containsKey ( cpar ) ) {
                    ispar = true;
                    setSelected ( cpar );

                    if ( parWithValue ( cpar ) ) {
                        nextpar = "1";
                        if ( ( i + 1 ) < input.length )
                            setValue ( cpar, input[ i + 1 ] );
                        else
                            ret = false;
                    }
                }

                if ( !ispar ) {
                    if ( _numberOfParameters < 0 || _addpar.size() < _numberOfParameters )
                        _addpar.add ( input[ i ] );
                    else
                        ret = false;
                }
            } else
                nextpar = "0";
        }

        // SET TO EMPTY STRING UNDEFINED PAR
        while( _addpar.size() < _numberOfParameters )
            _addpar.add ( "" );

        // SET paramOk
        paramOK = ret ;
    }


    /**
     * Returns the additional parameter if set, else the empty string.
     *
     * @param i Parameter's position.
     * @return A string.
     */
    public String getOptionalAdditionalPar ( int i ) {
        if ( i > 0 && i <= _addpar.size() )
            return  _addpar.get ( i - 1 );
        return "";
    }


    /**
     * Returns the additional parameter if set, else the empty string.
     *
     * @param i Parameter's position.
     * @return A string.
     */
    public int getOptionalAdditionalParSize () {
        return _addpar.size();
    }


    /**
     * Returns the parameter's value.
     *
     * @param parameter Parameter's name.
     * @return The value.
     */
    public String getParValue ( String parameter ) {
        if ( _pars.containsKey( parameter ) ) {
            String[] t = _pars.get( parameter );
            return t[0];
        }
        return "";
    }


    /**
     * The toString method.
     *
     * @return A string.
     */
    public String toString() {
        String pp = "PARAMETERS:\n";
        for ( int i = 0; i < _addpar.size(); i++ )
            pp += "- parameter " + ( i + 1 ) + " is : " + _addpar.get ( i ) + "\n";

        pp += "Options:\n";
        for ( int i = 0; i < _order.size(); i++ ) {
            String tt=_order.get(i);
            String[] cpar = _pars.get ( tt );
            pp += "- option " + tt;
            if ( cpar[2].equals ( "y" ) )
                pp +=" with value " + cpar[0];
            if ( cpar[3].equals("y") ) pp+=" [*]";
            pp+="\n";
        }
        return pp;
    }


    /**
     * Print to the STDERR the help message.
     */
    public void doMsg ( ) {
        System.err.println ( messageHelp );
    }


    /**
     * The method prints the help.
     */
    public void doHelp() {
        for ( int i = 0; i < _order.size(); i++ ) {
            String cpar = _order.get( i );
            String[] tt = _pars.get(cpar);
            String space = "";
            String value = cpar;
            if ( tt[2].equals ( "y" ) )
                value += " <par>";
            int cc = 11 - value.length();
            for ( int y = 0; y < cc; y++ )
                space += " ";
            syntaxHelp.append ( space ).append ( "-" ).append ( value ).append ( " : " ).append ( tt[1] ).append ( "\n" );
            if ( tt[3].equals ( "y" ) )
                syntaxHelp.append ( "\n" );
        }
        System.out.println ( syntaxHelp.toString() );
    }


    /**
     * Remove dash from the string.
     *
     * @param par String.
     * @return String without starting dash.
     */
    private static String trimParameter ( String par ) {
        if ( par.startsWith ( "-" ) )
            return par.substring ( 1 );
        else if ( par.startsWith ( "--" ) )
            return par.substring ( 2 );
        return "";
    }

} // CLOSE CLASS
