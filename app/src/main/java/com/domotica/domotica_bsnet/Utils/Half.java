package com.domotica.domotica_bsnet.Utils;

public class Half {
    private static final int FP16_SIGN_MASK         = 0x8000;
    private static final int FP16_EXPONENT_SHIFT    = 10;
    private static final int FP16_EXPONENT_MASK     = 0x1f;
    private static final int FP16_SIGNIFICAND_MASK  = 0x3ff;
    private static final int FP16_EXPONENT_BIAS     = 15;

    private static final int FP32_EXPONENT_BIAS     = 127;
    private static final int FP32_EXPONENT_SHIFT    = 23;

    private static final int FP32_DENORMAL_MAGIC = 126 << 23;
    private static final float FP32_DENORMAL_FLOAT = Float.intBitsToFloat(FP32_DENORMAL_MAGIC);

    public static float toFloat(short h) {
        int bits = h & 0xffff;
        int s = bits & FP16_SIGN_MASK;
        int e = (bits >>> FP16_EXPONENT_SHIFT) & FP16_EXPONENT_MASK;
        int m = (bits                        ) & FP16_SIGNIFICAND_MASK;
        int outE = 0;
        int outM = 0;
        if (e == 0) { // Denormal or 0
            if (m != 0) {
                // Convert denorm fp16 into normalized fp32
                float o = Float.intBitsToFloat(FP32_DENORMAL_MAGIC + m);
                o -= FP32_DENORMAL_FLOAT;
                return s == 0 ? o : -o;
            }
        } else {
            outM = m << 13;
            if (e == 0x1f) { // Infinite or NaN
                outE = 0xff;
            } else {
                outE = e - FP16_EXPONENT_BIAS + FP32_EXPONENT_BIAS;
            }
        }
        int out = (s << 16) | (outE << FP32_EXPONENT_SHIFT) | outM;
        return Float.intBitsToFloat(out);
    }

}
