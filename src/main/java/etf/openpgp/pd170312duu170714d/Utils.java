/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etf.openpgp.pd170312duu170714d;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

}
