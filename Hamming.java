/**
 * @author Liby Lee
 * @date 09-26-2016
 */

/**
 * Hamming Class, Provide apis for encoding and decoding 4 digit codes
 */
public class Hamming {
    
    //What digit the parity bit covers
    private static int[][] parityCoversDigit = {{0, 1, 3},  //p1 covers d1, d2, d4
                                                {0, 2, 3},  //p2 covers d1, d3, d4
                                                {1, 2, 3}}; //p3 covers d2, d3, d4
    
    /**
     * Encode the length 4 data
     * @param data the data to be encoded
     * @return the encoded int array data
     */
    public static int[] encode(int[] data) {
        int[] parity = getParity(data); //p1, p2 and p3
        
        //Note: append a 0 in the end of the array
        int[] encoded = {parity[0], parity[1], data[0], parity[2], data[1], data[2], data[3], 0};
        
        //Calculate the num of the '1's. And append the extra parity bit to the encoded data
        int sum = 0;
        for (int i = 0; i < encoded.length; i++) {
            sum += encoded[i];
        }
        encoded[7] = sum % 2;
        return encoded;
    }
    
    /**
     * Decode the encoded code
     * @param encoded the encoded length 8 array.
     * @return int[] the decoded length 4 data.
     * @throws InvalidArgumentException if there's 2 errors in the encoded data
     */
    public static int[] decode(int[] encoded) {
        
        //Save all the useful data
        int[] data = {encoded[2], encoded[4], encoded[5], encoded[6]};
        int[] parity = {encoded[0], encoded[1], encoded[3]};
        
        //Generate the expected correctParity based on the 4 digit data
        int[] correctParity = getParity(data);
        
        //Compare the given parity and the correct parity
        //If like original p1 equals expected p1, then comparison[0] is true
        boolean[] comparison = {false, false, false};
        for (int i = 0; i < comparison.length; i++) {
            comparison[i] = parity[i] == correctParity[i];
        }
        
        //Check if there's an error in the encoded array
        //And save that position in the incorrectPosition
        int incorrectPosition = -1;
        if (!comparison[0] && comparison[1] && comparison[2]) { //p1 wrong
            incorrectPosition = 0;
        }
        if (comparison[0] && !comparison[1] && comparison[2]) { //p2 wrong
            incorrectPosition = 1;
        }
        if (!comparison[0] && !comparison[1] && comparison[2]) { //p1, p2 wrong
            incorrectPosition = 2;
        }
        if (comparison[0] && comparison[1] && !comparison[2]) { //p3 wrong
            incorrectPosition = 3;
        }
        if (!comparison[0] && comparison[1] && !comparison[2]) { //p1, p3 wrong
            incorrectPosition = 4;
        }
        if (comparison[0] && !comparison[1] && !comparison[2]) { //p2, p3 wrong
            incorrectPosition = 5;
        }
        if (!comparison[0] && !comparison[1] && !comparison[2]) { //all three are wrong
            incorrectPosition = 6;
        }
        
        //Check if there's an incorrect digit
        if (incorrectPosition != -1) {
            
            //Flip the digit on the [incorrectPosition] of the 8 digit encoded code
            encoded[incorrectPosition] = encoded[incorrectPosition] == 0 ? 1 : 0;
            
            //Ok we now flipped the digit.
            //Then we need to calculate how many 1's are in the encoded array.
            //This will be stored in the [sum]
            int sum = 0;
            for (int i = 0; i < encoded.length; i++) {
                sum += encoded[i];
            }
            
            //If there are even 1s, then we are good
            //If there are odd 1s, then there must be two errors which we cannot fix
            if (sum % 2 == 0) {
                
                //Now the sum is even, so we are goo.
                //Generate the data from the 8 digit encoded code
                //And return the data
                int[] decodedData = {encoded[2], encoded[4], encoded[5], encoded[6]};
                return decodedData;
            }
            else {
                
                //No we are not good.
                //Print the Error and return null.
                System.out.println("Error Occurs, May Contain 2 Errors.");
                return null;
            }
        }
        else {
            
            //Ok so there are no error in the first 7 bits.
            //And for now, it doesn't matter whether p8 is correct or not
            //So just return the data!
            return data;
        }
    }
    
    /**
     * Get the 3 parity bits of a length 4 data
     * @param data the original length 4 data
     * @return int[] the 3 parity bits
     */
    private static int[] getParity(int[] data) {
        int[] parity = {0, 0, 0};
        for (int i = 0; i < parity.length; i++) {
            int sum = 0;
            for (int j = 0; j < parityCoversDigit[i].length; j++) {
                sum += data[parityCoversDigit[i][j]];
            }
            parity[i] = sum % 2;
        }
        return parity;
    }
    
    /**
     * Change the int array to data string
     * @param data the data containing 0 and 1 ints.
     * @return string
     */
    private static String toString(int[] data) {
        String str = "";
        for (int i = 0; i < data.length; i++) {
            str += data[i];
        }
        return str;
    }
    
    /**
     * Change the string to int array
     * @param data to be translated
     */
    private static int[] toArray(String data) {
        int[] arr = new int[data.length()];
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == '0') {
                arr[i] = 0;
            }
            else {
                arr[i] = 1;
            }
        }
        return arr;
    }
    
    /**
     * Print the data given the int array data
     * @param data the data to be printed
     */
    private static void printData(int[] data) {
        if (data != null) {
            System.out.println(toString(data));
        }
    }
    
    /**
     * Main method for testing Hamming
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("\nTESTING 1011 --- Encoded: 01100110");
        printData(encode(toArray("1011")));
        printData(decode(toArray("01100110"))); //Correct One
        printData(decode(toArray("11100110"))); //Has One Error
        printData(decode(toArray("00100110"))); //Has One Error
        printData(decode(toArray("01000110"))); //Has One Error
        printData(decode(toArray("01110110"))); //Has One Error
        printData(decode(toArray("01101110"))); //Has One Error
        printData(decode(toArray("01100010"))); //Has One Error
        printData(decode(toArray("01100100"))); //Has One Error
        printData(decode(toArray("01100111"))); //Has One Error
        printData(decode(toArray("00000110"))); //Has Two Errors
        printData(decode(toArray("10100110"))); //Has Two Errors
        printData(decode(toArray("01100000"))); //Has Two Errors
        printData(decode(toArray("01100011"))); //Has Two Errors
        printData(decode(toArray("01000111"))); //Has Two Errors

        System.out.println("\nTESTING 0101 --- Encoded: 01001011");
        printData(encode(toArray("0101")));
        printData(decode(toArray("01001011")));
        printData(decode(toArray("11001011")));
        printData(decode(toArray("10001011")));
    }
}
