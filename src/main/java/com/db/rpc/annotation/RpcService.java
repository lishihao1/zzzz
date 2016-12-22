package com.db.rpc.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * ָ��Զ�̽ӿ�ע�� 
 * @author dengbo
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component // �����ɱ� Spring ɨ��
public @interface RpcService {
	Class<?> cls();
}
