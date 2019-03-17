# HTTP

| 版本/状态 | 责任人 | 起止日期   | 备注                               |
| --------- | ------ | ---------- | ---------------------------------- |
| V1.0/草稿 | 蔡政和 | 2019/03/17 | 创建http文档                       |
| V1.0/草稿 | 蔡政和 | 2019/03/17 | 新增HTTP/1.0，HTTP/1.1，HTTP/2章节 |

## 版本更迭

### HTTP/0.9

http是基于tcp/ip的应用层协议，不涉及数据包传输，主要规定了客户端和服务器之间的通信格式，默认使用80端口。

1991年发布了0.9版本，该版本只有一个`GET`命令。

```http
GET /index.html
```

该命令表示，TCP连接建立后，客户端向服务器请求网页index.html。

协议规定，服务器只能返回HTML格式的字符串，不能回应其他格式。

```html
<html>
  <body>Hello World</body>
</html>
```

服务器发送完毕，就关闭TCP连接。

### HTTP/1.0

#### 简介

1996年5月，HTTP/1.0发布。在原来的基础上，任何格式的内容都可以发送，包括文字、图像、视频、二进制文件。

除了`GET`命令，还新增`POST`和`HEAD`命令。

HTTP请求和回应的格式也变了。除了数据部分，新增HTTP Header，用于描述元数据。

其它新增功能还包括状态码、多字符集支持、多部分发送（multi-part type）、权限、缓存、内容编码。

#### 请求格式

```http
GET / HTTP/1.0
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5)
Accept: */*
```

#### 回应格式

```http
HTTP/1.0 200 OK 
Content-Type: text/plain
Content-Length: 137582
Expires: Thu, 05 Dec 1997 16:00:00 GMT
Last-Modified: Wed, 5 August 1996 15:55:28 GMT
Server: Apache 0.84

<html>
  <body>Hello World</body>
</html>
```

回应的格式是"头信息 + 一个空行（`\r\n`） + 数据"。其中，第一行是"协议版本 + 状态码（status code） + 状态描述"。

#### Content-Type

1.0版本规定，头信息必须是ASCII码，数据可以是任意格式。因此，服务器回应的时候，必须告诉客户端是什么格式的数据，下面是一些常见的值：

```
text/plain
text/html
text/css
image/jpeg
image/png
image/svg+xml
audio/mp4
video/mp4
application/javascript
application/pdf
application/zip
application/atom+xml
```

这些数据类型总称为`MIME Type`，每个值包括一级类型和二级类型，用斜杠分隔。

厂商也可以自定义类型：

```
application/vnd.debian.binary-package
```

上面的类型表明，发送的是Debian系统的二进制数据包。

`MIME Type`还可以在尾部使用分号，添加参数。

```
Content-Type: text/html; charset=utf-8
```

上面的类型表明，发送的是网页，编码格式是UTF-8

客户端请求时，可以使用`Accept`字段声明自己可以接受哪些数据格式。

```
Accept: */*
```

上面代码中，客户端声明自己可以接受任何格式的数据。

`MIME Type`不仅用在HTTP协议，还可以用在其他地方，比如网页：

```html
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<!-- 等同于 -->
<meta charset="utf-8" /> 
```

#### Content-Encoding

由于可以把数据定义成任何格式，因此可以将数据压缩后再发送，`Content-Encoding`声明了数据的压缩格式：

```
Content-Encoding: gzip
Content-Encoding: compress
Content-Encoding: deflate
```

客户端请求时，使用`Accept-Encoding`指明自己支持哪种压缩格式：

```
Accept-Encoding: gzip, deflate
```

#### 缺点

每个TCP连接只能发送一个请求。发送数据完毕，连接就关闭，如果还要请求其他资源，就必须再新建一个连接。

TCP连接的新建成本很高，因为需要客户端和服务器三次握手，并且开始时发送速率较慢（slow start）。

为了解决该问题，客户端在请求时，会使用一个非标准的`Connection`字段：

```
Connection: keep-alive
```

这个字段要求服务器不要关闭TCP连接，以便其他请求复用，服务器同样回应这个字段：

```
Connection: keep-alive
```

但是这不是标准字段，不是根本的解决办法。

### HTTP/1.1

1997年1月，HTTP/1.1 版本发布，只比 1.0 版本晚了半年。

#### 持久连接

TCP连接默认不关闭，可以被多个请求复用。

若想要关闭连接，客户端在最后一个请求时发送：

```
Connection: close
```

目前，对于同一个域名，大多数浏览器允许同时建立6个持久连接。

#### 管道机制

1.1版本引入了管道机制。即在同一个TCP连接中，客户端可以同时发送多个请求（实际上依然有个先后顺序，只是客户端不需要等服务器回应就可以发送下一个请求）。

假如客户端想同时请求2个资源。以往的做法是先请求A，等服务器响应后再请求B；现在则可以同时请求A、B，但服务器依然是按照顺序，先回应A，再回应B。

#### Content-Length

