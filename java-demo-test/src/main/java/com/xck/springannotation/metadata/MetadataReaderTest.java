package com.xck.springannotation.metadata;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

/**
 * @Classname MetadataReaderTest
 * @Description TODO
 * @Date 2021/1/24 18:44
 * @Created by xck503c
 */
public class MetadataReaderTest {

    public static void main(String[] args) throws Exception{
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader("com.xck.springannotation.Man");

        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        System.out.println(annotationMetadata.getAnnotationTypes());
        System.out.println(annotationMetadata.getAnnotationAttributes("org.springframework.stereotype.Service"));
        System.out.println(annotationMetadata.getMetaAnnotationTypes("org.springframework.stereotype.Service"));
        System.out.println(annotationMetadata.isConcrete());
    }
}
