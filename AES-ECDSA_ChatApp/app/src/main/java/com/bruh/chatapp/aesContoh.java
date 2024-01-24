package com.bruh.chatapp;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class aesContoh{
	
	static char[] input; //Penampung input
	static int[][] output; //Penampung output
	static char[] secret; //Penampung secret key
	static int[][] state; //Penampung status
	static int[][] rahasia; //Penampung konversi secret key ke int
	static int[][] s_box; //Penampung sbox
	static int[][] invs_box; //Penampung inverse s-box
	static int nb, nr,nk; //Penampung nb dan nr
    static byte[][] Kunci;
    //Isi S-Box
	public static int[] sbox = {0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
        0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
        0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
        0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
        0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
        0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
        0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
        0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
        0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
        0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
        0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
        0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
        0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
        0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
        0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
        0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16};

//Isi Inverse S-Box
public static int[] invsbox = {0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb, 
	0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb, 
	0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e, 
0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25, 
0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92, 
0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84, 
0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06,
 0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b, 
 0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73, 
 0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e, 
 0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b, 
 0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4, 
 0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f, 
 0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef, 
 0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61, 
0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d};


//Isi RCon untuk Key Expansion
 public static final int[] rcon = {0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a,
        0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39,
        0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a,
        0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8,
        0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef,
        0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc,
        0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b,
        0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3,
        0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94,
        0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20,
        0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35,
        0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f,
        0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04,
        0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63,
        0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd,
        0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb};

/*Prosedur enkripsi*/
	public static byte[] EncryptBlock(byte[] in, byte[][] password){

       
        byte[] out = new byte[in.length];
        //inisiasi state
		byte[][] state = new byte[4][nb];
		
        for (int i = 0; i<4;i++ ) {
                for(int j=0;j<nb;j++){
                    state[i][j] = in[i+j*4];
                }
        }

    	System.out.printf("Sesudah:\n");
        state = AddRoundKey(state,password,0); //Proses AddRoundKey pertama

        //Looping Standard Round
		int round; //nr-1
		 for(round=1;round<nr;round++){
		    state = ByteSub(state);
        	state = ShiftRow(state);
			state = MixColumn(state);
            state = AddRoundKey(state,password,round);
		 }

  //       //Proses final round
		    state = ByteSub(state);
		    state = ShiftRow(state);
           state = AddRoundKey(state,password,nr);


        //Memasukkan isi state ke dalam output
		for(int i=0;i<4;i++){
		    for(int j=0;j<nb;j++){
                out[i+j*4] = state[i][j];
            }
        }

        return out;
	}


    //Proses AddRoundKey
	 private static byte[][] AddRoundKey(byte[][] state, byte[][] secret, int round){

        byte[][] temp = new byte[state.length][state[0].length];

        for(int j=0;j<nb;j++){
            for(int i=0;i<state[j].length;i++){
                 temp[j][i] = (byte) (state[j][i] ^ secret[round*nb+j][i]);
            }
        }

        Log.d("Add Round Key:", Arrays.deepToString(temp));
        return temp;
	 }


     //Proses SubByte
	private static  byte[][] ByteSub(byte[][] state){

         byte[][] temp = new byte[state.length][state[0].length];

    	for(int i=0;i<4;i++){
    		for(int j=0;j<nb;j++){
    			int plot = state[i][j];
    			temp[i][j] = (byte) (sbox[plot & 0x000000ff] & 0xff);
    		}
    	}

        Log.d("Substitution Bytes:", Arrays.deepToString(temp));
        return temp;

	}

    //Proses MixColumn
	private static byte[][] MixColumn(byte[][] state){
    		int[] MixColumnMatrix = new int[4];
    		byte b2 = (byte) 0x02, b3 = (byte)0x03;
    		for(int j = 0;j<4;j++){
    			MixColumnMatrix[0] = Gmul(b2,state[0][j]) ^ Gmul(b3,state[1][j]) ^ state[2][j] ^ state[3][j];
    			MixColumnMatrix[1] = state[0][j] ^ Gmul(b2, state[1][j]) ^ Gmul(b3, state[2][j]) ^ state[3][j];
    			MixColumnMatrix[2] = state[0][j] ^ state[1][j] ^ Gmul(b2,state[2][j]) ^ Gmul(b3,state[3][j]);
    			MixColumnMatrix[3] = Gmul(b3,state[0][j]) ^ state[1][j] ^ state[2][j] ^ Gmul(b2, state[3][j]);

    			for(int i=0;i<4;i++){
    				state[i][j] = (byte)(MixColumnMatrix[i]);
    			}

    		}

        Log.d("Mix Column:", Arrays.deepToString(state));
            return state;

	}

    //Proses perkalian matriks
   public static byte Gmul(byte a, byte b) {
       byte aa = a, bb = b, r=0, t;
        while(aa !=00){
            if((aa&1) != 0){
                r = (byte)(r^bb);
            }
            t = (byte)(bb & 0x80);
            bb = (byte) (bb << 1);
            if(t != 0 ){
                bb = (byte)(bb ^ 0x1b);
            }
            aa = (byte)((aa & 0xff) >> 1);
        }
        Log.d("Galois Multiplication", String.valueOf(r));
        return r;
    }



    //Proses ShiftRow
	private static byte[][] ShiftRow(byte[][] state){

        byte[] t = new byte[4];
    	for(int i=0;i<4;i++){
    		for(int j=0;j<nb;j++){
    			t[j] = state[i][(j+i)%nb];
    		}
            for(int j=0;j<nb;j++){
                state[i][j] = t[j];
            }
    	}
        Log.d("Shift Row:", Arrays.deepToString(state));
        return state;
	}


    /*Proses Ekspansi Kunci*/
	private static byte[][] KeyExpansion(byte secret[]){
	   byte[][] w = new byte[nb*(nr+1)][4];
       //byte[] temp = new byte[4];
		int i=0;
		while(i<nk){
			w[i][0] = secret[i*4];
            w[i][1] = secret[i*4+1];
            w[i][2] = secret[i*4+2];
            w[i][3] = secret[i*4+3];

			i++;
		}

		i = nk;
		while(i<nb*(nr+1)){
            byte[] temp = new byte[4];
            for(int k=0;k<4;k++){
                temp[k] = w[i-1][k];
            }
			if(i%nk==0){
				temp =  SubWord(RotWord(temp));
                temp[0] = (byte) (temp[0]  ^ rcon[i/nk] &0xff);
			}else if((nk > 6) && (i%nk==4)){
				temp = SubWord(temp);
			}
			w[i] = xor_func(w[i-nk],temp);
			i++;
		}

        Log.d("Key Expansion:", Arrays.deepToString(w));
        return w;

	}


	public static byte[] SubWord(byte[] w){
		byte[] aftersub = new byte[w.length];
		for(int i=0;i<aftersub.length;i++){
			aftersub[i] = (byte) (sbox[w[i] & 0x000000ff] & 0xff);
		}

        Log.d("SubWord:", Arrays.toString(aftersub));
		return aftersub;
	}

	public static byte[] RotWord(byte[] w){
		byte[] afterRot = new byte[w.length];
		for(int i=0;i<3;i++){
			afterRot[i] = w[i+1];
		}
		afterRot[3] = w[0];
        Log.d("RotWord:", Arrays.toString(afterRot));
		return afterRot;
	}


	

	public static byte[] DecryptBlocks(byte[] in, byte[][] password){
		byte[] out = new byte[in.length];
        byte[][] state = new byte[4][nb];
		for (int i = 0; i<4;i++ ) {
		    for(int j=0;j<nb;j++){
                state[i][j] = in[i+j*4];
            }
        }

    	state = AddRoundKey(state,password,nr);
    	int round;
    	for(round = nr-1;round >= 1;round--){
    		state = InvShiftRows(state);
    		state = InvSubBytes(state);
    		state = AddRoundKey(state,password,round);
    		state = InvMixColumns(state);
    	}
    	state = InvShiftRows(state);
    	state = InvSubBytes(state);
    	state = AddRoundKey(state,password,0);


        for(int i=0;i<4;i++){
            for(int j=0;j<nb;j++){
                out[i+j*4] = state[i][j];
            }
        }

        Log.d("Decrypt Blocks:", Arrays.toString(out));
        return out;
	}


    private static byte[] xor_func(byte[] a, byte[] b) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ b[i]);
        }
        return out;

    }

	private static byte[][] InvShiftRows(byte[][] state){
       byte[] t = new byte[4];
        
        for(int i=0;i<4;i++){
            for(int j=0;j<nb;j++){
                t[(j+i)%nb] = state[i][j];
            }
            for(int j=0;j<nb;j++){
                state[i][j] = t[j];
            }
        }

        Log.d("Inverse Shift Rows:", Arrays.deepToString(state));
        return state;
	}

	private static byte[][] InvSubBytes(byte[][] state){

    	for(int i=0;i<4;i++){
    		for(int j=0;j<nb;j++){
    			byte plot = state[i][j];
    			state[i][j] = (byte) (invsbox[plot & 0x000000ff] & 0xff);
    		}
    	}

        Log.d("Inverse Sub Bytes:", Arrays.deepToString(state));
        return state;

	}

	private static byte[][] InvMixColumns(byte[][] state){
		int[] InvMixColumns = new int[4];
		byte be = (byte) 0x0e, bb = (byte)0x0b, bd = (byte)0x0d,b9 = (byte)0x09;
		for(int j =0;j<4;j++ ){
			InvMixColumns[0] = Gmul(be,state[0][j]) ^ Gmul(bb,state[1][j]) ^ Gmul(bd,state[2][j]) ^ Gmul(b9,state[3][j]); 
			InvMixColumns[1] = Gmul(b9,state[0][j]) ^ Gmul(be,state[1][j]) ^ Gmul(bb,state[2][j]) ^ Gmul(bd,state[3][j]);
			InvMixColumns[2] = Gmul(bd,state[0][j]) ^ Gmul(b9,state[1][j]) ^ Gmul(be,state[2][j]) ^ Gmul(bb,state[3][j]);
			InvMixColumns[3] = Gmul(bb,state[0][j]) ^ Gmul(bd,state[1][j]) ^ Gmul(b9,state[2][j]) ^ Gmul(be,state[3][j]);

			for(int i=0;i<4;i++){
				state[i][j] = (byte)(InvMixColumns[i]);
			}
		}

        Log.d("Inverse Mix Column:", Arrays.deepToString(state));
        return state;
	}

    public static byte[] Encrypt(byte[] in, byte[] secret){
        nb = 4;
        nk = secret.length/4;
        nr = nk+6;
        int length = 0;
        byte[] padding = new byte[1];
        int i;
        length = 16 - in.length % 16;


        padding = new byte[length];
        padding[0] = (byte) 0x80;
        for(i=1 ; i < length;i++){
            padding[i] = 0;
        }

        byte[] temp = new byte[in.length +length];
        byte[] blocks = new byte[16];

        Kunci = KeyExpansion(secret);
        int hitung = 0;
        for(i=0;i<in.length + length; i++){
            if(i>0 && i%16 == 0){
                blocks = EncryptBlock(blocks,Kunci);
                System.arraycopy(blocks,0,temp,i-16,blocks.length);
            }
            if(i < in.length){
                blocks[i%16] = in[i];
            }else{
                blocks[i%16] = padding[hitung%16];
                hitung++;
            }
        }
        if(blocks.length == 16){
            blocks = EncryptBlock(blocks,Kunci);
            System.arraycopy(blocks,0,temp,i-16,blocks.length);
        }

        return temp;
    }

    public static byte[] Decrypt(byte[] in, byte[] secret){ //Prosedur dekripsi encrypted text
        int i;
        byte[] temp = new byte[in.length];
        byte[] blocks = new byte[16];  
        nb = 4;
        nk = secret.length/4;
        nr = nk+6;
        Kunci = KeyExpansion(secret); //Mengambil Kunci sesi dari Key Expansion

        for(i=0;i<in.length;i++){
            if(i > 0 && i %16==0){
                blocks = DecryptBlocks(blocks,Kunci); //Dekripsi text per blok dengan kunci sesi
                 System.arraycopy(blocks,0,temp,i-16,blocks.length); //Isi temp dengan blok terdeskripsi
            }
            if(i < in.length){
                blocks[i%16] = in[i];
            }

        }
        blocks = DecryptBlocks(blocks,Kunci); //Melakukan dekripsi text untuk blok terakhir
        System.arraycopy(blocks,0,temp,i-16,blocks.length);
        Log.d("temp isi: ", Arrays.toString(temp));
        temp = DeletePadding(temp);
        return temp;
    }


    //Generasi kunci
    public static byte[] KeyGen(){
        String secret = new String();
	    byte[] sec = new byte[secret.length()];
        try{
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);
            SecretKey secretKey = keygen.generateKey();
            sec = secretKey.getEncoded();
        }catch (NoSuchAlgorithmException e){
	        e.printStackTrace();
        }
        return sec;
    }

    //Hapus padding
    public static byte[] DeletePadding(byte[] in){
        int count = 0;
        int i = in.length - 1;
        while(in[i] == 0){
            count++;
            i--;
        }

        byte[] temp = new byte[in.length-count-1];
        System.arraycopy(in,0,temp,0,temp.length);
        Log.d("del padding:", Arrays.toString(temp));
        return temp;
    }
}