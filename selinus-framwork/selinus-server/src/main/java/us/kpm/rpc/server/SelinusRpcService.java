package us.kpm.rpc.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: SelinusRpcService
 * PackageName: us.kpm.rpc.server
 *
 * @author: luffych
 * @version: 1.0
 * Filename:    SelinusRpcService.java
 * Create at:  2019/2/25
 * Copyright:   Copyright (c)2019
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SelinusRpcService {

    public Class<?> value();

}
