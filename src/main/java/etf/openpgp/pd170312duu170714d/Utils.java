/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.openpgp.pd170312duu170714d;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.bouncycastle.bcpg.ArmoredOutputStream;

/**
 *
 * @author Uros
 */
public class Utils {
    public static void touch_file( File file ) throws FileNotFoundException
    {
        file.getParentFile().mkdirs();

        try
        {
            file.createNewFile();
        }
        catch( IOException ex )
        {
            if( Files.notExists( Paths.get( file.getAbsolutePath() ) ) )
            {
                throw new FileNotFoundException( "Could not ensure the file exists" );
            }
        }
    }
    
    public static String keyIdToHexString( long keyId )
    {
        String hexString = Long.toHexString( keyId );
        String userFriendlyHexString = hexString.replaceAll( "....(?!$)", "$0 " );
        return userFriendlyHexString;
    }

    public static long hexStringToKeyId( String userFriendlyHexString )
    {
        String hexString = userFriendlyHexString.replaceAll( "\\s", "" );
        String mostSignificantBits = hexString.substring( 0, 8 );
        String leastSignificantBits = hexString.substring( 8, 16 );
        long keyId = (Long.parseLong( mostSignificantBits, 16 ) << 32) | Long.parseLong( leastSignificantBits, 16 );
        return keyId;
    }
    
    // file chooser dialog type
    public static final int OPEN_DIALOG = JFileChooser.OPEN_DIALOG;
    public static final int SAVE_DIALOG = JFileChooser.SAVE_DIALOG;
    // file chooser file types
    public static final int ANY_FILE = 0;
    public static final int PGP_MESSAGE_FILE = 1;
    public static final int PGP_KEY_FILE = 2;
    public static final int TXT_FILE = 3;
    // file chooser previous path
    private static File previousPath = null;

    public static String getUserSelectedFilePath( int dialogType, int allowedFileType )
    {
        JFileChooser jFileChooser = new javax.swing.JFileChooser();
        jFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        jFileChooser.setMultiSelectionEnabled( false );
        jFileChooser.setCurrentDirectory( previousPath );

        switch( allowedFileType )
        {
            case ANY_FILE:
            {
                break;
            }
            case PGP_MESSAGE_FILE:
            {
                jFileChooser.setFileFilter( new FileNameExtensionFilter( "PGP message (*.gpg, *.sig)", "gpg", "sig" ) );
                break;
            }
            case PGP_KEY_FILE:
            {
                jFileChooser.setFileFilter( new FileNameExtensionFilter( "PGP key file (*.asc)", "asc" ) );
                break;
            }
            case TXT_FILE:
            {
                jFileChooser.setFileFilter( new FileNameExtensionFilter( "Text file (*.txt)", "txt" ) );
                break;
            }
            default:
            {
                throw new IllegalArgumentException( "Invalid <allowed file type> provided" );
            }
        }

        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
        // these two lines dont't work since we don't have internal access to the showOpenDialog and showSaveDialog methods
        // jFrame.setTitle("Choose file");
        // jFrame.getContentPane().setSize(new Dimension(640, 480));

        int dialogStatus = -1;
        switch( dialogType )
        {
            case OPEN_DIALOG:
            {
                dialogStatus = jFileChooser.showOpenDialog( jFrame );
                break;
            }
            case SAVE_DIALOG:
            {
                dialogStatus = jFileChooser.showSaveDialog( jFrame );
                break;
            }
            default:
            {
                throw new IllegalArgumentException( "Invalid dialog type provided" );
            }
        }

        if( dialogStatus != JFileChooser.APPROVE_OPTION )
        {
            return null;
        }

        String filePath = jFileChooser.getSelectedFile().getAbsolutePath();
        switch( allowedFileType )
        {
            case PGP_MESSAGE_FILE:
            {
                if( !filePath.endsWith( ".gpg" ) )
                    filePath += ".gpg";
                break;
            }
            case PGP_KEY_FILE:
            {
                if( !filePath.endsWith( ".asc" ) )
                    filePath += ".asc";
                break;
            }
        }

        previousPath = jFileChooser.getCurrentDirectory();
        return filePath;
    }
    
    public static void writeToFile( String filePath, byte[] content )
    {
        try {
            File outputFile = new File( filePath );
            outputFile.createNewFile();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream( filePath );
                fos.write( content );
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public static byte[] readFromFile( String filePath )
    {
        File file = new File( filePath );
        try(FileInputStream fin = new FileInputStream( file );)
        {
            byte fileContent[] = new byte[( int )file.length()];
            fin.read( fileContent );
            return fileContent;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
    
    public static byte[] encodeAsRadix64(
            byte[] message ) throws IOException
    {
        if( message == null )
            return null;

        ByteArrayOutputStream messageStream = null;
        ArmoredOutputStream armoredStream = null;

        try
        {
            // make an armored output stream using the message stream
            messageStream = new ByteArrayOutputStream();
            armoredStream = new ArmoredOutputStream( messageStream );

            // write the radix64 data packet to the message stream and close the armored data stream
            armoredStream.write( message );
            armoredStream.close();

            // overwrite the message buffer and close the message stream
            message = messageStream.toByteArray();
            messageStream.close();

            return message;
        }
        catch( IOException ex ){}
        finally
        {
            try
            {
                // close all open resources
                if( messageStream != null )
                    messageStream.close();
                if( armoredStream != null )
                    armoredStream.close();
            }
            catch( IOException ex ){}
        }
        return null;
    }

    public static void writeToFile( String filePath, String content )
    {
        try( PrintWriter out = new PrintWriter( filePath ) )
        {
            out.println( content );
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
