package com.raggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/*return type
*
*/
@Data
public class R<T> implements Serializable {

    private Integer code; //1:success，other: fail

    private String msg; //error message

    private T data; //other data

    private Map map = new HashMap(); //动态数据

    public static <T> R<T> success(T object) { //new a Result class and set success code to 1
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {//new a Result class and set success code to 0
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
