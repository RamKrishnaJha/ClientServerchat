import java.util.*;
import java.util.stream.Collectors;
import java.net.*;
import java.io.*;
import java.math.*;

public class client {
 static BigInteger p, q, exp, d, num, phi;
 static int bitLength = 256;
 static Random R = new Random();

 public static void main(String args[]) {
  p = BigInteger.probablePrime(bitLength, R);
  q = BigInteger.probablePrime(bitLength, R);
  num = p.multiply(q);
  exp = BigInteger.probablePrime(bitLength / 2, R);
  phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
  while (phi.gcd(exp).compareTo(BigInteger.ONE) != 0 && exp.compareTo(phi) < 0)
   exp.add(BigInteger.ONE);
  d = exp.modInverse(phi);
  List<BigInteger> i = new ArrayList<>();
  i.add(num);
  i.add(exp);
  try {
   String pub1 = i.stream().map(String::valueOf).collect(Collectors.joining(","));
   Scanner ser = new Scanner(System.in);
   Socket s = new Socket("localhost", 998);
   DataInputStream dis = new DataInputStream(s.getInputStream());
   DataOutputStream dos = new DataOutputStream(s.getOutputStream());
   System.out.println("Connected to server");
   dos.writeUTF(pub1);
   // System.out.println(dis.readUTF());
   String pub = dis.readUTF();
   String[] arr = pub.split(",");
   BigInteger n = new BigInteger(arr[0]);
   BigInteger e = new BigInteger(arr[1]);
   // System.out.println(n);
   // System.out.println(e);
   int done = 1;
   while (done != 0) {
    System.out.println("Enter 1 to continue 0 to quit");
    done = ser.nextInt();
    if (done != 0) {
     System.out.println("\n enter message  ");
     ser.nextLine();
     String msg = ser.nextLine();
     byte msg_arr[] = msg.getBytes();
     byte en[] = encrypt(msg_arr, e, n);
     dos.writeInt(en.length);
     dos.write(en);
     int length = dis.readInt();

     if (length > 0) {
      byte[] enc = new byte[length];
      dis.readFully(enc, 0, enc.length);
      System.out.println("Encrypted Byte Array : " + display(enc));
      byte de[] = decrypt(enc);
      System.out.println("Received Msg : " + new String(de));
     }
    }
   }
   dis.close();
   dos.close();
   s.close();
   ser.close();
  } catch (IOException e) {
   System.out.println("IO: " + e.getMessage());
  }
 }

 static byte[] encrypt(byte a[], BigInteger e, BigInteger n) {
  return (new BigInteger(a).modPow(e, n)).toByteArray();
 }

 static byte[] decrypt(byte a[]) {
  return (new BigInteger(a).modPow(d, num)).toByteArray();
 }

 static String display(byte a[]) {
  String s = "";
  for (int i = 0; i < a.length; i++)
   s += Byte.toString(a[i]);
  return s;
 }
}
