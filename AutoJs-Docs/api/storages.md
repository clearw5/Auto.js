# Storages

> Stability: 2 - Stable

storages模块提供了保存简单数据、用户配置等的支持。保存的数据除非应用被卸载或者被主动删除，否则会一直保留。

storages支持`number`, `boolean`, `string`等数据类型以及把`Object`, `Array`用`JSON.stringify`序列化存取。

storages保存的数据在脚本之间是共享的，任何脚本只要知道storage名称便可以获取到相应的数据，因此它不能用于敏感数据的储存。
storages无法像Web开发中LocalStorage一样提供根据域名独立的存储，因为脚本的路径随时可能改变。

## storages.create(name)
* `name` {string} 本地存储名称

创建一个本地存储并返回一个`Storage`对象。不同名称的本地存储的数据是隔开的，而相同名称的本地存储的数据是共享的。

例如在一个脚本中，创建名称为ABC的存储并存入a=123:
```
var storage = storages.create("ABC");
storage.put("a", 123);
```
而在另一个脚本中是可以获取到ABC以及a的值的：
```
var storage = storages.create("ABC");
log("a = " + storage.get("a"));
```

因此，本地存储的名称比较重要，尽量使用含有域名、作者邮箱等唯一信息的名称来避免冲突，例如：
```
var storage = storages.create("2732014414@qq.com:ABC");
```

## storages.remove(name)
* `name` {string} 本地存储名称

删除一个本地存储以及他的全部数据。如果该存储不存在，返回false；否则返回true。

# Storages

## Storage.get(key[, defaultValue])
* `key` {string} 键值
* `defaultValue` {any} 可选，默认值

从本地存储中取出键值为key的数据并返回。

如果该存储中不包含该数据，这时若指定了默认值参数则返回默认值，否则返回undefined。

返回的数据可能是任意数据类型，这取决于使用`Storage.put`保存该键值的数据时的数据类型。

## Storage.put(key, value)
* `key` {string} 键值
* `value` {any} 值

把值value保存到本地存储中。value可以是undefined以外的任意数据类型。如果value为undefined则抛出TypeError。

存储的过程实际上是使用JSON.stringify把value转换为字符串再保存，因此value必须是可JSON化的才能被接受。

## Storage.remove(key)
* `key` {string} 键值

移除键值为key的数据。不返回任何值。

## Storage.contains(key)
* `key` {string} 键值

返回该本地存储是否包含键值为key的数据。是则返回true，否则返回false。

## Storage.clear()

移除该本地存储的所有数据。不返回任何值。