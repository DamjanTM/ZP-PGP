/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.openpgp.pd170312duu170714d;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPUtil;

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

    private static String startingFolder = System.getProperty("user.home")+"\\Desktop";
        
    public static Path getUserSelectedFilePath( int dialogType_, String extension_ )
    {
        Path ret = null;
        JFileChooser chooser = new JFileChooser(startingFolder);
        chooser.setDialogType(dialogType_);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("(*."+extension_+") files", extension_);
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(new JPanel());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            ret = Paths.get(chooser.getSelectedFile().getPath());
            startingFolder = chooser.getSelectedFile().getParentFile().getPath();
            if( !ret.endsWith( "." + extension_ ) )
                ret = ret.resolveSibling(ret.getFileName() +  "." + extension_ );
        }
        return ret;
    }
    
        public static InputStream removeRadix64Encoding( InputStream inputStream ) throws IOException
    {
        return PGPUtil.getDecoderStream( new BufferedInputStream( inputStream ) );
    }

}
