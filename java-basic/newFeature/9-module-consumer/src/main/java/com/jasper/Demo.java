package com.jasper;

import com.jasper.api.OpenUtil;
//import com.jasper.internel.CloseUtil;
// java: package com.jasper.internel is not visible
//  (package com.jasper.internel is declared in module producer, which does not export it)

public class Demo {
    public static void main(String[] args) {
        OpenUtil.print();
//        CloseUtil.print();
    }
}