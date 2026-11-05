/* Foundation native regression probe; test-only, never packaged in the app. */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libavfilter/buffersrc.h>
#include <libavfilter/buffersink.h>
#include <libavutil/adler32.h>
#include <libavutil/imgutils.h>
#include <libavutil/samplefmt.h>

static int consume(AVCodecContext *decoder, AVFrame *frame, unsigned long *hash,
                   int *frames, long long *samples) {
    int result;
    while ((result = avcodec_receive_frame(decoder, frame)) >= 0) {
        (*frames)++;
        if (decoder->codec_type == AVMEDIA_TYPE_VIDEO) {
            int size = av_image_get_buffer_size(frame->format, frame->width, frame->height, 1);
            if (size <= 0) return -1;
            unsigned char *buffer = av_malloc(size);
            if (!buffer) return -1;
            int used = av_image_copy_to_buffer(buffer, size, (const uint8_t * const *)frame->data,
                                               frame->linesize, frame->format, frame->width, frame->height, 1);
            if (used < 0) { av_free(buffer); return -1; }
            *hash = av_adler32_update(*hash, buffer, used);
            av_free(buffer);
        } else {
            int channels = frame->ch_layout.nb_channels;
            int planar = av_sample_fmt_is_planar(frame->format);
            int planes = planar ? channels : 1;
            int size = frame->nb_samples * av_get_bytes_per_sample(frame->format) * (planar ? 1 : channels);
            if (size <= 0) return -1;
            for (int i = 0; i < planes; i++) *hash = av_adler32_update(*hash, frame->extended_data[i], size);
            *samples += frame->nb_samples;
        }
        av_frame_unref(frame);
    }
    return result == AVERROR(EAGAIN) || result == AVERROR_EOF ? 0 : result;
}

static int decode(const char *path, const char *override) {
    AVFormatContext *format = NULL;
    AVCodecContext **decoders = NULL;
    AVPacket *packet = av_packet_alloc();
    AVFrame *frame = av_frame_alloc();
    int result = avformat_open_input(&format, path, NULL, NULL);
    if (result < 0 || avformat_find_stream_info(format, NULL) < 0) return 1;
    decoders = av_calloc(format->nb_streams, sizeof(*decoders));
    unsigned long *hashes = av_calloc(format->nb_streams, sizeof(*hashes));
    int *counts = av_calloc(format->nb_streams, sizeof(*counts));
    long long *samples = av_calloc(format->nb_streams, sizeof(*samples));
    int opened = 0;
    for (unsigned i = 0; i < format->nb_streams; i++) {
        AVCodecParameters *params = format->streams[i]->codecpar;
        if (params->codec_type != AVMEDIA_TYPE_AUDIO && params->codec_type != AVMEDIA_TYPE_VIDEO) continue;
        const AVCodec *codec = override ? avcodec_find_decoder_by_name(override) : avcodec_find_decoder(params->codec_id);
        if (!codec || codec->id != params->codec_id) return 2;
        decoders[i] = avcodec_alloc_context3(codec);
        if (!decoders[i] || avcodec_parameters_to_context(decoders[i], params) < 0) return 3;
        decoders[i]->thread_count = 1;
        if (avcodec_open2(decoders[i], codec, NULL) < 0) return 4;
        hashes[i] = 1;
        opened++;
    }
    if (!opened) return 5;
    while ((result = av_read_frame(format, packet)) >= 0) {
        unsigned i = packet->stream_index;
        if (decoders[i]) {
            if (avcodec_send_packet(decoders[i], packet) < 0 || consume(decoders[i], frame, &hashes[i], &counts[i], &samples[i]) < 0) return 6;
        }
        av_packet_unref(packet);
    }
    if (result != AVERROR_EOF) return 7;
    for (unsigned i = 0; i < format->nb_streams; i++) {
        if (!decoders[i]) continue;
        if (avcodec_send_packet(decoders[i], NULL) < 0 || consume(decoders[i], frame, &hashes[i], &counts[i], &samples[i]) < 0) return 8;
        if (!counts[i]) return 9;
        printf("decoded\t%u\t%s\t%d\t%lld\t%08lx\n", i, decoders[i]->codec->name, counts[i], samples[i], hashes[i]);
        avcodec_free_context(&decoders[i]);
    }
    av_packet_free(&packet); av_frame_free(&frame); avformat_close_input(&format);
    av_free(decoders); av_free(hashes); av_free(counts); av_free(samples);
    return 0;
}


