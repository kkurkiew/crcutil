/// Module      :  CrcUtil.java
/// Description :  A CRC utility
/// Copyright   :  (c) Kamil Kurkiewicz
/// License     :  NONE
///
/// Maintainer  :  <kkurkiew@outlook.com>
/// Stability   :  experimental
/// Portability :  portable (Java 15+)
///
/// Defines a utility class for computing the CRC-32
/// checksum of arbitrary input streams, intended to be run as a script
///
/// To run, execute the following:
///
///     java CrcUtil.java
///


import java.io.*;
import java.util.zip.CRC32;


////////////////////////////////////////////////////////////


/**
 * The driver
 */
public class CrcUtil {

    public static void main(String[] args) {
        FileInputStream fin;

        if (args.length != 1) {
            System.out.printf("""
                              Expected 1 argument, received %d
                              CrcUtil: Wrong number of arguments
                              
                              Usage:
                                java CrcUtil.java InFile
                                Generate and display CRC32 checksum over a file
                              
                              """, args.length);
            return;
        }

        try {
            fin = new FileInputStream(args[0]);
        } catch (FileNotFoundException e) {
            System.out.println("CrcUtil: The system cannot find the file specified.");
            return;
        }

        try {
            CRC32 crc32 = new CRC32();
            crc32.update(fin.readAllBytes());
            System.out.printf("""
                              CRC32 checksum of %s:
                              %x
                              CrcUtil: Command completed successfully.
                              """, args[0], crc32.getValue());
        } catch (IOException e) {
            System.out.println("CrcUtil: The system cannot read from the specified device.");
        }

        try {
            fin.close();
        } catch (IOException e) {
            System.out.println("CrcUtil: The input file could not be closed.");
        }
    }

}

