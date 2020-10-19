package com.sevenchip.charger.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

/**
 * 处理字节的常用工具类方法
 *
 * @author : Alvin
 * create at : 2020/8/6 17:59
 * description :
 */
public class ByteUtils {

    /**
     * 构造新字节时需要与的值表
     */
    private static final byte[] BUILD_BYTE_TABLE = new byte[]{(byte) 128, (byte) 64, (byte) 32, (byte) 16, (byte) 8, (byte) 4, (byte) 2, (byte) 1};

    private static final String[] BINARY_ARRAY = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};

    private ByteUtils() {
    }

    public static byte[] capture(byte[] src, int begin, int length) {
        byte[] bs = new byte[length];
        System.arraycopy(src, begin, bs, 0, length);
        return bs;
    }

    /**
     * short转换到字节数组
     *
     * @param number 需要转换的数据。
     * @return 转换后的字节数组。
     */
    public static byte[] shortToByte(short number) {
        byte[] b = new byte[2];
        for (int i = 1; i >= 0; i--) {
            b[i] = (byte) (number % 256);
            number >>= 8;
        }
        return b;
    }

    /**
     * 字节到short转换
     *
     * @param b short的字节数组
     * @return short数值。
     */
    public static short byteToShort(byte[] b) {
        return (short) ((((b[0] & 0xff) << 8) | b[1] & 0xff));
    }

    /**
     * 整型转换到字节数组
     *
     * @param number 整形数据。
     * @return 整形数据的字节数组。
     */
    public static byte[] intToByte(int number) {
        byte[] b = new byte[4];
        for (int i = 3; i >= 0; i--) {
            b[i] = (byte) (number % 256);
            number >>= 8;
        }
        return b;
    }

    /**
     * 字节数组到整型转换
     *
     * @param b 整形数据的字节数组。
     * @return 字节数组转换成的整形数据。
     */
    public static int byteToInt(byte[] b) {
        return ((((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16) | ((b[2] & 0xff) << 8) | (b[3] & 0xff)));
    }

    /**
     * long转换到字节数组
     *
     * @param number 长整形数据。
     * @return 长整形转换成的字节数组。
     */
    public static byte[] longToByte(long number) {
        byte[] b = new byte[8];
        for (int i = 7; i >= 0; i--) {
            b[i] = (byte) (number % 256);
            number >>= 8;
        }
        return b;
    }

    /**
     * 字节数组到整型的转换
     *
     * @param b 长整形字节数组。
     * @return 长整形数据。
     */
    public static long byteToLong(byte[] b) {
        return ((((long) b[0] & 0xff) << 56) | (((long) b[1] & 0xff) << 48) | (((long) b[2] & 0xff) << 40) | (((long) b[3] & 0xff) << 32) | (((long) b[4] & 0xff) << 24)
                | (((long) b[5] & 0xff) << 16) | (((long) b[6] & 0xff) << 8) | ((long) b[7] & 0xff));
    }

    /**
     * double转换到字节数组
     *
     * @param d 双精度浮点。
     * @return 双精度浮点的字节数组。
     */
    public static byte[] doubleToByte(double d) {
        byte[] bytes = new byte[8];
        long l = Double.doubleToLongBits(d);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = Long.valueOf(l).byteValue();
            l = l >> 8;
        }
        return bytes;
    }

    /**
     * 字节数组到double转换
     *
     * @param b 双精度浮点字节数组。
     * @return 双精度浮点数据。
     */
    public static double byteToDouble(byte[] b) {
        long l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);
        l &= 0xffffffffL;
        l |= ((long) b[4] << 32);
        l &= 0xffffffffffL;

        l |= ((long) b[5] << 40);
        l &= 0xffffffffffffL;
        l |= ((long) b[6] << 48);
        l &= 0xffffffffffffffL;

        l |= ((long) b[7] << 56);

        return Double.longBitsToDouble(l);
    }

    /**
     * float转换到字节数组
     *
     * @param d 浮点型数据。
     * @return 浮点型数据转换后的字节数组。
     */
    public static byte[] floatToByte(float d) {
        byte[] bytes = new byte[4];
        int l = Float.floatToIntBits(d);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = Integer.valueOf(l).byteValue();
            l = l >> 8;
        }
        return bytes;
    }

    /**
     * 字节数组到float的转换
     *
     * @param b 浮点型数据字节数组。
     * @return 浮点型数据。
     */
    public static float byteToFloat(byte[] b) {
        int l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);
        l &= 0xffffffffL;

        return Float.intBitsToFloat(l);
    }

    /**
     * 字符串到字节数组转换
     *
     * @param s       字符串。
     * @param charset 字符编码
     * @return 字符串按相应字符编码编码后的字节数组。
     */
    public static byte[] stringToByte(String s, Charset charset) {
        return s.getBytes(charset);
    }

    /**
     * 高低位转换
     *
     * @param data
     * @return
     */
    public static byte[] byteArrayReverse(byte[] data) {
        for (int i = 0; i < data.length / 2; i++) {
            byte temp = data[i];
            data[i] = data[data.length - 1 - i];
            data[data.length - 1 - i] = temp;
        }
        return data;
    }

    /**
     * byte数组转成16进制字符串
     *
     * @param data 转换的数组
     * @return 16进制的字符串表示形式(e.g : 6E0100048F)
     */
    public static String byteArray2String(byte[] data) {
        StringBuilder buffer = new StringBuilder();
        for (byte datum : data) {
            String str = Integer.toHexString(datum & 0xFF);
            if (str.length() < 2) {
                buffer.append(0);
            }
            buffer.append(str.toUpperCase(Locale.getDefault()));
        }
        return buffer.toString();
    }

    /**
     * 将byte数组转换为16进制字符串
     *
     * @param data 转换的数组
     * @param len  数组长度
     * @return 16进制的字符串表示形式(e.g : 0x6E 0x01 0x00 0x04 0x8F)
     */
    public static String byteArray2HexStringWithSpaces(byte[] data, int len) {
        Formatter formatter = new Formatter();
        byte[] buf = new byte[len];
        System.arraycopy(data, 0, buf, 0, len);
        int i;
        for (i = 0; i < len; i++) {
            formatter.format("0x%02X ", buf[i]);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * 将byte数组转换为16进制字符串
     *
     * @param data 转换的数组
     * @param len  数组长度
     * @return 16进制的字符串表示形式(e.g : 0x6E0x010x000x040x8F)
     */
    public static String byteArray2HexString(byte[] data, int len) {
        Formatter formatter = new Formatter();
        byte[] buf = new byte[len];
        System.arraycopy(data, 0, buf, 0, len);
        int i;
        for (i = 0; i < len; i++) {
            formatter.format("0x%02X", buf[i]);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * byte数组转换二进制字符串
     *
     * @param data
     * @return 二进制的字符串表示形式(e.g : 1000111101100110000010001100110000011010)
     */
    public static String byteArray2BinaryString(byte[] data) {
        StringBuilder binaryStr = new StringBuilder();
        int pos;
        for (byte b : data) {
            //高四位
            pos = (b & 0xF0) >> 4;
            binaryStr.append(BINARY_ARRAY[pos]);
            //低四位
            pos = b & 0x0F;
            binaryStr.append(BINARY_ARRAY[pos]);
        }
        return binaryStr.toString();
    }


    /**
     * 把字符串去空格后转换成byte数组(e.g: "37 5a"-->{0x37, 0x5A})
     *
     * @param str 转换的字符串
     * @return
     */
    public static byte[] string2ByteArray(String str) {
        String s = str.replace(" ", "");
        int string_len = s.length();
        int len = string_len / 2;
        if (string_len % 2 == 1) {
            s = "0" + s;
            string_len++;
            len++;
        }
        byte[] data = new byte[len];
        for (int i = 0; i < len; i++) {
            data[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return data;
    }

    /**
     * 二进制字符串转换byte数组(e.g: "0111111"-->{0x7F} or "0111111,0111111"-->{0x7F, 0x7F})
     *
     * @param binaryStr 二进制字符串(e.g: "0111111" or "0111111,0111111")
     * @return
     */
    public static byte[] binaryString2ByteArrayByDot(String binaryStr) {
        //假设binaryByte 是01，10，011，00以，分隔的格式的字符串
        String[] binaryStrs = binaryStr.split(",");
        byte[] data = new byte[binaryStrs.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) parseInt(binaryStrs[i]);
        }
        int length = binaryStr.length();
        if (length % 8 == 0) {
            String[] bs = new String[length / 8];
            for (int i = 0; i < length / 8; i++) {
                bs[i] = binaryStr.substring(i * 8, i * 8 + 8);
            }
        }


        return data;
    }

    /**
     * 二进制字符串转换byte数组(e.g: "0111111"-->{0x7F} or "0111111,0111111"-->{0x7F, 0x7F})
     *
     * @param binaryStr 二进制字符串(e.g: "0111111" or "0111111,0111111")
     * @return
     */
    public static byte[] binaryString2ByteArray(String binaryStr) {
        int length = binaryStr.length();
        if (length % 8 == 0) {
            int byteLength = length / 8;
            byte[] data = new byte[byteLength];
            for (int i = 0; i < byteLength; i++) {
                data[i] = (byte) parseInt(binaryStr.substring(i * 8, i * 8 + 8));
            }
            return data;
        }
        return null;
    }

    private static int parseInt(String str) {
        //32位 为负数
        if (32 == str.length()) {
            str = "-" + str.substring(1);
            return -(Integer.parseInt(str, 2) + Integer.MAX_VALUE + 1);
        }
        return Integer.parseInt(str, 2);
    }


    /**
     * int转byte[]，低位在前，高位在后
     *
     * @param i 转换的int
     * @return data
     */
    public static byte[] int2ByteArrayLSB(int i) {
        byte b4 = (byte) ((i) >> 24);
        byte b3 = (byte) (((i) << 8) >> 24);
        byte b2 = (byte) (((i) << 16) >> 24);
        byte b1 = (byte) (((i) << 24) >> 24);
        return new byte[]{b1, b2, b3, b4};

    }

    /**
     * int转byte[]，低位在前，高位在后
     *
     * @param i 转换的int
     * @return data
     */
    public static byte[] int2ByteArrayLSB2(int i) {
        byte[] data = new byte[2];
        data[0] = (byte) (i & 0xFF);           // Least significant "byte"
        data[1] = (byte) ((i & 0xFF00) >> 8);  // Most significant "byte"
        return data;
    }

    /**
     * int转byte[]，高位在前，低位在后
     *
     * @param i 转换的int
     * @return data
     */
    public static byte[] int2ByteArrayMSB2(int i) {
        byte[] data = new byte[2];
        data[0] = (byte) ((i & 0xFF00) >> 8);
        data[1] = (byte) (i & 0xFF);
        return data;
    }


    /**
     * int转byte数组
     *
     * @param i
     * @param byteOrder 大小端排列顺序(e.g: ByteOrder.LITTLE_ENDIAN)
     * @return
     */
    public static byte[] int2ByteArray(int i, ByteOrder byteOrder) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.order(byteOrder); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
        b.putInt(i);
        return b.array();
    }

    /**
     * 从数组里，指定位置转换出一个int(包括1~4字节)
     *
     * @param data        byte[]
     * @param beginPos    int
     * @param len         int
     * @param isLowEndian boolean true 低位在前，高位在后, false 高位在前，低位在后
     * @return int
     */
    public static int byteArray2Int(byte[] data, int beginPos, int len, boolean isLowEndian) {
        if (beginPos > data.length - len) {
            return 0;
        }
        int aInt = 0;
        int tmpInt;
        for (int i = 0; i < len; i++) {
            tmpInt = data[i + beginPos];
            if (tmpInt < 0) {
                tmpInt += 256;
            }
            if (isLowEndian) {
                tmpInt = tmpInt << (i * 8);
            } else {
                tmpInt = tmpInt << ((len - i - 1) * 8);
            }
            aInt |= tmpInt;
        }
        return aInt;
    }

    /**
     * 2进制字符串转16进制字符串(e.g: "01111111"-->"7F")
     *
     * @param bStr 二进制字符串(e.g: "01111111")
     * @return
     */
    public static String binaryString2hexString(String bStr) {
        if (bStr == null || bStr.equals("") || bStr.length() % 8 != 0) {
            return null;
        }
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bStr.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bStr.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp).toUpperCase());
        }
        return tmp.toString();
    }

    /**
     * 16进制字符串转2进制字符串(e.g: "FF"-->"01111111")
     *
     * @param hexStr 16进制字符串(e.g: "FF")
     * @return
     */
    public static String hexString2BinaryString(String hexStr) {
        if (hexStr == null || hexStr.length() % 2 != 0) {
            return null;
        }
        String bString = "", tmp;
        for (int i = 0; i < hexStr.length(); i++) {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexStr
                    .substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /**
     * 字节数组带字符串的转换
     *
     * @param b       字符串按指定编码转换的字节数组。
     * @param charset 字符编码。
     * @return 字符串。
     */
    public static String byteToString(byte[] b, Charset charset) {
        return new String(b, charset);
    }

    /**
     * 对象转换成字节数组。
     *
     * @param obj 字节数组。
     * @return 对象实例相应的序列化后的字节数组。
     * @throws IOException
     */
    public static byte[] objectToByte(Object obj) throws IOException {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(buff);
        out.writeObject(obj);
        try {
            return buff.toByteArray();
        } finally {
            out.close();
        }
    }

    /**
     * 序死化字节数组转换成实际对象。
     *
     * @param b 字节数组。
     * @return 对象。
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object byteToObject(byte[] b) throws IOException, ClassNotFoundException {
        ByteArrayInputStream buff = new ByteArrayInputStream(b);
        ObjectInputStream in = new ObjectInputStream(buff);
        Object obj = in.readObject();
        try {
            return obj;
        } finally {
            in.close();
        }
    }

    /**
     * 比较两个字节的每一个bit位是否相等.
     *
     * @param a 比较的字节.
     * @param b 比较的字节
     * @return ture 两个字节每一位都相等,false有至少一位不相等.
     */
    public static boolean equalsBit(byte a, byte b) {
        return Arrays.equals(byteToBitArray(a), byteToBitArray(b));
    }

    /**
     * 比较两个数组中的每一个字节,两个字节必须二进制字节码每一位都相同才表示两个 byte相同.
     *
     * @param a 比较的字节数组.
     * @param b 被比较的字节数.
     * @return ture每一个元素的每一位两个数组都是相等的, false至少有一位不相等.
     */
    public static boolean equalsBit(byte[] a, byte[] b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }

        int length = a.length;
        if (b.length != length) {
            return false;
        }

        for (int count = 0; count < a.length; count++) {
            if (!equalsBit(a[count], b[count])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 返回某个字节的bit组成的字符串.
     *
     * @param b 字节.
     * @return Bit位组成的字符串.
     */
    public static String bitString(byte b) {
        StringBuilder buff = new StringBuilder();
        boolean[] array = byteToBitArray(b);
        for (int i = 0; i < array.length; i++) {
            buff.append(array[i] ? 1 : 0);
        }
        return buff.toString();
    }

    /**
     * 计算出给定byte中的每一位,并以一个布尔数组返回. true表示为1,false表示为0.
     *
     * @param b 字节.
     * @return 指定字节的每一位bit组成的数组.
     */
    public static boolean[] byteToBitArray(byte b) {
        boolean[] buff = new boolean[8];
        int index = 0;
        for (int i = 7; i >= 0; i--) {
            buff[index++] = ((b >>> i) & 1) == 1;
        }
        return buff;
    }

    /**
     * 返回指定字节中指定bit位,true为1,false为0. 指定的位从0-7,超出将抛出数据越界异常.
     *
     * @param b     需要判断的字节.
     * @param index 字节中指定位.
     * @return 指定位的值.
     */
    public static boolean byteBitValue(byte b, int index) {
        return byteToBitArray(b)[index];
    }

    /**
     * 根据布尔数组表示的二进制构造一个新的字节.
     *
     * @param values 布尔数组,其中true表示为1,false表示为0.
     * @return 构造的新字节.
     */
    public static byte buildNewByte(boolean[] values) {
        byte b = 0;
        for (int i = 0; i < 8; i++) {
            if (values[i]) {
                b |= BUILD_BYTE_TABLE[i];
            }
        }
        return b;
    }

    /**
     * 将指定字节中的某个bit位替换成指定的值,true代表1,false代表0.
     *
     * @param b        需要被替换的字节.
     * @param index    位的序号,从0开始.超过7将抛出越界异常.
     * @param newValue 新的值.
     * @return 替换好某个位值的新字节.
     */
    public static byte changeByteBitValue(byte b, int index, boolean newValue) {
        boolean[] bitValues = byteToBitArray(b);
        bitValues[index] = newValue;
        return buildNewByte(bitValues);
    }

    /**
     * 将指定的IP地址转换成字节表示方式. IP数组的每一个数字都不能大于255,否则将抛出IllegalArgumentException异常.
     *
     * @param address IP地址数组.
     * @return IP地址字节表示方式.
     */
    public static byte[] ipAddressBytes(String address) {
        if (address == null || address.length() > 15) {
            throw new IllegalArgumentException("Invalid IP address.");
        }

        final int ipSize = 4;// 最大IP位数
        final char ipSpace = '.';// IP数字的分隔符
        int[] ipNums = new int[ipSize];
        StringBuilder number = new StringBuilder();// 当前操作的数字
        StringBuilder buff = new StringBuilder(address);
        int point = 0;// 当前操作的数字下标,最大到3.
        char currentChar;
        for (int i = 0; i < buff.length(); i++) {
            currentChar = buff.charAt(i);
            if (ipSpace == currentChar) {
                // 当前位置等于最大于序号后,还有字符没有处理表示这是一个错误的IP.
                if (point == ipSize - 1 && buff.length() - (i + 1) > 0) {
                    throw new IllegalArgumentException("Invalid IP address.");
                }
                ipNums[point++] = Integer.parseInt(number.toString());
                number.delete(0, number.length());
            } else {
                number.append(currentChar);
            }
        }
        ipNums[point] = Integer.parseInt(number.toString());

        byte[] ipBuff = new byte[ipSize];
        int pointNum = 0;
        for (int i = 0; i < 4; i++) {
            pointNum = Math.abs(ipNums[i]);
            if (pointNum > 255) {
                throw new IllegalArgumentException("Invalid IP address.");
            }
            ipBuff[i] = (byte) (pointNum & 0xff);
        }

        return ipBuff;
    }

    public static String byteArray2ASSCLL(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            char c = (char) b;
            sb.append(c);
        }
        return sb.toString();
    }

    public static byte[] asscllToByteArray(String data) {
        byte[] bytes = new byte[data.length()];
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            bytes[i]= (byte) c;
        }
        return bytes;
    }
}
