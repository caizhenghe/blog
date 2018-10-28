# Player

[TOC]

## 基本概念

### 码流、帧率和分辨率

// FIXME

码流一般是1Mbps，即128KBps，帧率假如是25fps，则平均一帧大约5KB。通常一个I帧大约是100KB（yuv数据，通过分辨率进行计算），而P帧会比I帧小很多。Demux时一次性解500个Packet，500 * 188 = 90KB，几乎相当于一个I帧，因此是够用的

帧率和分辨率确定的情况下，码率越高成像质量越高；帧率和码率确定的情况下（每帧的数据量大小固定，一般不会这么去控制变量），分辨率越高图片越清晰，分辨率越低色彩越细腻（每个像素内的色彩值更丰富）；码率和分辨率固定的情况下（一般不会这样控制变量），帧率越高连续性越好，但成像越差，帧率越低连续性越差，但成像越好。

## 数据流程

![player_process](C:/blog/player/src/%E6%92%AD%E6%94%BE%E5%99%A8%E6%95%B0%E6%8D%AE%E6%B5%81.jpg)

> 播放：[TS]->Demux:[H264,H265]->Decode:[YUV,PCM]->Render
>
> 录像（转码）：[TS,MP4]->Demux:[H265]->Decode:[YUV,PCM]->Encode:[H264]->Mux:[TS,MP4]

## 数据格式

### TS

1个Packet：188Byte

PAT、PMT

### H264

NAL unit

SPS、PPS、I帧

PTS间隔：9000（TimeScale）=1s

### H265

IDR、APS

### YUV

// FIXME

LineSize表示一行中y的个数，LineSize * height 是y分量所占内存大小（Byte）。在420的yuv数据中，每四个像素公用一个u、v，所以他们的内存大小是：LineSize * height / 4。

## 容器

### SYSBuffer

Size：188*2048（2048个packet）

### ESBuffer

Video：4000 * 1024

Audio：400 * 1024

MaxFrame：128

iIndexHead：第一帧的索引

iIndexTail：最后一帧的下一帧的索引

SpilitFrame：在最后一帧的iPos处追加一个帧（如果iPos是最后一帧的末尾，则它的作用是告诉别人这一帧已经填满）

### AVFrameQueue

## Demux

一次Demux500个packet

## Decode

## AVSync

1. MapSyncInfo：记录pStartPTS和pFirstPTS。其中pStartPTS是本次Sync的起始时间，pFirstPTS是整个文件的第一帧的时间。
2. BufferReady：计算从ESBuffer队尾帧到AVFrameQueue队头帧的PTS间隔，判断是否已经缓存了足够的数据，若是则向RC发送SET_RENDERMODE和EXTRA_INFO（PLAY_READY）消息，开始渲染操作。目的是避免出现音视频断断续续的情况（音频和视频其中有一个ready即认为已经ready）。
3. ExecuteAudioSync：同步音频。取出FrameQueue中第一个未Ready的音频帧。
   - 以音频为主，直接将该帧置为Ready；
   - 以系统时间为主，如果系统时间gap大于等于音频gap，将当前帧置为ready，如果系统时间gap大于（音频gap+500ms），继续同步下一帧音频。
4. ExecuteVideoSync：同步视频。取出FrameQueue中第一个未Ready的视频帧。
   - 以音频为主，计算正在播放的音频时间llAllowance。若当前视频帧的时间小于等于llAllowance，将这个帧置为Ready；若正在播放的视频时间小于llAllowance，则需要追赶，继续同步下一帧视频。
   - 以系统时间为主，如果系统时间gap大于等于视频gap，将当前帧置为ready（忽略fPlayRate）；如果系统时间gap大于（视频gap+500ms），继续同步下一帧视频。

## Encode

## Mux

缓存一个I帧：



## 相关技术

### dump数据

**dump ts流**

DataSource的execute的TOSTOP 

```c
FILE *fp = fopen("/mnt/sdcard/TEST.ts", "ab+");
if (fp != NULL)
{
    fwrite(MBUFFERSYSStartPos(&SysBuffer), 1, iBytes2Write, fp);
}
fclose(fp);
```

**dump h264/h265**

Render的DecodeIn，当bNeedAdvance时：

```c
FILE *fp = fopen("/mnt/sdcard/TEST.h264", "ab+");
if (fp != NULL)
{
    fwrite(MBUFFERESFirstFramePos(pESBuffer), 1, pESBuffer->piTail[pESBuffer->iIndexHead] - pESBuffer->piHead[pESBuffer->iIndexHead], fp);
}
fclose(fp);
```

**dump yuv**

TPPlayerDecoderFFMpeg的DecodeOut中：

```c
FILE *pFile = fopen("/mnt/sdcard/YUV", "ab+");
int iHeight[3] = { pFrame->uiHeight, pFrame->uiHeight / 2, pFrame->uiHeight / 2 };
for (int i = 0; i < 3; i++)
{
    if (pFrame->pFrame->linesize[i] > 0)
        fwrite(pFrame->pFrame->data[i], 1, iHeight[i] * pFrame->pFrame->linesize[i], pFile);
}
fclose(pFile);
```



### h264分析工具