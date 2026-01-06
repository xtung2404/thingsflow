package com.example.thingflowsdk.core.base.define;

import android.content.Context;

import java.util.ArrayList;

import rogo.iot.module.flowcommon.type.FInputValueType;
import rogo.iot.module.platform.define.IoTCondition;

public class TFComparision extends IoTCondition {
    public static final int DIFF = -1;

    public static int[] getComparionTypes(Context context, int type) {
        // Sử dụng một List linh động để thêm các phần tử
        ArrayList<Integer> typeList = new ArrayList<>();

        // Luôn thêm hai giá trị này vào
        typeList.add(EQUAL);
        typeList.add(DIFF);

        // Thêm các giá trị khác dựa trên điều kiện
        if (type != FInputValueType.STRING) {
            typeList.add(BETWEEN);
            typeList.add(LESS_THAN);
            typeList.add(LESS_EQUAL);
            typeList.add(GREATER_THAN);
            typeList.add(GREATER_EQUAL);
        }

        // Chuyển ArrayList<Integer> thành một mảng int[] để trả về
        int[] result = new int[typeList.size()];
        for (int i = 0; i < typeList.size(); i++) {
            result[i] = typeList.get(i);
        }

        return result;
    }
}
