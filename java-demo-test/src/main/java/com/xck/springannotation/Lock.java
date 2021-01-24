package com.xck.springannotation;

import java.lang.annotation.*;

/**
 * @Classname Lock
 * @Description TODO
 * @Date 2021/1/24 18:54
 * @Created by xck503c
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Lock {
}
