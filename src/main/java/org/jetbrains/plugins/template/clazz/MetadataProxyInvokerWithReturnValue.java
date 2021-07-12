package org.jetbrains.plugins.template.clazz;

public interface MetadataProxyInvokerWithReturnValue<T> {
  T invoke(MetadataProxy delegate);
}
