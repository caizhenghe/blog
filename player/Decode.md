# Decode

## SoftDecode

```c++
//Output FFmpeg's av_log()
void custom_log(void *ptr, int level, const char *fmt, va_list vl) {
    FILE *fp = fopen("/storage/emulated/0/av_log.txt", "a+");
    if (fp) {
        vfprintf(fp, fmt, vl);
        fflush(fp);
        fclose(fp);
    }
}

JNIEXPORT jint JNICALL Java_com_czh_ffmpeg_mediakit_MediaKit_decodeNative
        (JNIEnv *env, jobject obj, jstring input_jstr, jstring output_jstr) {
    AVFormatContext *pFormatCtx;
    int i, videoindex;
    AVCodecContext *pCodecCtx;
    AVCodec *pCodec;
    // 定义两个Frame的用意是什么
    AVFrame *pFrame, *pFrameYUV;
    uint8_t *out_buffer;
    AVPacket *packet;
    int y_size;
    int ret, got_picture;
    struct SwsContext *img_convert_ctx;
    FILE *fp_yuv;
    int frame_cnt;
    clock_t time_start, time_finish;
    double time_duration = 0.0;

    char input_str[500] = {0};
    char output_str[500] = {0};
    char info[1000] = {0};
    sprintf(input_str, "%s", env->GetStringUTFChars(input_jstr, NULL));
    sprintf(output_str, "%s", env->GetStringUTFChars(output_jstr, NULL));

    //FFmpeg av_log() callback
    av_log_set_callback(custom_log);
    
	/* avformat.h*/
    // 准备所有资源
	av_register_all();
	// 初始化网络组件
    avformat_network_init();
    // 初始化AVFormatContext
    pFormatCtx = avformat_alloc_context();
	// 打开输入流，将格式信息写入pFormatCtx
	avformat_open_input(&pFormatCtx, input_str, NULL, NULL);
    // 查询流信息（判断pFormatCtx是否有效？）
    avformat_find_stream_info(pFormatCtx, NULL);

	/* AVFormatContext */
	// 获取视频流下标
	videoindex = -1;
    for (i = 0; i < pFormatCtx->nb_streams; i++)
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoindex = i;
            break;
        }
    // 获取AVCodecContext
    pCodecCtx = pFormatCtx->streams[videoindex]->codec;
    
    /* avcodec.h */
    // 获取AVCodec
    pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
    // 打开codec (判断codec是否有效?)
    avcodec_open2(pCodecCtx, pCodec, NULL);

    /* avframe.h */
	// 初始化AVFrame
    pFrame = av_frame_alloc();
    pFrameYUV = av_frame_alloc();
    
    /* mem.h */
    // 创建out_buffer
    out_buffer = (unsigned char *) av_malloc(
    /* imageutils.h */
    /* @brief 计算outbuffer的大小
	pCodecCtx->width: 表示画面的宽度(px)
    align: assumed linesize alignment，取1是什么含义？*/
            av_image_get_buffer_size(AV_PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height, 1));
	// 将out_buffer中的数据填充到pFrameYUV中?
    av_image_fill_arrays(pFrameYUV->data, pFrameYUV->linesize, out_buffer,
                         AV_PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height, 1);
    packet = (AVPacket *) av_malloc(sizeof(AVPacket));

	/* swscale.h */
	/* @brief 获取图像转换的Context
	SwsContext：software scale， 用于视频图像缩放和格式转换
	pCodecCtx->pix_fmt：转换前的格式（如h264），如果用户知晓则由用户指定，否则由demuxer或decoder指定。
	AV_PIX_FMT_YUV420P：转换后的格式。
	SWS_BICUBIC：转换算法。
	sws_scale各种算法对比分析： https://blog.csdn.net/leixiaohua1020/article/details/12029505 */
    img_convert_ctx = sws_getContext(pCodecCtx->width, pCodecCtx->height, pCodecCtx->pix_fmt,
                                     pCodecCtx->width, pCodecCtx->height, AV_PIX_FMT_YUV420P,
                                     SWS_BICUBIC, NULL, NULL, NULL);

    fp_yuv = fopen(output_str, "wb+");
    frame_cnt = 0;
	/* avformat.h */
    // 每次读取一帧（此处packet代表帧）
	while (av_read_frame(pFormatCtx, packet) >= 0) {
        if (packet->stream_index == videoindex) {
        	/* avcodec.h */
            // 对该帧进行解码，得到YUV数据（注意4字节和8字节对齐问题？）
            ret = avcodec_decode_video2(pCodecCtx, pFrame, &got_picture, packet);
            if (got_picture) {
            	/* swscale.h */
                // 图像转换（该步骤的作用是什么？）
                sws_scale(img_convert_ctx, (const uint8_t *const *) pFrame->data, pFrame->linesize, 0, pCodecCtx->height, pFrameYUV->data, pFrameYUV->linesize);
				// 分别将pFrameYUV->data中的y，u，v数据写到文件中
                y_size = pCodecCtx->width * pCodecCtx->height;
                fwrite(pFrameYUV->data[0], 1, y_size, fp_yuv);    //Y
                fwrite(pFrameYUV->data[1], 1, y_size / 4, fp_yuv);  //U
                fwrite(pFrameYUV->data[2], 1, y_size / 4, fp_yuv);  //V
                //Output info
                char pictype_str[10] = {0};
                switch (pFrame->pict_type) {
                    case AV_PICTURE_TYPE_I:
                        sprintf(pictype_str, "I");
                        break;
                    case AV_PICTURE_TYPE_P:
                        sprintf(pictype_str, "P");
                        break;
                    case AV_PICTURE_TYPE_B:
                        sprintf(pictype_str, "B");
                        break;
                    default:
                        sprintf(pictype_str, "Other");
                        break;
                }
                LOGI("Frame Index: %5d. Type:%s", frame_cnt, pictype_str);
                frame_cnt++;
            }
        }
        av_free_packet(packet);
    }
    
    //flush decoder，该步骤作用是什么？
    //FIX: Flush Frames remained in Codec
    while (1) {
        ret = avcodec_decode_video2(pCodecCtx, pFrame, &got_picture, packet);
        if (ret < 0)
            break;
        if (!got_picture)
            break;
        sws_scale(img_convert_ctx, (const uint8_t *const *) pFrame->data, pFrame->linesize, 0,
                  pCodecCtx->height,
                  pFrameYUV->data, pFrameYUV->linesize);
        int y_size = pCodecCtx->width * pCodecCtx->height;
        fwrite(pFrameYUV->data[0], 1, y_size, fp_yuv);    //Y
        fwrite(pFrameYUV->data[1], 1, y_size / 4, fp_yuv);  //U
        fwrite(pFrameYUV->data[2], 1, y_size / 4, fp_yuv);  //V
        //Output info
        char pictype_str[10] = {0};
        switch (pFrame->pict_type) {
            case AV_PICTURE_TYPE_I:
                sprintf(pictype_str, "I");
                break;
            case AV_PICTURE_TYPE_P:
                sprintf(pictype_str, "P");
                break;
            case AV_PICTURE_TYPE_B:
                sprintf(pictype_str, "B");
                break;
            default:
                sprintf(pictype_str, "Other");
                break;
        }
        LOGI("Frame Index: %5d. Type:%s", frame_cnt, pictype_str);
        frame_cnt++;
    }
    sws_freeContext(img_convert_ctx);
    fclose(fp_yuv);

    av_frame_free(&pFrameYUV);
    av_frame_free(&pFrame);
    avcodec_close(pCodecCtx);
    avformat_close_input(&pFormatCtx);

    return 0;
}

```



## HardDecode

TODO：使用avcodec_find_decoder_by_name(“h264_mediacodec”)获取Decoder

参考资料：https://blog.csdn.net/tifentan/article/details/80605472