一个TCP连接可以传送多个回应，势必要有一种机制，区分数据包是属于哪个回应。这就是`Content-Length`的作用，声明本次回应的数据长度：

```
Content-Length: 3495
```

在1.0版本，`Content-Length`不是必需的，因为浏览器发现服务器关闭了TCP连接，就表明收到的数据包已经全了。

#### 分块传输编码

使用`Content-Length`的前提是，服务器发送回应之前，需要知道数据报的长度。

对于一些耗时的动态操作（比如下载图片），服务器需要等待所有操作完毕后才发送数据，效率十分低下。更好的做法是，产生一块数据就发送一块，使用“流模式”（stream）代替“缓存模式”（buffer）。

因此1.1规定可以不使用`Content-Length`字段，而使用分块传输编码（chunked transfer encoding）。只要返回的头信息中带有`Transfer-Encoding`字段，就表明回应是由数量未定的数据块组成的。

```
Transfer-Encoding: chunked
```

在每个非空的数据块之前，有个16进制的数值，表示这个块的长度。最后是一个大小为0的数据块，表示本次回应的数据块发送完毕：

```http
HTTP/1.1 200 OK
Content-Type: text/plain
Transfer-Encoding: chunked

25
This is the data in the first chunk

1C
and this is the second one

3
con

8
sequence

0
```

#### 其它功能

1.1版还新增了许多动词方法：`PUT`、`PATCH`、`HEAD`、 `OPTIONS`、`DELETE`。
另外，客户端请求的头信息新增了`Host`字段，用来指定服务器的域名。

```
Host: www.example.com
```

有了`Host`字段，就可以将请求发往同一台服务器上的不同网站，为虚拟主机的兴起打下了基础。

#### 缺点

虽然1.1版本允许复用TCP连接，但是在同一个连接中，服务器必须**按照顺序**处理客户端的请求。要是前面的回应特别慢，后面的请求就会被阻塞住，这称为“队头阻塞”。

为了避免这个问题，只有两个方法：一是**减少请求次数**，二是**同时多开持久连接**。这导致了很多的网页优化技巧，比如合并脚本和样式表、将图片嵌入CSS代码、域名分片（domain sharding）等等。

### HTTP/2

2015年，HTTP/2 发布。

#### 二进制协议

HTTP/1.1 版的头信息肯定是文本（ASCII编码），数据体可以是文本，也可以是二进制。HTTP/2 则是一个彻底的二进制协议，头信息和数据体都是二进制，并且统称为"帧"（frame）：头信息帧和数据帧。

二进制协议的一个好处是，可以定义额外的帧。HTTP/2 定义了近十种帧，为将来的高级应用打好了基础。如果使用文本实现这种功能，解析数据将会变得非常麻烦，二进制解析则方便得多。

#### 多工

HTTP/2 复用TCP连接，在一个TCP连接里面，服务器同时收到了A请求和B请求，可以不按照顺序返回，谁先处理完毕就先回应谁。这样就避免了"队头堵塞"。

这样双向的、实时的通信，就叫做多工（Multiplexing）。

#### 数据流

因为 HTTP/2 的数据包是不按顺序发送的，同一个连接里面连续的数据包，可能属于不同的回应。因此，必须要对数据包做标记，指出它属于哪个回应。

HTTP/2 将每个请求或回应的所有数据包，称为一个数据流（stream）。每个数据流都有一个独一无二的编号。数据包发送的时候，都必须标记数据流ID，用来区分它属于哪个数据流。另外还规定，客户端发出的数据流，ID一律为奇数，服务器发出的，ID为偶数。

数据流发送到一半的时候，客户端和服务器都可以发送信号（`RST_STREAM`帧），取消这个数据流。1.1版取消数据流的唯一方法，就是关闭TCP连接。这就是说，HTTP/2 可以取消某一次请求，同时保证TCP连接还打开着，可以被其他请求使用。

客户端还可以指定数据流的优先级。优先级越高，服务器就会越早回应。

#### 头信息压缩

HTTP 协议不带有状态，每次请求都必须附上所有信息。所以，请求的很多字段都是重复的，比如`Cookie`和`User Agent`，一模一样的内容，每次请求都必须附带，这会浪费很多带宽，也影响速度。

HTTP/2 对这一点做了优化，引入了头信息压缩机制（header compression）。一方面，头信息使用`gzip`或`compress`压缩后再发送；另一方面，客户端和服务器同时维护一张头信息表，所有字段都会存入这个表，生成一个索引号，以后就不发送同样字段了，只发送索引号，这样就提高速度了。

#### 服务器推送

HTTP/2 允许服务器未经请求，主动向客户端发送资源，这叫做服务器推送（server push）。

常见场景是客户端请求一个网页，这个网页里面包含很多静态资源。正常情况下，客户端必须收到网页后，解析HTML源码，发现有静态资源，再发出静态资源请求。其实，服务器可以预期到客户端请求网页后，很可能会再请求静态资源，所以就主动把这些静态资源随着网页一起发给客户端了。