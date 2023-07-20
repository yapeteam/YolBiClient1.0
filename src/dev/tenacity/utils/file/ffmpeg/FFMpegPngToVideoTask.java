package dev.tenacity.utils.file.ffmpeg;

import org.mountcloud.ffmepg.task.bean.FFTask;

public class FFMpegPngToVideoTask extends FFTask<FFMpegPngToVideo> {
    private FFMpegPngToVideoResult data;

    public FFMpegPngToVideoTask(FFMpegPngToVideo operation) {
        super(operation);
    }

    public FFMpegPngToVideoTask(FFMpegPngToVideoResult result, FFMpegPngToVideo operation) {
        super(operation);
        this.data = result;
    }

    @Override
    public void callExecStart() {

    }

    @Override
    public void callRsultLine(String s) {
        System.out.println(s);
    }

    @Override
    public void callExecEnd() {
        if (this.data == null) {
            this.data = new FFMpegPngToVideoResult(this.result.toString());
        }
        System.out.println(result.toString());
    }
}
