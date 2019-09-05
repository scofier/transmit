package com.hebaibai.ctrt;

import com.hebaibai.ctrt.ext.sign.PICC_XM_testSign;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;

public class MainTest {

    public static void main(String[] args) {
        CrtrUtils.SIGN_LIST.add(new PICC_XM_testSign());
        Main.main(new String[]{"-c", "/home/hjx/work/myProject/transmit/file/config.json"});
    }

}