/* Exercise the inherited runtime-tempo path and both AVOS extension exports. */
extern void avfilter_atempo_get_state_v2(AVFilterContext *, int *, int64_t *, int64_t *, int64_t *, int64_t *, double *, int64_t *);
static int tempo_test(void) {
    AVFilterGraph *graph = avfilter_graph_alloc();
    AVFrame *input = av_frame_alloc(), *output = av_frame_alloc();
    AVFilterContext *source = NULL, *tempo = NULL, *sink = NULL;
    unsigned long hash = 1;
    long long samples = 0;
    const char *spec = "abuffer=time_base=1/48000:sample_rate=48000:sample_fmt=flt:channel_layout=mono,atempo@tempo=1.25,abuffersink";
    if (avfilter_graph_parse_ptr(graph, spec, NULL, NULL, NULL) < 0 || avfilter_graph_config(graph, NULL) < 0) return 10;
    for (unsigned i = 0; i < graph->nb_filters; i++) {
        AVFilterContext *f = graph->filters[i];
        if (!strcmp(f->filter->name, "abuffer")) source = f;
        if (!strcmp(f->filter->name, "atempo")) tempo = f;
        if (!strcmp(f->filter->name, "abuffersink")) sink = f;
    }
    if (!source || !tempo || !sink) return 11;
    for (int index = 0; index <= 100; index++) {
        if (index == 50 && avfilter_process_command(tempo, "tempo", "0.75", NULL, 0, 0) < 0) return 12;
        if (index == 75 && avfilter_process_command(tempo, "tempo", "2.0", NULL, 0, 0) < 0) return 13;
        if (index < 100) {
            input->format = AV_SAMPLE_FMT_FLT; input->sample_rate = 48000;
            av_channel_layout_default(&input->ch_layout, 1);
            input->nb_samples = 480; input->pts = index * 480;
            if (av_frame_get_buffer(input, 0) < 0) return 14;
            float *data = (float *)input->data[0];
            for (int i = 0; i < 480; i++) data[i] = ((index * 480 + i) * 13 % 257 - 128) / 256.0f;
            if (av_buffersrc_add_frame_flags(source, input, AV_BUFFERSRC_FLAG_KEEP_REF) < 0) return 15;
            av_frame_unref(input);
        } else if (av_buffersrc_add_frame_flags(source, NULL, 0) < 0) return 16;
        int result;
        while ((result = av_buffersink_get_frame(sink, output)) >= 0) {
            hash = av_adler32_update(hash, output->data[0], output->nb_samples * sizeof(float));
            samples += output->nb_samples;
            av_frame_unref(output);
        }
        if (result != AVERROR(EAGAIN) && result != AVERROR_EOF) return 17;
        int ring; int64_t in, out, ns_in, ns_out, media; double speed;
        avfilter_atempo_get_state_v2(tempo, &ring, &in, &out, &ns_in, &ns_out, &speed, &media);
        printf("atempo-state\t%d\t%d\t%lld\t%lld\t%lld\t%lld\t%.2f\t%lld\n", index, ring,
               (long long)in, (long long)out, (long long)ns_in, (long long)ns_out, speed, (long long)media);
    }
    if (!samples) return 18;
    printf("atempo-output\t%lld\t%08lx\n", samples, hash);
    av_frame_free(&input); av_frame_free(&output); avfilter_graph_free(&graph);
    return 0;
}

int main(int argc, char **argv) {
    av_log_set_level(AV_LOG_ERROR);
    if (argc > 1 && !strcmp(argv[1], "--atempo")) return tempo_test();
    if (argc > 1) return decode(argv[1], argc > 2 ? argv[2] : NULL);
    printf("version\t%s\nlicence\t%s\n", av_version_info(), avutil_license());
    void *state = NULL;
    const AVCodec *codec;
    while ((codec = av_codec_iterate(&state))) printf("codec\t%s\t%d\t%d\t%d\n", codec->name, codec->id, av_codec_is_decoder(codec), av_codec_is_encoder(codec));
    state = NULL;
    const AVInputFormat *input;
    while ((input = av_demuxer_iterate(&state))) printf("demuxer\t%s\n", input->name);
    state = NULL;
    const AVOutputFormat *output;
    while ((output = av_muxer_iterate(&state))) printf("muxer\t%s\n", output->name);
    state = NULL;
    const AVFilter *filter;
    while ((filter = av_filter_iterate(&state))) printf("filter\t%s\n", filter->name);
    const char *protocol;
    state = NULL;
    while ((protocol = avio_enum_protocols(&state, 0))) printf("input-protocol\t%s\n", protocol);
    state = NULL;
    while ((protocol = avio_enum_protocols(&state, 1))) printf("output-protocol\t%s\n", protocol);
    return 0;
}
