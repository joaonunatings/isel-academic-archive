import java.io.Console;
import java.text.ParseException;
import java.util.Scanner;

public class HybridCipher {

    private static String[] args = null;

    private static final Console console = System.console();
    private static final Scanner scanner = new Scanner(System.in);

    private static final String TRUST_ANCHORS_PATH = "files/certs/trust-anchors/trust.jks";
    private static final String TRUST_ANCHORS_PASSWORD = "changeit";

    public static void main(String[] _args) {

        args = _args;
        if(args.length == 0) {
            System.err.println("Missing arguments.\nPlease use: java HybridCipher <option>. Try -help for help.");
            System.exit(-1);
        }

        Option option = null;
        try {
            option = Option.parse(args[0]);
        } catch (ParseException e) {
            System.err.println("Invalid option: " + args[0]);
            System.exit(-1);
        }

        option.action.run();

        System.out.println("\nDone.");
        System.exit(0);
    }

    public static void encode() {
        if (args.length < 3) {
            System.err.println("Missing arguments.\nPlease use: java HybridCipher -enc <data file> <certificate file> [cipher output file] [key output file] [iv]");
            System.exit(-1);
        }
        System.out.println("Current mode: ENCODE");
        HybridEncoder encoder = new HybridEncoder(TRUST_ANCHORS_PATH, TRUST_ANCHORS_PASSWORD);

        // Args: [1] - data file; [2] - certificate file; [3] - cipher output file; [4] - key output file; [5] - iv
        String dataFilePath = args[1];
        String certificateFilePath = args[2];
        String iv;
        if (args.length < 6) {
            System.out.println("\nPlease enter the IV: ");
            // NOTE: If running on Intellij IDE, use scanner instead of console.
            // iv = console.readLine();
            iv = scanner.nextLine();
        } else iv = args[5];

        if (args.length >= 5) encoder.encode(dataFilePath, certificateFilePath, iv, args[3], args[4]);
        else encoder.encode(dataFilePath, certificateFilePath, iv);
    }

    public static void decode() {
        if (args.length < 2) {
            System.err.println("Missing arguments.\nPlease use: java HybridCipher -dec <ciphered file> <ciphered key file> <certificate private file> [output file] [iv] [private key password]");
            System.exit(-1);
        }

        System.out.println("Current mode: DECODE");
        HybridDecoder decoder = new HybridDecoder();

        String cipheredFilePath = args[1];
        String cipheredKeyFilePath = args[2];
        String certificatePrivateFilePath = args[3];

        String iv;
        String password;
        if (args.length < 6) {
            System.out.println("\nPlease enter the IV: ");
            // NOTE: If running on Intellij IDE, use scanner instead of console.
            // iv = console.readLine();
            iv = scanner.nextLine();
        } else
            iv = args[5];

        if (args.length < 7) {
            System.out.println("\nPlease enter the private key password:");
            // NOTE: If running on Intellij IDE, use scanner instead of console.
            // password = new String(console.readPassword());
            password = scanner.nextLine();
        } else
            password = args[6];

        // Args: [1] - ciphered file; [2] - ciphered key file; [3] - certificate private file; [4] - output file; [5] - iv; [6] - private key password
        if (args.length >= 5)
            decoder.decode(cipheredFilePath, cipheredKeyFilePath, certificatePrivateFilePath, iv, password, args[4]);
        else
            decoder.decode(cipheredFilePath, cipheredKeyFilePath, certificatePrivateFilePath, iv, password);
    }

    public static void printHelp() {
        System.out.println("Showing help\n");
        System.out.println("Usage: java HybridCipher <option> [args]\n");
        System.out.println("Options:");
        System.out.println("-enc <data file> <certificate file> [cipher output file] [key output file] [iv]");
        System.out.println("-dec <ciphered file> <ciphered key file> <certificate private file> [output file] [iv] [private key password]");
    }

    public enum Option {
        ENC("-enc", HybridCipher::encode),
        DEC("-dec", HybridCipher::decode),
        HELP("-help", HybridCipher::printHelp);

        private final String arg;
        private final Runnable action;

        Option(String arg, Runnable action) {
            this.arg = arg;
            this.action = action;
        }

        static Option parse(String arg) throws ParseException {
            for (Option option : Option.values()) {
                if (option.arg.equals(arg)) {
                    return option;
                }
            }
            throw new ParseException("Invalid option: " + arg, 0);
        }
    }

}
