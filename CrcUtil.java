/// Module      :  CrcUtil.java
/// Description :  A CRC utility
/// Copyright   :  (c) 2025 Kamil Kurkiewicz
/// License     :  NONE
///
/// Maintainer  :  <kkurkiew@outlook.com>
/// Stability   :  experimental
/// Portability :  portable (Java 15+)
///
/// Defines a utility class for computing the CRC-32 checksum of
/// arbitrary input streams, intended to behave similarly to Cert[Uu]til.exe
///
/// To run, execute the following:
///
///     java CrcUtil.java
///


import java.io.*;
import java.util.Arrays;
import java.util.zip.CRC32;


////////////////////////////////////////////////////////////


/**
 * The driver
 */
public class CrcUtil {

    /**
     * Number of test blocks
     */
    private static final int TEST_BLOCK_COUNT = 1000000;

    /**
     * Length of test block
     */
    private static final int TEST_BLOCK_LENGTH = 1000000;

    /**
     * Default buffer size
     */
    private static final int DEFAULT_BUFFER_SIZE = 32768;

    /**
     * Minimum buffer size
     */
    private static final int MIN_BUFFER_SIZE = 1;

    /**
     * Maximum buffer size
     */
    private static final int MAX_BUFFER_SIZE = 2147483645;

    /**
     * Entry point
     */
    public static void main(String[] args) {
        int i;
        int BufferSize;

        if (args.length == 0) {
            usage(false);
            return;
        }

        if (Arrays.asList(args).contains("-?") || Arrays.asList(args).contains("/?")) {
            usage(true);
            return;
        }

        if (Arrays.asList(args).contains("-t")) {
            timetrial();
            return;
        }

        if (Arrays.asList(args).contains("-x")) {
            testsuite();
            return;
        }

        if (Arrays.asList(args).contains("-s")) {
            i = 0;
            while (!args[i].equals("-s")) {
                i++;
            }
            crcString(args[i+1], false);
            return;
        }

        if (Arrays.asList(args).contains("-showupdates")) {
            i = 0;
            while (!args[i].equals("-showupdates")) {
                i++;
            }
            if (i + 1 == args.length) {  // java CrcUtil.java -showupdates
                System.out.print("""
                                 Expected at least 1 argument, received 0
                                 CrcUtil: Missing argument
                                 
                                 """);
                usage(false);
            } else if (i + 2 == args.length) {  // java CrcUtil.java -showupdates InFile
                crcFile(true, DEFAULT_BUFFER_SIZE, args[i+1]);
            } else {  // java CrcUtil.java -showupdates BufferSize InFile
                try {
                    BufferSize = Integer.parseUnsignedInt(args[i+1]);
                    if (BufferSize < MIN_BUFFER_SIZE || BufferSize > MAX_BUFFER_SIZE) {
                        System.out.printf(
                                "CrcUtil: BufferSize should be an unsigned integer in the range of %d through %d.\n",
                                        MIN_BUFFER_SIZE, MAX_BUFFER_SIZE);
                    } else {
                        crcFile(true, BufferSize, args[i+2]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("CrcUtil: The provided BufferSize argument does not have the appropriate format.");
                }
            }
            return;
        }

        // java CrcUtil.java InFile
        crcFile(false, DEFAULT_BUFFER_SIZE, args[0]);
    }

    /**
     * Displays help text
     */
    private static void usage(boolean full) {
        System.out.print("""
                         Usage:
                           java CrcUtil.java [Options] [InFile]
                           Generate and display CRC32 checksum over a file
                         
                         Options:
                           -t                -- Run time trial
                           -x                -- Run test script
                           -s String                  -- Checksum string
                           -showupdates [BufferSize]  -- Process BufferSize bytes at a time
                         
                         BufferSize ranges from 1 to 2147483645 (default: 32768)
                         
                         CrcUtil -?              -- Display help text
                         
                         """
                         + (full ? """
                         CrcUtil: -? command completed successfully.
                         
                         """ : ""));
    }

    /**
     * Runs time trial
     */
    private static void timetrial() {
        int i;
        CRC32 crc32;
        long startTime, endTime;
        byte[] block = new byte[TEST_BLOCK_LENGTH];

        System.out.printf(
                "CRC32 time trial. Checksumming %d %d-byte blocks...", TEST_BLOCK_COUNT, TEST_BLOCK_LENGTH);

        for (i = 0; i < TEST_BLOCK_LENGTH; i++) {
            block[i] = (byte) (i & 0xFF);
        }

        startTime = System.currentTimeMillis();

        crc32 = new CRC32();
        for (i = 0; i < TEST_BLOCK_COUNT; i++) {
            crc32.update(block, 0, TEST_BLOCK_LENGTH);
        }

        endTime = System.currentTimeMillis();

        System.out.printf("""
                           done
                          Checksum = %x
                          Time = %d seconds
                          Speed = %d bytes/second
                          CrcUtil: -t command completed successfully.
                          """, crc32.getValue(), (endTime - startTime) / 1000, ((long) TEST_BLOCK_LENGTH * (long) TEST_BLOCK_COUNT) / ((endTime - startTime) / 1000));
    }

    /**
     * Runs test script
     */
    private static void testsuite() {
        System.out.println("CRC32 test suite:");
        crcString("");
        crcString("a");
        crcString("abc");
        crcString("cyclic redundancy check");
        crcString("abcdefghijklmnopqrstuvwxyz");
        crcString("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        crcString("12345678901234567890123456789012345678901234567890123456789012345678901234567890");
        System.out.println("CrcUtil: -x command completed successfully");
    }

    /**
     * Checksums a string
     * <p>
     * NOTE: Checksumming a string is <i>not</i> buffered
     */
    private static void crcString(String str) {
        crcString(str, true);
    }

    /**
     * Like {@link CrcUtil#crcString(String)}, but can be told to produce more output
     */
    private static void crcString(String str, boolean quiet) {
        CRC32 crc32 = new CRC32();
        crc32.update(str.getBytes());
        if (quiet) {
            System.out.printf("\"%s\" = %x\n", str, crc32.getValue());
        } else {
            System.out.printf("""
                              CRC32 checksum of "%s":
                              %x
                              CrcUtil: -s command completed successfully.
                              """, str, crc32.getValue());
        }
    }

    /**
     * Checksums a file.
     *
     * @param showupdates a boolean.
     *                    {@code crcFile} computes the checksum incrementally utilizing an
     *                    internal buffer. If this parameter is set to {@code true} then, apart from the
     *                    final CRC value, the output produced will also include all intermediate values in sequence order.
     * @param BufferSize  an integer.
     *                    If {@code showupdates} is {@code true}, specifies the size
     *                    (or length) of the internal buffer; otherwise, the parameter is ignored.
     * @param InFile      the file to checksum
     */
    private static void crcFile(boolean showupdates, int BufferSize, String InFile) {
        int     i;
        byte[]  buf;
        CRC32   crc32;
        FileInputStream  fin;

        try {
            fin = new FileInputStream(InFile);
        } catch (FileNotFoundException e) {
            System.out.println("CrcUtil: The system cannot find the file specified.");
            return;
        }

        try {
            crc32 = new CRC32();
            buf = new byte[BufferSize];

            System.out.printf(
                    (showupdates ? "Incremental " : "") + "CRC32 checksum of %s:\n", InFile);

            if (showupdates) {
                int upd;

                upd = 0;
                i = fin.read(buf);
                while (i != -1) {
                    crc32.update(buf, 0, i);
                    System.out.printf("Update %d = %x\n", upd, crc32.getValue());
                    upd++;
                    i = fin.read(buf);
                }
            } else {
                i = fin.read(buf);
                while (i != -1) {
                    crc32.update(buf, 0, i);
                    i = fin.read(buf);
                }
                System.out.printf("%x\n", crc32.getValue());
            }
            System.out.println(
                    "CrcUtil: " + (showupdates ? "-showupdates command " : "Command ") + "completed successfully");
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

