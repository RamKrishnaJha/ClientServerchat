import java.util.*;
import java.util.stream.Collectors;
import java.net.*;
import java.io.*;
import java.math.*;

public class server {
 static BigInteger p, q, e, d, n, phi;
 static int bitLength = 256;
 static Random R = new Random();

 public static void main(String args[]) {
  p = BigInteger.probablePrime(bitLength, R);
  q = BigInteger.probablePrime(bitLength, R);
  n = p.multiply(q);
  e = BigInteger.probablePrime(bitLength / 2, R);
  phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
  while (phi.gcd(e).compareTo(BigInteger.ONE) != 0 && e.compareTo(phi) < 0)
   e.add(BigInteger.ONE);
  d = e.modInverse(phi);
  List<BigInteger> i = new ArrayList<>();
  i.add(n);
  i.add(e);
  try {
   String pub = i.stream().map(String::valueOf).collect(Collectors.joining(","));
   Scanner scr = new Scanner(System.in);
   ServerSocket s = new ServerSocket(998);
   System.out.println("server ready \n waiting for connection \n");
   Socket s1 = s.accept();
   DataOutputStream dos = new DataOutputStream(s1.getOutputStream());
   DataInputStream dis = new DataInputStream(s1.getInputStream());
   System.out.println("Connected to 127.0.0.1");
   dos.writeUTF(pub);
   String pub1 = dis.readUTF();
   String[] arr = pub1.split(",");
   BigInteger num = new BigInteger(arr[0]);
   BigInteger exp = new BigInteger(arr[1]);
   int done = 1;
   while (done != 0) {
    int length = dis.readInt();
    if (length > 0) {
     byte[] en = new byte[length];
     dis.readFully(en, 0, en.length);
     System.out.println("Encrypted Byte Array : " + display(en));
     byte de[] = decrypt(en);
     System.out.println("Received Msg : " + new String(de));
    }
    System.out.println("Enter 1 to continue 0 to quit");
    done = scr.nextInt();
    if (done != 0) {
     System.out.println("Enter message");
     scr.nextLine();
     String msg = scr.nextLine();
     byte msg_arr[] = msg.getBytes();
     byte en[] = encrypt(msg_arr, exp, num);
     dos.writeInt(en.length);
     dos.write(en);
    }
   }
   dos.close();
   s1.close();
   scr.close();
   s.close();

  } catch (IOException e) {
   System.out.println("IO: " + e.getMessage());
  } finally {
   System.out.println("\n connection terminated");
  }
 }

 static byte[] encrypt(byte a[], BigInteger exp, BigInteger num) {
  return (new BigInteger(a).modPow(exp, num)).toByteArray();
 }

 static byte[] decrypt(byte a[]) {
  return (new BigInteger(a).modPow(d, n)).toByteArray();
 }

 static String display(byte a[]) {
  String s = "";
  for (int i = 0; i < a.length; i++)
   s += Byte.toString(a[i]);
  return s;
 }
}