package com.shawckz.ipractice.database.mongo.annotations;


import com.shawckz.ipractice.configuration.AbstractSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface DatabaseSerializer {

    Class<? extends AbstractSerializer> serializer();

}
