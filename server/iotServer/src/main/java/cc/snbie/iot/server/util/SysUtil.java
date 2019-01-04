package cc.snbie.iot.server.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.util.UUID;

public class SysUtil {

    public static String intArrayToHexString(int array[]){
        StringBuffer stringBuffer = new StringBuffer();
        for(int a : array){
            stringBuffer.append(intToHexString(a));
        }
        return  stringBuffer.toString();
    }

    public static String createId(){
       String id=uuid().replaceAll("-","");
        id= DigestUtils.md5Hex(id.getBytes()).substring(8,24).toUpperCase();
        return id;
    }

    public static String uuid(){
        return (UUID.randomUUID().toString());
    }


    public static String isValidHexString(String hexString){
        //isValidHexString 检查是否是有效的16进制字符串,如果是非法则返回“”，否则返回去掉空格的16进制字符串
        //00020101 , 00 02 01 01 02    02 00 ,都是合法的
        //0002010G,00 021 2 00 00,0101025都是非法的。
        //规则是一个16进制数由2个连续的有效字符组成（0-9,a-f),16进制数之间可以有一个或多个空格，或没有空格
        String tmp_str,return_string="";
        hexString = hexString.toUpperCase();
        hexString = hexString.trim();
        while (hexString.length()>=2)
        {
            tmp_str=hexString.substring(0,2);//从0到2
            try {
                Integer.parseInt(tmp_str, 16);
            } catch (NumberFormatException e) {
                return "";
            }
            hexString = hexString.substring(2);
            hexString = hexString.trim();
            return_string+=tmp_str;
        }
        if (hexString.length()==1)
            return "";//字符有单个的。

        return  return_string;
    }

    public static int[] hexStringToint(String[] hexString){
        int out[]=new int[hexString.length];
        for(int i=0;i<hexString.length;i++){
            //System.out.println(hexString[i]);
            out[i]=Integer.parseInt(hexString[i],16);
        }
        return  out;
    }

    public static String[] split(String msg, int num) {
        if(num==0){
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        int step=0;
        for(int i=0;i<msg.length();i++){
            stringBuffer.append(msg.charAt(i));
            step++;
            if(step==num){
                step=0;
                stringBuffer.append(",");
            }
        }
        return StringUtils.split(stringBuffer.toString(),",");
    }

    public static void main(String args[]){
        /*String[] oo=split("00020103D9AABB",2);
        for (String o: oo){
            System.out.println(o);
        }*/
        StringBuffer stringBuffer=new StringBuffer("0123456789");
        System.out.println(stringBuffer.indexOf("3",3));
        System.out.println(stringBuffer.indexOf("3",5));
        System.out.println(stringBuffer.toString().split(",")[0]);
    }

    public static String intToHexString(int num) {
        String hexStr=Integer.toHexString(num);
        if(hexStr.length()<2){
            hexStr="0"+hexStr;
        }
        return hexStr;
    }

    /**
     *
     * 修改日期
     * 使用Base64加密算法加密字符串
     *return
     */
    public static String encodeBase64(String plainText){
        byte[] b=plainText.getBytes();
        Base64 base64=new Base64();
        b=base64.encode(b);
        String s=new String(b);
        return s;
    }

    public static String decodeBase64(String encodeStr){
        byte[] b=encodeStr.getBytes();
        Base64 base64=new Base64();
        b=base64.decode(b);
        String s=new String(b);
        return s;
    }

    public static String reverse(String s) {
        StringBuffer stringBuffer=new StringBuffer();
        String[] spiltSeg= SysUtil.split(s, 2);
        for (int j=spiltSeg.length-1;j>=0;j-- ){
            stringBuffer.append(spiltSeg[j]);
        }
        s=stringBuffer.toString();
        return s;
    }

    public static int[] toIntArray(Integer[] v) {
        int s[]=new int[v.length];
        for(int i=0;i<v.length;i++){
            s[i]=v[i];
        }
        return s;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<bytes.length;i++){
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString();
    }

    /**
     * 把 192.168.1.1 这样的ip转换到16进制
     * @param ip
     * @return
     */
    public static String ipAddressToHex(String ip){
        String[] tp=ip.split("\\.");
        StringBuffer sb=new StringBuffer();
        for(String s : tp){
            sb.append(intToHexString(Integer.parseInt(s)));
        }
        return sb.toString();
    }

}
