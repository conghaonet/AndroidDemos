package com.app2m.demo.mymp4parser;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.XmlBox;
import com.googlecode.mp4parser.util.Path;

import java.io.*;
//import com.googlecode.mp4parser.FileChannel;


public class Mp4ParserPerformance extends Activity {
    String mp4File = Environment.getExternalStorageDirectory().getPath() + File.separator + "vid.mp4";

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Debug.startMethodTracing();
//        Debug.startAllocCounting();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView tv = (TextView) findViewById(R.id.text);
        String text = "";

        File sdCard = Environment.getExternalStorageDirectory();
        for (int i = 0; i < 100; i++) {
            try {
//                FileInputStream fileInputStream = new FileInputStream(new File(sdCard, "Movies/urndeceapidIMDBtt04721814LKQIvbjJF72ODtC.uvu"));
//                IsoFile isoFile = new IsoFile(fileInputStream.getChannel());
                IsoFile isoFile = new IsoFile(mp4File);
                //isoFile = new IsoFile(Channels.newChannel(new FileInputStream(this.filePath)));
                //Path path = new Path(isoFile);
                XmlBox xmlBox = (XmlBox) Path.getPath(isoFile, "/moov/meta/xml ");
                String xml = xmlBox.getXml();
                //System.err.println(xml;
            } catch (IOException e) {

            }
        }
    }

/*
    private static double correctTimeToNextSyncSample(Track track, double cutHere) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
		for (TimeToSampleBox.Entry entry : track.getDecodingTimeEntries()) {
            for (int j = 0; j < entry.getCount(); j++) {
                if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                    // samples always start with 1 but we start with zero therefore +1
                    timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
                }
                currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
        }
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                return timeOfSyncSample;
            }
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }
*/

}
