package com.xck.springannotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @Classname Man
 * @Description TODO
 * @Date 2021/1/24 17:35
 * @Created by xck503c
 */
@Scope("prototype")
@Service
@Lock
public class Man {

    @Autowired
    public Name name;

    @Service
    private class Pep{

    }
}
